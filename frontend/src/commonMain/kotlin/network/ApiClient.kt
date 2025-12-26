package net.tronkowski.hahasp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.tronkowski.hahasp.model.DesignerObject

object ApiClient {
    val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    suspend fun fetchEntities(): List<Map<String, Any>> {
        return try {
            client.get("http://localhost:8080/api/ha/entities").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun publishConfig(objects: List<DesignerObject>): String {
        return try {
            val response: Map<String, String> =
                    client
                            .post("http://localhost:8080/api/config/publish") {
                                contentType(io.ktor.http.ContentType.Application.Json)
                                setBody(objects)
                            }
                            .body()
            response["status"] ?: "Success"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun saveLayout(objects: List<DesignerObject>): String {
        return try {
            val response: Map<String, String> =
                    client
                            .post("http://localhost:8080/api/config/layout") {
                                contentType(io.ktor.http.ContentType.Application.Json)
                                setBody(objects)
                            }
                            .body()
            response["status"] ?: "Success"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun fetchLayout(): List<DesignerObject> {
        return try {
            client.get("http://localhost:8080/api/config/layout").body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
