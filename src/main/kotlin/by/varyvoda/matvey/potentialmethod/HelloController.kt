package by.varyvoda.matvey.potentialmethod

import by.varyvoda.matvey.potentialmethod.domain.Vector
import by.varyvoda.matvey.potentialmethod.domain.drawing.Plot
import by.varyvoda.matvey.potentialmethod.domain.image.SampleImage
import by.varyvoda.matvey.potentialmethod.domain.learning.Cluster
import by.varyvoda.matvey.potentialmethod.domain.learning.PotentialMethod
import by.varyvoda.matvey.potentialmethod.domain.learning.Sample
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.chart.BarChart
import javafx.scene.chart.XYChart
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import java.io.File
import java.io.FileNotFoundException
import java.util.stream.IntStream
import kotlin.streams.toList


const val cols = 3 * 2
const val rows = 5 * cols / 3

val activeBackground = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
val nonActiveBackground = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))

class HelloController {

    @FXML
    private lateinit var grid: GridPane

    @FXML
    private lateinit var chart: BarChart<String, Double>

    private lateinit var potentialMethod: PotentialMethod

    private val plot = Plot(rows, cols)

    private val bars = FXCollections.observableList(mutableListOf<XYChart.Series<String, Double>>())

    private var filterPlotChanges = false

    @FXML
    fun initialize() {
        grid.onMouseReleased = EventHandler { evaluateClasses() }
        for (col in 0 until cols) {
            for (row in 0 until rows) {
                val pane = Pane()
                pane.background = nonActiveBackground

                plot.observablePixels[row][col].addListener { _, _, new ->
                    pane.background = if (new == 1) activeBackground else nonActiveBackground
                }

                val handler = EventHandler { mouseEvent: MouseEvent ->
                    plot.observablePixels[row][col].value = if (mouseEvent.isShiftDown) 0 else 1
                }
                pane.onMouseDragOver = handler
                pane.onMouseClicked = handler

                grid.widthProperty().addListener { _, _, new ->
                    val width = new.toDouble() / cols
                    pane.minWidth = width
                    pane.maxWidth = width
                }

                grid.heightProperty().addListener { _, _, new ->
                    val height = new.toDouble() / rows
                    pane.minHeight = height
                    pane.maxHeight = height
                }

                grid.add(pane, col, row)
            }
        }

        val plotChangeWrapper = { plotChanger: Runnable ->
            filterPlotChanges = true
            plotChanger.run()
            filterPlotChanges = false
            evaluateClasses()
        }

        grid.sceneProperty().addListener { _, _, scene ->
            scene.setOnKeyPressed { keyEvent ->
                if (!keyEvent.isShiftDown) return@setOnKeyPressed

                if (keyEvent.code == KeyCode.R) {
                    plotChangeWrapper {
                        plot.clear()
                    }
                }
                if (keyEvent.code == KeyCode.S) {
                    plotChangeWrapper {
                        plot.setPlot(SampleImage(plot.getIntPlot()).normalize(rows, cols).pixels)
                    }
                }
                if (keyEvent.code == KeyCode.D) {
                    plotChangeWrapper {
                        plot.setPlot(SampleImage(plot.getIntPlot()).normalize(5, 3).pixels)
                    }
                }
            }
        }

        chart.data = bars
        createMethod()
    }

    private fun evaluateClasses() {
        bars.clear()
        bars.add(
            potentialMethod.evaluateClasses(SampleImage(plot.getIntPlot()).normalize(rows, cols).getVector())
                .entries.stream()
                .collect(
                    { XYChart.Series<String, Double>() },
                    { acc, entry -> acc.data.add(XYChart.Data(entry.key.toString(), entry.value)) },
                    { a, b -> a.data.addAll(b.data) }
                )
        )
    }

    private fun createMethod() {
        potentialMethod = PotentialMethod(getClusters()) { r -> 10.0 / (1.0 + r * r) }
    }

    private fun getClusters(): Map<Int, Cluster> {
        val samplesFolder = File("samples\\img")
        if (!samplesFolder.exists()) throw FileNotFoundException("Cannot find samples folder.")

        var s = false
        val samples = samplesFolder.listFiles()!!.flatMap { folder ->
            if (!folder.exists()) listOf()
            else folder.listFiles()!!.asList()
                .parallelStream()
                .map { file ->
                    if (!s) {
                        s = true
                        plot.setPlot(SampleImage.fromFile(file).normalize(rows, cols).pixels)
                    }
                    Sample(SampleImage.fromFile(file).normalize(rows, cols).getVector(), folder.name.toInt())
                }.toList()
        }

        return samples.stream()
            .collect(
                {
                    IntStream.rangeClosed(0, 9)
                        .collect(
                            { HashMap<Int, MutableList<Vector>>() },
                            { acc, id -> acc[id] = mutableListOf() },
                            { left, right -> left.putAll(right) }
                        )
                },
                { acc, sample -> acc[sample.clusterId]!!.add(sample.vector) },
                { left, right -> left.putAll(right) }
            )
            .mapValues {
                Cluster(it.key, it.value.toList())
            }
    }
}
