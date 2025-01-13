package com.example.pdf_reader

import android.net.Uri
import java.util.Base64

object UriSerializer : KSerializer<Uri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun serialize(encoder: Base64.Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Base64.Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}