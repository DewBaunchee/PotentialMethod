package by.varyvoda.matvey.potentialmethod

import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.ceil


fun main() {
    for (digit in "0123456789") {
        Files.createDirectories(Path.of("samples\\img\\$digit"))
        getAllFonts().forEach { font ->
            val file = File("samples\\img\\$digit\\${font.replace(Regex("\\s+"), "")}.png")
            file.writeText("")
            ImageIO.write(createImage(digit, font), "png", file)
        }
    }
}

private fun getAllFonts(): List<String> {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts.toList().map { it.getFontName(Locale.US) }
}

private fun createImage(s: Char, fontName: String): BufferedImage {
    var img = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
    var g: Graphics = img.graphics

    val f = Font(fontName, Font.PLAIN, 48)
    g.font = f

    val frc: FontRenderContext = g.fontMetrics.fontRenderContext
    val rect: Rectangle2D = f.getStringBounds(s + "", frc)
    g.dispose()

    img = BufferedImage(
        ceil(rect.width).toInt(),
        ceil(rect.height).toInt(),
        BufferedImage.TYPE_4BYTE_ABGR
    )
    g = img.graphics
    g.color = Color.black
    g.font = f

    val fm: FontMetrics = g.fontMetrics
    val x = 0
    val y: Int = fm.ascent
    g.drawString(s.toString(), x, y)

    g.dispose()

    return img
}
