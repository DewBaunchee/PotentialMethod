package by.varyvoda.matvey.potentialmethod.domain

class Scale(domainFrom: Double, domainTo: Double, realFrom: Double, realTo: Double) {

    private val shift: Double = domainFrom - realFrom

    private val ratio: Double = (domainTo - domainFrom) / (realTo - realFrom)

    fun scale(domain: Double): Double {
        return (domain - shift) / ratio
    }
}