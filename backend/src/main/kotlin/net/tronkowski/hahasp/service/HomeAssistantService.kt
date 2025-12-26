package net.tronkowski.hahasp.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class HomeAssistantService(
        private val webClientBuilder: WebClient.Builder,
        @Value("\${ha.url:https://ha-nh.tronkowski.org}") private val haUrl: String,
        @Value("\${ha.token}") private val haToken: String
) {
    private val webClient = webClientBuilder.baseUrl(haUrl).build()

    fun getEntities(): List<Map<String, Any>> {
        return webClient
                .get()
                .uri("/api/states")
                .header("Authorization", "Bearer $haToken")
                .retrieve()
                .bodyToFlux(Map::class.java)
                .collectList()
                .map { list -> list.map { it as Map<String, Any> } }
                .block()
                ?: emptyList()
    }

    fun reloadPages() {
        webClient
                .post()
                .uri("/api/services/openhasp/load_pages")
                .header("Authorization", "Bearer $haToken")
                .retrieve()
                .bodyToMono(Void::class.java)
                .block()
    }
}
