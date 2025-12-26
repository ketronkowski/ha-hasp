package net.tronkowski.hahasp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import net.tronkowski.hahasp.model.DesignerObject

@Composable
fun CanvasView(
        screenWidth: Int,
        screenHeight: Int,
        objects: List<DesignerObject>,
        selectedObjectId: Int?,
        onObjectClicked: (DesignerObject) -> Unit,
        onObjectMoved: (DesignerObject, Int, Int) -> Unit
) {
    Box(
            modifier =
                    Modifier.size(screenWidth.dp, screenHeight.dp)
                            .background(Color.Black)
                            .border(2.dp, Color.DarkGray)
    ) {
        objects.forEach { obj ->
            DesignerObjectView(
                    obj = obj,
                    isSelected = obj.id == selectedObjectId,
                    onClicked = { onObjectClicked(obj) },
                    onMove = { newX, newY -> onObjectMoved(obj, newX, newY) }
            )
        }
    }
}

@Composable
fun DesignerObjectView(
        obj: DesignerObject,
        isSelected: Boolean,
        onClicked: () -> Unit,
        onMove: (Int, Int) -> Unit
) {
    var offsetX by remember { mutableStateOf(obj.x.toFloat()) }
    var offsetY by remember { mutableStateOf(obj.y.toFloat()) }

    // Sync state when props change (manual sync for now)
    LaunchedEffect(obj.x, obj.y) {
        offsetX = obj.x.toFloat()
        offsetY = obj.y.toFloat()
    }

    Box(
            modifier =
                    Modifier.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .size(obj.width.dp, obj.height.dp)
                            .background(
                                    when (obj.type) {
                                        "btn" -> Color(0xFF444444)
                                        "sw" -> Color(0xFF333333)
                                        "slider" -> Color(0xFF222222)
                                        else -> Color.Transparent
                                    },
                                    RoundedCornerShape(4.dp)
                            )
                            .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color =
                                            if (isSelected) Color(0xFF2196F3)
                                            else Color(0xFFBBBBBB),
                                    shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onClicked() }
                            .pointerInput(Unit) {
                                detectDragGestures(onDragStart = { onClicked() }) {
                                        change,
                                        dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                    onMove(offsetX.roundToInt(), offsetY.roundToInt())
                                }
                            },
            contentAlignment = Alignment.Center
    ) {
        when (obj.type) {
            "btn", "label" -> {
                Text(
                        text = obj.text ?: obj.type,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(2.dp)
                )
            }
            "checkbox" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).border(1.dp, Color.White))
                    Spacer(Modifier.width(4.dp))
                    Text(obj.text ?: "", color = Color.White, fontSize = 10.sp)
                }
            }
            "sw" -> {
                Box(
                        Modifier.size(30.dp, 16.dp)
                                .background(Color.Gray, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                ) { Box(Modifier.size(12.dp).background(Color.White, RoundedCornerShape(6.dp))) }
            }
            "slider" -> {
                Box(Modifier.fillMaxWidth(0.8f).height(4.dp).background(Color.Gray)) {
                    Box(
                            Modifier.size(8.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .align(Alignment.CenterStart)
                    )
                }
            }
            "dropdown" -> {
                Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(obj.text ?: "Select...", color = Color.White, fontSize = 10.sp)
                    Text("▼", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}
