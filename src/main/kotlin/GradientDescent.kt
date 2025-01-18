// Source: https://en.wikipedia.org/wiki/Gradient_descent

class GradientDescent(var f : (Double) -> Double) {
    var x : Double = 0.0
    var gamma : Double = 1.0
    var findMax : Boolean = true
    var dX : Double = 0.00001

    fun eval(pointNum : Int,  interval1 : Double, interval2 : Double) {
        // Variable that handles the maximum velocities that it finds. Starts off with the initial and final velocities
        var maxVelPoint: Double = Double.NEGATIVE_INFINITY
        var maxXPoint: Double = Double.NEGATIVE_INFINITY
        // Creates 10 points that the gradient descent algorithm is being used for

        for (gradientDecentPoints in 0..pointNum) {
            var gradientDescent = GradientDescent { t -> f(t) }
            gradientDescent.x = (interval2 - interval1) * gradientDecentPoints / pointNum + interval1
            gradientDescent.gamma = gamma
            gradientDescent.findMax = findMax

            var outNumber = 0;
            // Runs the gradient descent algorithm 10 times while checking if it exceeded the bounds of the interval
            while (true) {
                gradientDescent.update()

                if (gradientDescent.x < interval1 || gradientDescent.x > interval2) {
                    break;
                }
                outNumber ++
                if (Math.abs(gradientDescent.d(gradientDescent.x)) < 0.0001 || outNumber > 1000) {
                    if (findMax) {
                        if (gradientDescent.getY() > maxVelPoint) {
                            maxVelPoint = gradientDescent.getY()
                            maxXPoint = gradientDescent.x
                        }
                    } else {
                        if (gradientDescent.getY() < maxVelPoint) {
                            maxVelPoint = gradientDescent.getY()
                            maxXPoint = gradientDescent.x
                        }
                    }

                    break;
                }
            }
        }

        x = maxXPoint
    }

    fun update() {

        if (findMax) {
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