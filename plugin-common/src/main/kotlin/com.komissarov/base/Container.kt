package com.komissarov.base

import com.komissarov.exceptions.DemoContainerException
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.lazyModules
import org.koin.core.module.LazyModule
import org.koin.core.waitAllStartJobs
import org.koin.mp.KoinPlatform
import org.tinylog.Logger

/**
 * Base Container.
 */
abstract class Container : AutoCloseable {
    /**
     * Application module.
     */
    abstract val appModule: LazyModule

    /**
     * Registered components.
     */
    val components: Koin? by lazy {
        var koin: Koin? = null

        runCatching {
            startKoin {
                lazyModules(appModule)
            }

            koin = KoinPlatform.getKoinOrNull()
            koin?.waitAllStartJobs()
        }.onFailure { exception ->
            Logger.error {
                DemoContainerException(
                    buildString {
                        append(Container::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        koin
    }

    /**
     * Create component instance.
     */
    inline fun <reified T> getInstance(): T? where T : Any = components?.getOrNull<T>()

    /**
     * Close container.
     */
    override fun close() {
        runCatching {
            components?.close()
            stopKoin()
        }.onFailure { exception ->
            Logger.error {
                DemoContainerException(
                    buildString {
                        append(Container::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }
    }
}
