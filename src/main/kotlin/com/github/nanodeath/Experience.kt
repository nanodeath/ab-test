package com.github.nanodeath

/** Whether the user is in Control or one of the Treatments. */
sealed class Experience {
    object Control : Experience() {
        override fun toString() = "C"
    }

    data class Treatment(val value: Int) : Experience() {
        override fun toString() = "T$value"
    }
}