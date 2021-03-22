package com.github.nanodeath

object ExperienceCalculator {
    fun calculate(token: Long, allocations: Map<Experience, Int>): Experience {
        if (allocations.isEmpty()) return Experience.Control
        if (allocations.size == 1) return allocations.keys.single()

        val totalWeight = allocations.values.sum()
        // doubling because our domain space isn't 0 to MAXVALUE, it's -MINVALUE to MAXVALUE,
        // but you can't do (MAXVALUE - MINVALUE) / totalWeight without overflowing
        val bucketWidth = Long.MAX_VALUE / totalWeight * 2
        return identifyTreatment(token, allocations, bucketWidth)
    }

    private fun <T> identifyTreatment(token: Long, allocations: Map<T, Int>, bucketWidth: Long): T {
        var weightSoFar = Long.MIN_VALUE
        for ((treatment, weight) in allocations) {
            val allocationWidth = weight * bucketWidth
            val weightRange = weightSoFar until (weightSoFar + allocationWidth)
            if (token in weightRange) return treatment
            weightSoFar += allocationWidth
        }
        // Possible because Long.MAX_VALUE might not be evenly divisible by totalWeight
        return allocations.entries.last().key
    }
}