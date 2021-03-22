package com.github.nanodeath

interface AssignmentRecorder {
    fun recordAssignment(identity: String, experiment: String, experience: Experience)
}

class ConsoleAssignmentRecorder(private val prefix: String = "") : AssignmentRecorder {
    override fun recordAssignment(identity: String, experiment: String, experience: Experience) {
        System.err.println("${prefix}Identity $identity got $experiment:$experience")
    }
}