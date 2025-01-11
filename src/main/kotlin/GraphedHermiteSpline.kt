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


    }
}