package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.flywaydb.core.Flyway
import ru.stroganov.showroom.account.userinfoservice.*

val dbConnectionFactoryImpl = ConnectionFactories.get(
    ConnectionFactoryOptions
        .parse("r2dbc:postgresql://$DATABASE__HOST:$DATABASE__PORT/$DATABASE__DATABASE_NAME")
        .mutate()
        .option(ConnectionFactoryOptions.USER, DATABASE__USERNAME)
        .option(ConnectionFactoryOptions.PASSWORD, DATABASE__PASSWORD)
        .build()
)

val flywayImpl: Flyway = Flyway.configure()
    .dataSource(
        "jdbc:postgresql://$DATABASE__HOST:$DATABASE__PORT/$DATABASE__DATABASE_NAME",
        DATABASE__USERNAME,
        DATABASE__PASSWORD
    ).load()
