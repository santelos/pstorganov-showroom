package ru.stroganov.showroom.account.userinfoservice.common

import io.konform.validation.ValidationBuilder
import io.konform.validation.ValidationError
import io.konform.validation.ValidationErrors
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json

fun ValidationBuilder<String>.isNumber() = addConstraint(
    "must be a number"
) { runCatching { it.toLong() }.isSuccess }

val validationErrorSerializer: KSerializer<List<ValidationError>> = ListSerializer(object : KSerializer<ValidationError> {
    override val descriptor: SerialDescriptor = serialDescriptor<ValidationError>()

    override fun deserialize(decoder: Decoder): ValidationError =
        throw IllegalStateException("ValidationError is not deserializable")

    override fun serialize(encoder: Encoder, value: ValidationError) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.dataPath)
            encodeStringElement(descriptor, 1, value.dataPath)
        }
    }
})

fun ValidationErrors.toJson(): String = Json.encodeToString(validationErrorSerializer, this)
