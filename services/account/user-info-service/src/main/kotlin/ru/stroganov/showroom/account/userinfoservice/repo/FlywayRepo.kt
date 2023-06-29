package ru.stroganov.showroom.account.userinfoservice.repo

import org.flywaydb.core.Flyway

interface FlywayRepo {
    fun applyMigration()
}

internal object FlywayRepoObject : FlywayRepo by FlywayRepoImpl(
    flyway = flywayLoaded
)
internal class FlywayRepoImpl(
    private val flyway: Flyway
) : FlywayRepo {

    override fun applyMigration() {
        flyway.migrate()
    }
}
