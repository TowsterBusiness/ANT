import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Path2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

class InteractiveBezierCurvePanel : JPanel() {

    private var hermiteData : HermiteSpline = HermiteSpline(
        mutableListOf(
            Vector2d(50.0, 300.0),
            Vector2d(450.0, 300.0),
            Vector2d(650.0, 400.0),
            Vector2d(450.0, 400.0),
        ),
        mutableListOf(
            Vector2d(300.0, -750.0),
            Vector2d(300.0, -750.0),
            Vector2d(300.0, 750.0),
            Vector2d(450.0, 300.0),
        ),
        1000.0
    )
    private var splineSprite : DrawableHermiteSpline = DrawableHermiteSpline(hermiteData, this);
    private var splineGraph : GraphedHermiteSpline = GraphedHermiteSpline(hermiteData, this)

    private var t : Double = 0.0;
    private var animationSpeed : Double = 0.01;

    init {
        Timer(16) { // Roughly 60 FPS
            t += animationSpeed
            repaint()
        }.start()

        splineGraph.x = 900
        splineGraph.y = 20
        splineGraph.height = 80
        splineGraph.width = 960
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D

        splineSprite.draw(t, g2d)
    }
}


fun main() {
    val frame = JFrame("Interactive Bézier Curve")
    frame.apply {
        add(InteractiveBezierCurvePanel())
        size = Dimension(1000, 1000)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }
}
