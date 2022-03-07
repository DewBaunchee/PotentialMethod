package by.varyvoda.matvey.potentialmethod.domain.drawing

import by.varyvoda.matvey.potentialmethod.domain.Vector
import javafx.beans.property.SimpleIntegerProperty

class Plot(rows: Int, cols: Int) {

    val observablePixels = List(rows) { List(cols) { SimpleIntegerProperty() } }

    fun setPlot(plot: List<List<Int>>) {
        clear()
        for(row in plot.indices) {
            for(col in plot[row].indices) {
                observablePixels[row][col].value = plot[row][col]
            }
        }
    }

    fun getVector(): Vector {
        return Vector(
            values = observablePixels.stream()
                .collect(
                    { mutableListOf<Int>() },
                    { acc, row -> acc.addAll(row.map { it.value }) },
                    { left, right -> left.addAll(right) }
                ).toIntArray()
        )
    }

    fun getIntPlot(): List<List<Int>> {
        return observablePixels.map { row -> row.map { it.value } }
    }

    fun clear() {
        observablePixels.forEach { row ->
            row.forEach { pixel ->
                pixel.value = 0
            }
        }
    }
}