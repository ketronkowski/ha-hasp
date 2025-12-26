package net.tronkowski.hahasp.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class HaspObject(
        val page: Int,
        val id: Int,
        val obj: String,
        val x: Int? = null,
        val y: Int? = null,
        val w: Int? = null,
        val h: Int? = null,
        val text: String? = null,
        val entity: String? = null,
        val action: String? = null,
        val color: String? = null,
        val bg_color: String? = null,
        val font_size: Int? = null,
        val vis: Boolean? = null,
        val enabled: Boolean? = null
)

class HaspButton(
        page: Int,
        id: Int,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        text: String,
        entity: String? = null
) : HaspObject(page, id, "btn", x, y, w, h, text, entity)

class HaspLabel(page: Int, id: Int, x: Int, y: Int, text: String) :
        HaspObject(page, id, "label", x, y, text = text)

class HaspSlider(
        page: Int,
        id: Int,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        val min: Int = 0,
        val max: Int = 100,
        val val_now: Int = 0,
        entity: String? = null
) : HaspObject(page, id, "slider", x, y, w, h, entity = entity)

class HaspCheckbox(
        page: Int,
        id: Int,
        x: Int,
        y: Int,
        text: String,
        val value: Boolean = false,
        entity: String? = null
) : HaspObject(page, id, "checkbox", x, y, text = text, entity = entity)

class HaspSwitch(
        page: Int,
        id: Int,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        val value: Boolean = false,
        entity: String? = null
) : HaspObject(page, id, "sw", x, y, w, h, entity = entity)

class HaspDropdown(
        page: Int,
        id: Int,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        val options: String,
        entity: String? = null
) : HaspObject(page, id, "dropdown", x, y, w, h, entity = entity)
