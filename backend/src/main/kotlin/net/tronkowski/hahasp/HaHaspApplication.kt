package net.tronkowski.hahasp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class HaHaspApplication

fun main(args: Array<String>) {
    runApplication<HaHaspApplication>(*args)
}
