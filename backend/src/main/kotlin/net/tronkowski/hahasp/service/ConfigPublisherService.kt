package net.tronkowski.hahasp.service

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.nio.file.Paths
import net.tronkowski.hahasp.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigPublisherService(
        private val objectMapper: ObjectMapper,
        @Value("\${ha.config.path:/config/openhasp}") private val configPath: String
) {

    fun generateJsonl(objects: List<HaspObject>): String {
        return objects.joinToString("\n") { objectMapper.writeValueAsString(it) } + "\n"
    }

    fun publishConfig(filename: String, dtos: List<DesignerObjectDto>) {
        val objects =
                dtos.map { dto ->
                    when (dto.type) {
                        "btn" ->
                                HaspButton(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.width,
                                        dto.height,
                                        dto.text ?: "",
                                        dto.entityId
                                )
                        "label" -> HaspLabel(dto.page, dto.id, dto.x, dto.y, dto.text ?: "")
                        "slider" ->
                                HaspSlider(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.width,
                                        dto.height,
                                        dto.min ?: 0,
                                        dto.max ?: 100,
                                        entity = dto.entityId
                                )
                        "checkbox" ->
                                HaspCheckbox(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.text ?: "",
                                        entity = dto.entityId
                                )
                        "sw" ->
                                HaspSwitch(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.width,
                                        dto.height,
                                        entity = dto.entityId
                                )
                        "dropdown" ->
                                HaspDropdown(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.width,
                                        dto.height,
                                        dto.options ?: "",
                                        entity = dto.entityId
                                )
                        else ->
                                HaspButton(
                                        dto.page,
                                        dto.id,
                                        dto.x,
                                        dto.y,
                                        dto.width,
                                        dto.height,
                                        dto.text ?: "",
                                        dto.entityId
                                )
                    }
                }
        val content = generateJsonl(objects)
        val directory = Paths.get(configPath).toFile()
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, filename)
        file.writeText(content)
    }

    fun saveLayout(objects: List<DesignerObjectDto>) {
        val directory = Paths.get(configPath).toFile()
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "designer_layout.json")
        objectMapper.writeValue(file, objects)
    }

    fun loadLayout(): List<DesignerObjectDto> {
        val file = File(configPath, "designer_layout.json")
        return if (file.exists()) {
            objectMapper.readValue(
                    file,
                    objectMapper.typeFactory.constructCollectionType(
                            List::class.java,
                            DesignerObjectDto::class.java
                    )
            )
        } else {
            emptyList()
        }
    }
}
