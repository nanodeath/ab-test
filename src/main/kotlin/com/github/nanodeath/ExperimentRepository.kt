package com.github.nanodeath

/**
 * Looks up Experiment metadata by the name of the experiment.
 *
 * This shouldn't be done on every request; it's highly cacheable for short periods, like 1-5 minutes.
 *
 * Most implementations are like nesting dolls, or a filter chain: if they don't have information about the experiment,
 * they punt the decision to a provided delegate.
 */
interface ExperimentRepository {
    /**
     * Retrieve experiment information based on [experiment]. Some implementations may vary on [identity], for e.g.
     * developer overrides.
     */
    operator fun get(experiment: String, identity: String): ExperimentInfo
}

/** Simplest repository, just returns the empty experiment. */
object FallbackRepository : ExperimentRepository {
    override fun get(experiment: String, identity: String): ExperimentInfo = ExperimentInfo.noop
}

/**
 * Experiment into is just passed straight into the constructor!
 */
class HardcodedExperimentRepository(
    private val experiments: Map<String, ExperimentInfo>,
    private val next: ExperimentRepository = FallbackRepository
) : ExperimentRepository {
    override fun get(experiment: String, identity: String): ExperimentInfo =
        experiments[experiment] ?: next[experiment, identity]
}

/** Enable per-identity overrides. */
class OverrideExperimentRepository(
    private val identityOverride: Map</* experiment */String, Map</* identity */String, Experience>>,
    private val next: ExperimentRepository = FallbackRepository
) : ExperimentRepository {
    override fun get(experiment: String, identity: String): ExperimentInfo =
        identityOverride[experiment]?.get(identity)?.toExperimentInfo(experiment) ?: next[experiment, identity]

    private fun Experience.toExperimentInfo(name: String) = ExperimentInfo(name, mapOf(this to 1), 0L)
}