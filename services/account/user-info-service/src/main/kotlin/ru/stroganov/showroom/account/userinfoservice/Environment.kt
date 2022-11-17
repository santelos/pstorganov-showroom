package ru.stroganov.showroom.account.userinfoservice

// val OAUTH2__CLIENT_ID: String = System.getenv("OAUTH2__CLIENT_ID")
// val OAUTH2__CLIENT_SECRET: String = System.getenv("OAUTH2__CLIENT_SECRET")
val DATABASE__HOST: String = System.getenv("DATABASE__HOST")
val DATABASE__PORT: String = System.getenv("DATABASE__PORT") ?: "5432"
val DATABASE__USERNAME: String = System.getenv("DATABASE__USERNAME")
val DATABASE__PASSWORD: String = System.getenv("DATABASE__PASSWORD")
val DATABASE__DATABASE_NAME: String = System.getenv("DATABASE__DATABASE_NAME")
