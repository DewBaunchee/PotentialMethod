package by.varyvoda.matvey.potentialmethod

import by.varyvoda.matvey.potentialmethod.domain.Cluster
import by.varyvoda.matvey.potentialmethod.domain.PotentialMethod
import by.varyvoda.matvey.potentialmethod.domain.Sample
import by.varyvoda.matvey.potentialmethod.domain.Vector
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


const val cols = 3
const val rows = 5

val activeBackground = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
val nonActiveBackground = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
const val activeClass = "active"

class HelloController {

    @FXML
    private lateinit var grid: GridPane

    @FXML
    private lateinit var chart: BarChart<String, Double>

    private lateinit var potentialMethod: PotentialMethod

    private val sample: IntArray = IntArray(rows * cols) { 0 }

    private val bars = FXCollections.observableList(mutableListOf<XYChart.Series<String, Double>>())

    @FXML
    fun initialize() {
        for (col in 0 until cols) {
            for (row in 0 until rows) {
                val pane = Pane()
                pane.background = nonActiveBackground

                pane.styleProperty().addListener { _, _, new ->
                    pane.background = if (new == activeClass) activeBackground else nonActiveBackground
                    sample[row * cols + col] = if (new == activeClass) 1 else 0
                }

                val handler = EventHandler { mouseEvent: MouseEvent ->
                    pane.style = if (mouseEvent.isShiftDown) "" else activeClass
                    evaluateClasses()
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

        grid.sceneProperty().addListener { _, _, scene ->
            scene.setOnKeyPressed { keyEvent ->
                if (keyEvent.code == KeyCode.C && keyEvent.isShiftDown) {
                    grid.children.forEach { it.style = "" }
                }
            }
        }

        chart.data = bars

        createPerceptron()
    }

    private fun evaluateClasses() {
        bars.clear()
        bars.add(
            potentialMethod.evaluateClasses(Vector(values = sample))
                .entries.stream()
                .collect(
                    { XYChart.Series<String, Double>() },
                    { acc, entry -> acc.data.add(XYChart.Data(entry.key.toString(), entry.value)) },
                    { a, b -> a.data.addAll(b.data) }
                )
        )
    }

    private fun createPerceptron() {
        potentialMethod = PotentialMethod(getClusters()) { r -> 10.0 / (1.0 + r * r) }
    }

    private fun getClusters(): Map<Int, Cluster> {
        val samplesFolder = File("samples\\binary")
        if (!samplesFolder.exists()) throw FileNotFoundException("Cannot find samples folder.")

        val samples = samplesFolder.listFiles()!!.flatMap { folder ->
            if (!folder.exists()) listOf()
            else folder.listFiles()!!.map { file ->
                Sample(
                    Vector(values = file.readText()
                        .split(" ", "\r\n")
                        .map { it.toInt() }
                        .toIntArray()),
                    folder.name.toInt()
                )
            }
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
