package ru.stroganov.showroom.account.userinfoservice.repo

import org.flywaydb.core.Flyway

interface FlywayRepo {
    fun applyMigration()
}

internal object FlywayRepoObject : FlywayRepo by FlywayRepoImpl()
internal class FlywayRepoImpl(
    private val flyway: Flyway = flywayLoaded
) : FlywayRepo {

    override fun applyMigration() {
        flyway.migrate()
    }
}
