package ru.stroganov.showroom.account.userinfoservice.repo

import org.flywaydb.core.Flyway

interface FlywayRepo {
    fun applyMigration()
}

class FlywayRepoImpl(
    private val flyway: Flyway = flywayImpl
) : FlywayRepo {

    override fun applyMigration() {
        flyway.migrate()
    }
}
