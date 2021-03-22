package com.github.nanodeath

class ExperimentInfo(val name: String, val allocations: Map<Experience, Int>, val nonce: Long) {
    companion object {
        val noop = ExperimentInfo("", emptyMap(), 0L)
    }
}