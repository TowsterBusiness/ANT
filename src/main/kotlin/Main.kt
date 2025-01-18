import java.awt.*
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
            Vector2d(-804.0, 1308.0),
            Vector2d(450.0, 300.0),
        ),
        1000.0,
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

        splineGraph.x = 20
        splineGraph.y = 20
        splineGraph.height = 80
        splineGraph.width = 960
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D

        splineSprite.draw(t, g2d)

        splineGraph.draw(t, g)

        g2d.drawString(hermiteData.totalTime().toString(), 10, 10);

        var pos = hermiteData.posAtTime(t)
        var angle = hermiteData.velAtTime(t).angle()
        g2d.color = Color(255, 75, 75)
        val rect2 = Rectangle(-20, -20, 40, 40)
        g2d.translate(pos.x.toInt(), pos.y.toInt())
        g2d.rotate(angle)
        g2d.draw(rect2)
        g2d.fill(rect2)


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
