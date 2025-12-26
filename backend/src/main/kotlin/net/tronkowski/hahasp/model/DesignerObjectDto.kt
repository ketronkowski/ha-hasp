package net.tronkowski.hahasp.model

data class DesignerObjectDto(
        val id: Int,
        val type: String,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val text: String? = null,
        val entityId: String? = null,
        val page: Int = 1,
        val color: String? = null,
        val backgroundColor: String? = null,
        val fontSize: Int? = null,
        val options: String? = null,
        val min: Int? = null,
        val max: Int? = null
)
