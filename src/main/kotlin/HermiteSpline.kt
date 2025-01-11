import sun.security.ec.point.ProjectivePoint.Mutable
import java.awt.Point
import java.util.Vector
import kotlin.math.floor
import kotlin.math.max

data class HermiteSpline(var pos:MutableList<Vector2d>, var vel:MutableList<Vector2d>, var maxVel : Double) {
    val tks: MutableList<Double> = mutableListOf()

    init {
        calculateSplineTimes()
    }

    fun calculateSplineTimes() {
        for (tkIndex in 0..<pos.size - 1) {
            // Finds the maximum of the function in the current tk value
            var localTk = 0.25
            for (tkLoop in 0..4) {
                // Variable that handles the maximum velocities that it finds. Starts off with the initial and final velocities
                var maxVelPoint: Double = -1.0
                var maxXPoint: Double = -1.0
                // Creates 10 points that the gradient descent algorithm is being used for
                for (gradientDecentPoints in 0..100) {
                    var gradientDescent = GradientDescent { t -> velAtTime(t, tkIndex, localTk).magnitude() }
                    gradientDescent.x = localTk * gradientDecentPoints / 100.0
                    gradientDescent.gamma = 0.0000001

                    var isBroken = false;
                    // Runs the gradient descent algorithm 10 times while checking if it exceeded the bounds of the interval
                    for (gradientDecentUpdateIndex in 0..<20) {
                        gradientDescent.update()
                        if (gradientDescent.x < 0 || gradientDescent.x > localTk) {
                            isBroken = true
                            break;
                        }

                    }
                    if (gradientDescent.getY() > maxVelPoint && !isBroken) {
                        maxVelPoint = gradientDescent.getY()
                        maxXPoint = gradientDescent.x
                    }

                }

                if (maxVelPoint == -1.0) {
                    System.out.println("Finding Low Point")
                    var gradientDescent = GradientDescent { t -> velAtTime(t, tkIndex, localTk).magnitude() }
                    gradientDescent.x = 0.5
                    gradientDescent.gamma = 0.0000001
                    gradientDescent.findMax = false

                    // Runs the gradient descent algorithm 10 times while checking if it exceeded the bounds of the interval
                    for (gradientDecentUpdateIndex in 0..<20) {
                        gradientDescent.update()

                    }

                    maxVelPoint = gradientDescent.getY()
                    maxXPoint = gradientDescent.x
                }

                System.out.println("Max Vel Point: " + maxVelPoint)
                System.out.println("Max X Point  : " + maxXPoint)
                System.out.println("Local Tk     : " + localTk)

                // Finding the Newton update
                var newtonsMethod = NewtonsMethod { tk -> velAtTime(maxXPoint, tkIndex, tk).magnitude() }
                newtonsMethod.x = localTk
                newtonsMethod.yEnd = maxVel
                System.out.println("f            : " + newtonsMethod.f(localTk))
                System.out.println("d            : " + newtonsMethod.d(localTk))
                newtonsMethod.update()
                localTk = newtonsMethod.x
                System.out.println("Tk Point     : " + localTk)
            }
            System.out.println("==== New TK ====")
            tks.add(localTk)
        }
    }

    fun totalTime(): Double {
        var count: Double = 0.0
        for (tk in tks) {
            count += tk
        }
        return count
    }

    fun posAtTime(tRaw: Double): Vector2d {
        val tLength = this.totalTime()
        val t = tRaw % tLength

        var count = 0.0
        var index = 0
        for (tk in tks) {
            if (t < count + tk) break;
            count += tk
            index++
        }

        if (pos.size <= index + 1) System.out.println("time is out of scope of positions t: ${tRaw} pos: ${pos.size}")
        if (vel.size <= index + 1) System.out.println("time is out of scope of velocities t: ${tRaw} vel: ${vel.size}")

        return posAtTime(t - count, index)
    }

