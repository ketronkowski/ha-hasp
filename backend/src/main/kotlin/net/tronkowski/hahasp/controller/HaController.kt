package net.tronkowski.hahasp.controller

import net.tronkowski.hahasp.service.HomeAssistantService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ha")
class HaController(private val haService: HomeAssistantService) {

    @GetMapping("/entities")
    fun getEntities(): List<Map<String, Any>> {
        return haService.getEntities()
    }

    @PostMapping("/reload")
    fun reloadPages() {
        haService.reloadPages()
    }
}
