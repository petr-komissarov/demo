package com.komissarov.services

import com.google.gson.Gson
import com.komissarov.Settings
import com.komissarov.models.Payload
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

internal class Http(private val gson: Gson) {
    companion object {
        private val client by lazy { OkHttpClient() }
        private val logger by lazy { KotlinLogging.logger {} }
    }

    suspend fun post(payload: Payload) = withContext(Dispatchers.IO) {
        var result: Int? = null

        try {
            val body = gson
                .toJson(payload.toHttpMap())
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request
                .Builder()
                .url("${Settings.Http.BASE_URL}/api/events")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    result = 1
                } else {
                    logger.error { "HTTP response is not OK! Status: ${response.code}. Reason: ${response.message}" }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException, is IOException, is IllegalStateException -> {
                    logger.error(e) { "Http exception!" }
                }

                else -> logger.error(e) { "Unexpected http exception!" }
            }
        }

        return@withContext result
    }
}
