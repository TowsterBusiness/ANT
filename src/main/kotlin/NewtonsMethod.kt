// Source: https://en.wikipedia.org/wiki/Newton%27s_method

class NewtonsMethod(var f : (Double) -> Double) {
    var x : Double = 0.0
    var yEnd : Double = 0.0
    var dX : Double = 0.00001

    fun update() : Double {
        x -= (f(x) - yEnd) / d(x);
        return x
    }

    fun d(x : Double) : Double {
        return (f(x + dX) - f(x)) / dX
    }
}