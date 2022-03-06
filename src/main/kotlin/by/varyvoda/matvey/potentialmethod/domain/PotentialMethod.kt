package by.varyvoda.matvey.potentialmethod.domain


class PotentialMethod(
    private val clusters: Map<Int, Cluster>,
    private val decisionFunction: (difference: Int) -> Double
) {

    fun evaluateClasses(evaluating: Vector): Map<Int, Double> {
        val potentials: HashMap<Int, Double> = HashMap()
        clusters.forEach { entry ->
            entry.value.samples.forEach { sample ->
                potentials[entry.key] = potentials.getOrDefault(entry.key, 0.0) + decisionFunction(evaluating.compare(sample))
            }
        }
        return potentials
    }
}
