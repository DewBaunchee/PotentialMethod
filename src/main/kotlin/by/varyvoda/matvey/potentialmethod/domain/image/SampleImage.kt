package by.varyvoda.matvey.potentialmethod.domain.image

import by.varyvoda.matvey.potentialmethod.domain.Scale
import by.varyvoda.matvey.potentialmethod.domain.Vector
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import javax.imageio.ImageIO

class SampleImage(val pixels: List<List<Int>>) {

    private val width = pixels.elementAtOrElse(0) { listOf() }.size
    private val height = pixels.size

    companion object {

        fun fromFile(file: File): SampleImage {
            return fromBufferedImage(ImageIO.read(file))
        }

        private fun fromBufferedImage(image: BufferedImage): SampleImage {
            val pixels = MutableList(image.height) { MutableList(image.width) { 0 } }
            for (row in pixels.indices) {
                for (col in pixels[row].indices) {
                    pixels[row][col] = if (image.getRGB(col, row) == -16777216) 1 else 0
                }
            }
            return SampleImage(pixels)
        }

        fun empty(height: Int, width: Int): SampleImage {
            return SampleImage(MutableList(height) { MutableList(width) { 0 } })
        }
    }

    private fun trim(): SampleImage {
        if (width == 0 || height == 0) return this

        val top = getTop(pixels)
        if (top == -1) return SampleImage(emptyList())
        val right = getRight(pixels)
        val bottom = getBottom(pixels)
        val left = getLeft(pixels)
        val trimmedPixels = MutableList(bottom - top + 1) { row ->
            MutableList(right - left + 1) { col ->
                pixels[top + row][left + col]
            }
        }
        return SampleImage(trimmedPixels)
    }

    private fun scaleTo(rows: Int, cols: Int): SampleImage {
        if (width == 0 || height == 0) return empty(rows, cols)

        val lowestPixel = max(rows, height) - 1
        val rowsScale = Scale(0, lowestPixel, 0, min(rows, height) - 1)
        val scaledPixels = MutableList(rows) { MutableList(cols) { 0 } }

        for (row in 0..lowestPixel) {
            val rightestPixel = max(cols, width) - 1
            val colsScale = Scale(0, rightestPixel, 0, min(cols, width) - 1)

            for (col in 0..rightestPixel) {
                scaledPixels[
                        if (rows - 1 == lowestPixel)
                            row
                        else
                            rowsScale.scale(row)
                ][
                        if (cols - 1 == rightestPixel)
                            col
                        else
                            colsScale.scale(col)
                ] += pixels[
                        if (height - 1 == lowestPixel)
                            row
                        else
                            rowsScale.scale(row)
                ][
                        if (width - 1 == rightestPixel)
                            col
                        else
                            colsScale.scale(col)
                ]
            }
        }

        return SampleImage(scaledPixels.map { row -> row.map { pixel -> max(0, min(pixel, 1)) } })
    }

    fun getVector(): Vector {
        return Vector(values = pixels.flatten().toIntArray())
    }

    fun normalize(rows: Int, cols: Int): SampleImage {
        return trim().scaleTo(rows, cols)
    }
}

private fun getTop(pixels: List<List<Int>>): Int {
    for (row in pixels.indices) {
        for (col in pixels[row].indices) {
            if (pixels[row][col] > 0) {
                return row
            }
        }
    }
    return -1
}

private fun getRight(pixels: List<List<Int>>): Int {
    for (col in pixels[0].indices.reversed()) {
        for (row in pixels.indices) {
            if (pixels[row][col] > 0) {
                return col
            }
        }
    }
    return -1
}

private fun getBottom(pixels: List<List<Int>>): Int {
    for (row in pixels.indices.reversed()) {
        for (col in pixels[row].indices) {
            if (pixels[row][col] > 0) {
                return row
            }
        }
    }
    return -1
}

private fun getLeft(pixels: List<List<Int>>): Int {
    for (col in pixels[0].indices) {
        for (row in pixels.indices) {
            if (pixels[row][col] > 0) {
                return col
            }
        }
    }
    return -1
}


