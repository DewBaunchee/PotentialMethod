package by.varyvoda.matvey.potentialmethod.domain

class Vector(vararg val values: Int) {

    fun compare(vector: Vector): Int {
        return parallel(0, this, vector) { acc, left, right -> acc + (if (left != right) 1 else 0) }
    }

    fun scalarMul(vector: Vector): Int {
        return parallel(0, this, vector) { acc, left, right ->
            acc + left * right
        }
    }

    fun plus(vector: Vector): Vector {
        return Vector(values = parallel(mutableListOf<Int>(), this, vector) { acc, left, right ->
            acc.add(left + right)
            return@parallel acc
        }.toIntArray())
    }

    fun minus(vector: Vector): Vector {
        return Vector(values = parallel(mutableListOf<Int>(), this, vector) { acc, left, right ->
            acc.add(left - right)
            return@parallel acc
        }.toIntArray())
    }

    fun mul(multiplier: Int): Vector {
        return Vector(values = values.map { it * multiplier }.toIntArray())
    }

    override fun toString(): String {
        return values.joinToString(" ")
    }
}

private fun <R> parallel(
    initial: R,
    left: Vector,
    right: Vector,
    operator: (acc: R, left: Int, right: Int) -> R
): R {
    if (left.values.size != right.values.size) throw IllegalArgumentException("Vector lengths are different!")

    var accumulator = initial
    for (i in left.values.indices) {
        accumulator = operator(accumulator, left.values[i], right.values[i])
    }
    return accumulator
}