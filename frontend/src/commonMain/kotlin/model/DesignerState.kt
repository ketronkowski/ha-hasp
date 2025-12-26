package net.tronkowski.hahasp.model

import kotlinx.serialization.Serializable

@Serializable
data class DesignerObject(
        val id: Int,
        var type: String, // "btn", "label", "slider", etc.
        var x: Int,
        var y: Int,
        var width: Int,
        var height: Int,
        var text: String? = null,
        var entityId: String? = null,
        var page: Int = 1,
        var color: String? = null,
        var backgroundColor: String? = null,
        var fontSize: Int? = null,
        var options: String? = null,
        var min: Int? = null,
        var max: Int? = null
)

@Serializable
data class DesignerPage(
        val pageNumber: Int,
        val objects: MutableList<DesignerObject> = mutableListOf()
)

@Serializable
data class DesignerState(
        val pages: MutableList<DesignerPage> = mutableListOf(DesignerPage(1)),
        var currentPageIndex: Int = 0,
        var screenWidth: Int = 320,
        var screenHeight: Int = 480
)
