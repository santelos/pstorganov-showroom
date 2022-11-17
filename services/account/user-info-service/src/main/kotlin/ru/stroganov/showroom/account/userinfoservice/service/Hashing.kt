package ru.stroganov.showroom.account.userinfoservice.service

import at.favre.lib.crypto.bcrypt.BCrypt

interface Hashing {
    fun hash(input: String): String
    fun compare(input: String, hash: String): Boolean
}

internal object HashingObject : Hashing by HashingImpl()
internal class HashingImpl : Hashing {
    private val cost = 12
    private val bcrypt = BCrypt.withDefaults()
    private val verifyer = BCrypt.verifyer()

    override fun hash(input: String): String =
        bcrypt.hashToString(cost, input.toCharArray())

    override fun compare(input: String, hash: String): Boolean =
        verifyer.verify(input.toByteArray(), hash.toByteArray()).verified
}
