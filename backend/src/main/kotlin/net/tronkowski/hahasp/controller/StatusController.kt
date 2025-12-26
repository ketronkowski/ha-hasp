package net.tronkowski.hahasp.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class StatusController {
    
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf("status" to "Backend is running")
    }
}
