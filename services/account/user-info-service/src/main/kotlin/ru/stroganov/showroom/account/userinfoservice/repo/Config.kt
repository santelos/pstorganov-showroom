package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import ru.stroganov.showroom.account.userinfoservice.*

val r2dbcConnectionFactory: ConnectionFactory by lazy {
    ConnectionFactoryOptions
        .parse("r2dbc:postgresql://${appConfig.database.host}:${appConfig.database.port}/${appConfig.database.database}")
        .mutate()
        .option(ConnectionFactoryOptions.USER, appConfig.database.username)
        .option(ConnectionFactoryOptions.PASSWORD, appConfig.database.password)
        .build()
        .let(ConnectionFactories::get)
}

val flywayLoaded: Flyway by lazy {
    Flyway.configure()
        .dataSource(
            "jdbc:postgresql://${appConfig.database.host}:${appConfig.database.port}/${appConfig.database.database}",
            appConfig.database.username,
            appConfig.database.password
        ).load()
}
