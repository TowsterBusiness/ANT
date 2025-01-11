import java.awt.Point
import kotlin.math.*

class Vector2d(var x: Double, var y: Double) {

    // Vector magnitude (length)
    fun magnitude(): Double = sqrt(x * x + y * y)

    // Normalize the vector (make it unit length)
    fun normalize(): Vector2d {
        val mag = magnitude()
        return if (mag == 0.0) this else Vector2d(x / mag, y / mag)
    }

    // Add another vector
    operator fun plus(other: Vector2d): Vector2d = Vector2d(x + other.x, y + other.y)

    // Subtract another vector
    operator fun minus(other: Vector2d): Vector2d = Vector2d(x - other.x, y - other.y)

    // Scale the vector (multiply by a scalar)
    operator fun times(scalar: Double): Vector2d = Vector2d(x * scalar, y * scalar)

    // Divide the vector by a scalar
    operator fun div(scalar: Double): Vector2d = Vector2d(x / scalar, y / scalar)

    // Dot product
    infix fun dot(other: Vector2d): Double = x * other.x + y * other.y

    // Rotate the vector by an angle (in radians)
    fun rotate(angle: Double): Vector2d {
        val cosAngle = cos(angle)
        val sinAngle = sin(angle)
        return Vector2d(
            x * cosAngle - y * sinAngle,
            x * sinAngle + y * cosAngle
        )
    }

    // Angle of the vector (in radians)
    fun angle(): Double = atan2(y, x)

    // Create a copy of the vector
    fun copy(): Vector2d = Vector2d(x, y)

    // Print the vector as a string
    override fun toString(): String = "Vector2D(x=$x, y=$y)"

    fun toPoint() : Point = Point(x.toInt(), y.toInt());

    fun distanceSquared(other: Vector2d) : Double = (x - other.x).pow(2) + (y - other.y).pow(2)
}

fun pointToVector2d(point : Point): Vector2d = Vector2d(point.x.toDouble(), point.y.toDouble())