    fun posAtTime(tRaw: Double, index: Int): Vector2d {
        val tk = tks[index]

        val t = tRaw / tk

        val pos1: Vector2d = pos[index]
        val pos2: Vector2d = pos[index + 1]
        val vel1: Vector2d = vel[index]
        val vel2: Vector2d = vel[index + 1]

        val t2 = t * t
        val t3 = t * t * t

        val x =
            (2 * t3 - 3 * t2 + 1) * pos1.x + (t3 - 2 * t2 + t) * vel1.x * tk + (-2 * t3 + 3 * t2) * pos2.x + (t3 - t2) * vel2.x * tk
        val y =
            (2 * t3 - 3 * t2 + 1) * pos1.y + (t3 - 2 * t2 + t) * vel1.y * tk + (-2 * t3 + 3 * t2) * pos2.y + (t3 - t2) * vel2.y * tk

        return Vector2d(x, y)
    }

    fun velAtTime(tRaw: Double): Vector2d {
        val tLength = this.totalTime()
        val t = tRaw % tLength

        var count = 0.0
        var index = 0
        for (tk in tks) {
            if (t < count + tk) break;
            count += tk
            index++
        }

        if (pos.size < index + 1) System.out.println("time is out of scope of positions t: ${tRaw} pos: ${pos.size}")
        if (vel.size < index + 1) System.out.println("time is out of scope of velocities t: ${tRaw} vel: ${vel.size}")

        return velAtTime(t - count, index)
    }

    fun velAtTime(tRaw: Double, index: Int): Vector2d {
        val tk = tks[index]

        return velAtTime(tRaw, index, tk)
    }

    fun velAtTime(tRaw: Double, index: Int, tk: Double): Vector2d {
        val t = tRaw / tk

        val pos1: Vector2d = pos[index]
        val pos2: Vector2d = pos[index + 1]
        val vel1: Vector2d = vel[index]
        val vel2: Vector2d = vel[index + 1]

        val t2 = t * t

        val x = (6 * t2 - 6 * t) * (pos1.x - pos2.x) / tk + (3 * t2 - 4 * t + 1) * vel1.x + (3 * t2 - 2 * t) * vel2.x
        val y = (6 * t2 - 6 * t) * (pos1.y - pos2.y) / tk + (3 * t2 - 4 * t + 1) * vel1.y + (3 * t2 - 2 * t) * vel2.y

        return Vector2d(x, y)
    }

    fun accelerationAtTime(tRaw: Double): Vector2d {
        var count = 0.0
        var index = 0
        for (tk in tks) {
            if (tRaw < count + tk) break;
            count += tk
            index++
        }

        if (pos.size < index + 1) System.out.println("time is out of scope of positions t: ${tRaw} pos: ${pos.size}")
        if (vel.size < index + 1) System.out.println("time is out of scope of velocities t: ${tRaw} vel: ${vel.size}")

        return accelerationAtTime(tRaw - count, index)
    }

    fun accelerationAtTime(tRaw: Double, index: Int): Vector2d {
        val tk = tks[index]
        val tk2 = tk * tk

        if (pos.size < index + 1) System.out.println("time is out of scope of positions t: ${tRaw} pos: ${pos.size}")
        if (vel.size < index + 1) System.out.println("time is out of scope of velocities t: ${tRaw} vel: ${vel.size}")

        val t = tRaw / tk

        val pos1: Vector2d = pos[index]
        val pos2: Vector2d = pos[index + 1]
        val vel1: Vector2d = vel[index]
        val vel2: Vector2d = vel[index + 1]

        val x =
            (12 * t - 6) * pos1.x / tk2 + (6 * t - 4) * vel1.x / tk + (6 * t - 2) * vel2.x / tk + (6 - 12 * t) * pos2.x / tk2
        val y =
            (12 * t - 6) * pos1.y / tk2 + (6 * t - 4) * vel1.y / tk + (6 * t - 2) * vel2.y / tk + (6 - 12 * t) * pos2.y / tk2

        return Vector2d(x, y)
    }
}



