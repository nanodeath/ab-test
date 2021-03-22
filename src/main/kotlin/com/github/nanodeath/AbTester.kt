package com.github.nanodeath

import kotlin.random.Random

class AbTester(
    private val experimentRepo: ExperimentRepository,
    private val assignmentRecorder: AssignmentRecorder
) {
    fun getExperience(identity: String, experiment: String, record: Boolean = true): Experience {
        val experimentInfo = experimentRepo[experiment, identity]
        val token = identity.sha1().toLong().xor(experimentInfo.nonce.stretch())
        val assignment = ExperienceCalculator.calculate(token, experimentInfo.allocations)
        if (record) {
            assignmentRecorder.recordAssignment(identity, experiment, assignment)
        }
        return assignment
    }

    // Make nonces like "1" or "2" (used in tests) actually produce unique results; have to stretch them over the
    // MINVALUE to MAXVALUE space. Can do this in multiple ways, e.g. hashing, but this works fine.
    private fun Long.stretch() = Random(this).nextLong()
}

fun main() {
    val nonce = 1L
    val abTester1a = AbTester(
        HardcodedExperimentRepository(
            mapOf(
                "TEST" to ExperimentInfo(
                    "TEST", mapOf(
                        // C at 75%, T1 at 25%
                        Experience.Control to 3,
                        Experience.Treatment(1) to 1
                    ), nonce
                )
            )
        ), ConsoleAssignmentRecorder("V1a: ")
    )
    val abTester1b = AbTester(
        HardcodedExperimentRepository(
            mapOf(
                "TEST" to ExperimentInfo(
                    "TEST", mapOf(
                        // C at 25%, T1 at 75%
                        Experience.Control to 1,
                        Experience.Treatment(1) to 3
                    ), nonce
                )
            )
        ), ConsoleAssignmentRecorder("V1b: ")
    )
    val abTester2 = AbTester(
        HardcodedExperimentRepository(
            mapOf(
                "TEST" to ExperimentInfo(
                    "TEST", mapOf(
                        // C at 75%, T1 at 25%
                        Experience.Control to 3,
                        Experience.Treatment(1) to 1
                    ), nonce = 2L
                )
            )
        ), ConsoleAssignmentRecorder("V2: ")
    )

    println("What we're looking for is how the treatments change with the dial up.")
    println("Anything that was T1 in V1 should still be in T1 in V2.")
    val words = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
        sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
    """.trimIndent().splitToSequence(" ").take(10).toList()
    words.forEach { identity ->
        abTester1a.getExperience(identity, "TEST")
        abTester1b.getExperience(identity, "TEST")
        System.err.println("---")
    }
    println("---")
    println("---")
    println("V2: Same ratio as V1a, but different nonce. Should be different assignments.")
    words.forEach { identity ->
        abTester2.getExperience(identity, "TEST")
        System.err.println("---")
    }
}

/*
Sample output:

What we're looking for is how the treatments change with the dial up.
Anything that was T1 in V1 should still be in T1 in V2.
V1a: Identity Lorem got TEST:C
V1b: Identity Lorem got TEST:T1
---
V1a: Identity ipsum got TEST:C
V1b: Identity ipsum got TEST:T1
---
V1a: Identity dolor got TEST:T1
V1b: Identity dolor got TEST:T1
---
V1a: Identity sit got TEST:C
V1b: Identity sit got TEST:C
---
V1a: Identity amet, got TEST:T1
V1b: Identity amet, got TEST:T1
---
V1a: Identity consectetur got TEST:C
V1b: Identity consectetur got TEST:C
---
V1a: Identity adipiscing got TEST:C
V1b: Identity adipiscing got TEST:T1
---
V1a: Identity elit, got TEST:C
V1b: Identity elit, got TEST:C
---
V1a: Identity
sed got TEST:C
V1b: Identity
sed got TEST:T1
---
V1a: Identity do got TEST:C
V1b: Identity do got TEST:T1
---
V2: Identity Lorem got TEST:C
---
V2: Identity ipsum got TEST:C
---
V2: Identity dolor got TEST:C
---
V2: Identity sit got TEST:T1
---
V2: Identity amet, got TEST:C
---
V2: Identity consectetur got TEST:T1
---
V2: Identity adipiscing got TEST:C
---
V2: Identity elit, got TEST:T1
---
V2: Identity
sed got TEST:C
---
V2: Identity do got TEST:C
---
---
---
V2: Same ratio as V1a, but different nonce. Should be different assignments.
*/