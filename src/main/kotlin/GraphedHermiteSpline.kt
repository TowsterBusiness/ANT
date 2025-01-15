import java.awt.*
import javax.swing.JPanel

data class GraphedHermiteSpline(var hermiteData : HermiteSpline, val panel : JPanel) {
    var x = 0
    var y = 0
    var height = 0
    var width = 0

    fun draw(t: Double, g2d : Graphics2D) {
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val divisions = 1000.0

        g2d.color = Color.BLUE
        g2d.stroke = BasicStroke(2f)
        for (index in 0..<(divisions - 1).toInt()) {
            g2d.drawLine(
                x + (index / divisions * width).toInt(),
                (y + hermiteData.velAtTime(index / divisions * hermiteData.totalTime()).magnitude() / 1000 * height).toInt(),
                x + ((index + 1) / divisions * width).toInt(),
                (y + hermiteData.velAtTime((index + 1) / divisions * hermiteData.totalTime()).magnitude() / 1000 * height).toInt())
        }

        g2d.color = Color.RED
        g2d.stroke = BasicStroke(2f)
        for (index in 0..<(divisions - 1).toInt()) {
            g2d.drawLine(
                x + (index / divisions * width).toInt(),
                (y + hermiteData.accelerationAtTime(index / divisions * hermiteData.totalTime()).magnitude() / 1000 * height).toInt(),
                x + ((index + 1) / divisions * width).toInt(),
                (y + hermiteData.accelerationAtTime((index + 1) / divisions * hermiteData.totalTime()).magnitude() / 1000 * height).toInt())
        }

        g2d.drawLine(x, y, x, y + height)
        g2d.drawLine(x, y, x + width, y)
        g2d.drawLine(x + width, y, x + width, y + height)
        g2d.drawLine(x, y + height, x + width, y + height)


    }
}