package com.komissarov.shared

import com.komissarov.models.Payload
import io.mockk.mockk
import net.datafaker.Faker

internal object Data {
    val faker by lazy { Faker() }
    val payload by lazy { mockk<Payload>() }
}
