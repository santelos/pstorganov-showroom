package ru.stroganov.oauth2.tokenmediatingbackend

val OAUTH2__CALLBACK_URL: String = System.getenv("OAUTH2__CALLBACK_URL")
val OAUTH2__PROVIDER_URL: String = System.getenv("OAUTH2__PROVIDER_URL")
val OAUTH2__PROVIDER_INTERNAL_URL: String = System.getenv("OAUTH2__PROVIDER_INTERNAL_URL")
val OAUTH2__CLIENT_ID: String = System.getenv("OAUTH2__CLIENT_ID")
val OAUTH2__CLIENT_SECRET: String = System.getenv("OAUTH2__CLIENT_SECRET")

val SUCCESS_REDIRECT: String = System.getenv("SUCCESS_REDIRECT")
