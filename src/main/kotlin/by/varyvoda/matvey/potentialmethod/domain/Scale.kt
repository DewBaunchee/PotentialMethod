package by.varyvoda.matvey.potentialmethod.domain

import kotlin.math.round

class Scale(domainFrom: Int, domainTo: Int, realFrom: Int, realTo: Int) {

    private val shift: Int = domainFrom - realFrom

    private val ratio: Double = (domainTo.toDouble() - domainFrom) / (realTo - realFrom)

    fun scale(domain: Int): Int {
        return round((domain - shift) / ratio).toInt()
    }

    fun invert(real: Int): Int {
        return round(real * ratio + shift).toInt()
    }
}