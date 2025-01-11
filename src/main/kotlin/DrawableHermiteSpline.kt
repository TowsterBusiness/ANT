import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Path2D
import javax.swing.JPanel
import kotlin.math.pow

data class DrawableHermiteSpline(var hermiteData : HermiteSpline, val panel : JPanel) {
    private var selectedPointIndex: Int = -1
    // 0 for position and 1 for velocity
    private var selectedType: Int = 0

    init {
        panel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val threshold = 10;

                // Check if the position point is near
                selectedType = 0;
                selectedPointIndex = hermiteData.pos.indexOfFirst { it.distanceSquared(pointToVector2d(e.point)) < threshold * threshold }

                // Check if the velocity point is near
                if (selectedPointIndex == -1) {
                    selectedType = 1
                    for ((index, vel) in hermiteData.vel.withIndex()) {
                        if ((vel / 3.0 + hermiteData.pos[index]).distanceSquared(pointToVector2d(e.point)) < threshold * threshold) {
                            selectedPointIndex = index
                            break
                        }
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                selectedPointIndex = -1
            }
        })

        panel.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (selectedPointIndex != -1) {
                    if (selectedType == 0) {
                        hermiteData.pos[selectedPointIndex] = pointToVector2d(e.point)
                    } else if (selectedType == 1) {
                        hermiteData.vel[selectedPointIndex] = (pointToVector2d(e.point) - hermiteData.pos[selectedPointIndex]) * 3.0
                    }
                    hermiteData.calculateSplineTimes()
                }
            }
        })
    }

    fun draw(t: Double, g2d : Graphics2D) {
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        for (pathIndex in 0..hermiteData.pos.size - 2) {

            var posW1 = hermiteData.pos[pathIndex]

            var posW2 = hermiteData.pos[pathIndex + 1]

            var velW1 = (hermiteData.pos[pathIndex] + hermiteData.vel[pathIndex] / 3.0)

            var velW2 = (hermiteData.pos[pathIndex + 1] + hermiteData.vel[pathIndex + 1] / 3.0)

            var velW2inv = (hermiteData.pos[pathIndex + 1] - hermiteData.vel[pathIndex + 1] / 3.0)

            // Draw Bézier curve
            val path = Path2D.Double().apply {
                moveTo(posW1.x, posW1.y)
                curveTo(
                    velW1.x, velW1.y,
                    velW2inv.x, velW2inv.y,
                    posW2.x, posW2.y
                )
            }
            g2d.color = Color.BLUE
            g2d.stroke = BasicStroke(2f)
            g2d.draw(path)

            g2d.color = Color.RED
            g2d.stroke = BasicStroke(1f)
            g2d.drawLine(posW1.x.toInt(), posW1.y.toInt(), velW1.x.toInt(), velW1.y.toInt())
            g2d.drawLine(posW2.x.toInt(), posW2.y.toInt(), velW2.x.toInt(), velW2.y.toInt())

            g2d.fillOval(posW1.x.toInt() - 5, posW1.y.toInt() - 5, 10, 10)
            g2d.fillOval(posW2.x.toInt() - 5, posW2.y.toInt() - 5, 10, 10)
            g2d.fillOval(velW1.x.toInt() - 5, velW1.y.toInt() - 5, 10, 10)
            g2d.fillOval(velW2.x.toInt() - 5, velW2.y.toInt() - 5, 10, 10)
        }

        // Draw Bézier curve
        val path2 = Path2D.Double().apply {
            moveTo(hermiteData.pos[0].x, hermiteData.pos[0].y)
            for (i in 0..<(hermiteData.totalTime() * 1000.0).toInt()) {
                val vec = hermiteData.posAtTime(i / 1000.0)
                lineTo(vec.x, vec.y)
            }
        }
        g2d.color = Color.GREEN
        g2d.stroke = BasicStroke(2f)
        g2d.draw(path2)

        val dotPosition = hermiteData.posAtTime(t)
        g2d.color = Color.GREEN
        g2d.fillOval( (dotPosition.x - 5).toInt(), (dotPosition.y - 5).toInt(), 10, 10)

        val dotWVel = dotPosition + hermiteData.velAtTime(t) / 3.0
        g2d.fillOval((dotWVel.x - 5).toInt(), (dotWVel.y - 5).toInt(), 10, 10)

        g2d.stroke = BasicStroke(1f)
        g2d.drawLine(dotPosition.x.toInt(), dotPosition.y.toInt(), dotWVel.x.toInt(), dotWVel.y.toInt())
    }
}