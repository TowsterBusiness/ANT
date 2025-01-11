// Source: https://en.wikipedia.org/wiki/Gradient_descent

class GradientDescent(var f : (Double) -> Double) {
    var x : Double = 0.0
    var gamma : Double = 1.0
    var findMax : Boolean = true
    var dX : Double = 0.00001

    fun update() {

        if (findMax == true) {
            x += gamma * d(x)
        } else {
            x -= gamma * d(x)
        }
    }

    fun getY() : Double {
        return f(x)
    }

    fun d(x : Double) : Double {
        return (f(x) - f(x - dX)) / dX
    }

}