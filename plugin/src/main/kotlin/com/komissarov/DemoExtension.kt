package com.komissarov

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class DemoExtension @Inject constructor(objects: ObjectFactory) {
    val relativePath: Property<String> = objects.property(String::class.java)
}
