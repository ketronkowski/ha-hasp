package net.tronkowski.hahasp.controller

import net.tronkowski.hahasp.model.*
import net.tronkowski.hahasp.service.ConfigPublisherService
import net.tronkowski.hahasp.service.HomeAssistantService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/config")
class ConfigController(
        private val publisherService: ConfigPublisherService,
        private val haService: HomeAssistantService
) {

    @PostMapping("/publish")
    fun publish(@RequestBody objects: List<DesignerObjectDto>): Map<String, String> {
        publisherService.publishConfig("pages.jsonl", objects)
        haService.reloadPages()
        return mapOf("status" to "Config published and reload triggered")
    }

    @PostMapping("/layout")
    fun saveLayout(@RequestBody objects: List<DesignerObjectDto>): Map<String, String> {
        publisherService.saveLayout(objects)
        return mapOf("status" to "Layout saved")
    }

    @GetMapping("/layout")
    fun loadLayout(): List<DesignerObjectDto> {
        return publisherService.loadLayout()
    }
}
