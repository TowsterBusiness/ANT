import sun.security.ec.point.ProjectivePoint.Mutable
import java.awt.Point
import java.util.Vector
import kotlin.math.floor
import kotlin.math.max

data class HermiteSpline(var pos:MutableList<Vector2d>, var vel:MutableList<Vector2d>, var maxVel : Double, var maxAccel : Double) {
    var tks: MutableList<Double> = mutableListOf()

    init {
        calculateSplineTimes()
    }

    fun calculateSplineTimes() {

        System.out.println(velAtTime(0.0088657494, 2, 0.25).magnitude());

        tks = mutableListOf()
        System.out.println(pos)
        System.out.println(vel)
        for (tkIndex in 0..<pos.size - 1) {
            // Finds the maximum of the function in the current tk value
            var localTk = 0.25
            for (tkLoop in 0..4) {
                // Variable that handles the maximum velocities that it finds. Starts off with the initial and final velocities
                var anchorX = -1.0

                var gradientDescentMax = GradientDescent { t -> velAtTime(t, tkIndex, localTk).magnitude() }
                gradientDescentMax.gamma = 0.00001 * localTk
                gradientDescentMax.eval(20, 0.0, localTk)

                anchorX = gradientDescentMax.x

                if (gradientDescentMax.x == Double.NEGATIVE_INFINITY) {
                    System.out.println("Finding Low Point")
                    var gradientDescentMin = GradientDescent { t -> velAtTime(t, tkIndex, localTk).magnitude() }
                    gradientDescentMin.x = 0.5
                    gradientDescentMin.gamma = 0.00001 * localTk
                    gradientDescentMin.findMax = false

                    // Runs the gradient descent algorithm 10 times while checking if it exceeded the bounds of the interval
                    for (gradientDecentUpdateIndex in 0..<20) {
                        gradientDescentMin.update()
                    }

                    anchorX = gradientDescentMin.x

                    System.out.println("Max Vel Point: ${gradientDescentMin.getY()}")
                } else {
                    System.out.println("Max Vel Point: ${gradientDescentMax.getY()}")
                }

                System.out.println("Max X Point  : $anchorX")
                System.out.println("Local Tk     : $localTk")

                // Finding the Newton update
                var newtonsMethod = NewtonsMethod { tk -> velAtTime(anchorX, tkIndex, tk).magnitude() }
                newtonsMethod.x = localTk
                newtonsMethod.yEnd = maxVel
                System.out.println("f            : " + newtonsMethod.f(localTk))
                System.out.println("d            : " + newtonsMethod.d(localTk))
                newtonsMethod.update()
                localTk = newtonsMethod.x
                System.out.println("Tk Point     : $localTk")
            }

            var localTkAccel = 0.25
            for (tkLoop in 0..20) {
                // Variable that handles the maximum velocities that it finds. Starts off with the initial and final velocities
                var anchorX = -1.0

                var gradientDescentMax = GradientDescent { t -> accelerationAtTime(t, tkIndex, localTkAccel).magnitude() }
                gradientDescentMax.gamma = 0.00001 * localTkAccel
                gradientDescentMax.eval(20, 0.0, localTkAccel)

                val critAccel = gradientDescentMax.f(gradientDescentMax.x)
                val startAccel = gradientDescentMax.f(0.0)
                val endAccel = gradientDescentMax.f(localTkAccel)

                if (critAccel > startAccel) {
                    if (critAccel > endAccel) {
                        anchorX = gradientDescentMax.x
                    } else {
                        anchorX = localTkAccel
                    }
                } else {
                    if (startAccel > endAccel) {
                        anchorX = 0.0
                    } else {
                        anchorX = localTkAccel
                    }
                }

                if (gradientDescentMax.f(anchorX) < maxAccel && Math.abs(gradientDescentMax.f(anchorX) - maxAccel) < 1) break;

                System.out.println("Max X Point A: $anchorX")
                System.out.println("Local Tk    A: $localTkAccel")

                // Finding the Newton update
                var newtonsMethod = NewtonsMethod { tk -> accelerationAtTime(anchorX, tkIndex, tk).magnitude() }
                newtonsMethod.x = localTkAccel
                newtonsMethod.yEnd = maxAccel
                System.out.println("f           A: " + newtonsMethod.f(localTkAccel))
                System.out.println("d           A: " + newtonsMethod.d(localTkAccel))
                newtonsMethod.update()
                localTkAccel = newtonsMethod.x
                System.out.println("Tk Point    A: $localTkAccel")
            }



            System.out.println("==== New TK ====")
            tks.add(Math.max(localTk, localTkAccel))
        }

        System.out.println(totalTime())
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

        val x = pos1.x / tk * (6 * t2 - 6 * t) + vel1.x * (3 * t2 - 4 * t + 1) + vel2.x * (3 * t2 - 2 * t) + pos2.x / tk * (6 * t - 6 * t2)
        val y = pos1.y / tk * (6 * t2 - 6 * t) + vel1.y * (3 * t2 - 4 * t + 1) + vel2.y * (3 * t2 - 2 * t) + pos2.y / tk * (6 * t - 6 * t2)

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

    fun accelerationAtTime(tRaw: Double, index: Int, tk: Double): Vector2d {
        val t = tRaw / tk

        val tk2 = tk * tk

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