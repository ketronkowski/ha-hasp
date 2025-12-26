package net.tronkowski.hahasp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.tronkowski.hahasp.model.DesignerObject
import net.tronkowski.hahasp.model.DesignerPage
import net.tronkowski.hahasp.model.DesignerState
import net.tronkowski.hahasp.network.ApiClient
import net.tronkowski.hahasp.ui.CanvasView

@Composable
fun App() {
    var entities by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var designerState by remember { mutableStateOf(DesignerState()) }
    var selectedObject by remember { mutableStateOf<DesignerObject?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            entities = ApiClient.fetchEntities()
            val savedObjects = ApiClient.fetchLayout()
            if (savedObjects.isNotEmpty()) {
                val pagesMap = savedObjects.groupBy { it.page }
                val newPages =
                        pagesMap.entries
                                .sortedBy { it.key }
                                .map { (pageId, objects) ->
                                    DesignerPage(pageId, objects.toMutableList())
                                }
                                .toMutableList()

                if (newPages.isNotEmpty()) {
                    designerState = designerState.copy(pages = newPages, currentPageIndex = 0)
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar: Entities
            Column(
                    modifier =
                            Modifier.width(280.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFF2D2D2D))
                                    .padding(12.dp)
            ) {
                Text("Entities", color = Color.White, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(12.dp))

                if (errorMessage != null) {
                    Text("Error: $errorMessage", color = Color.Red, fontSize = 11.sp)
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(entities) { entity ->
                        val entityId = entity["entity_id"] as? String ?: "unknown"
                        Card(
                                modifier =
                                        Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                            val attributes = entity["attributes"] as? Map<*, *>
                                            val name =
                                                    attributes?.get("friendly_name") as? String
                                                            ?: entityId

                                            val newObj =
                                                    DesignerObject(
                                                            id =
                                                                    designerState.pages[
                                                                                    designerState
                                                                                            .currentPageIndex]
                                                                            .objects
                                                                            .size + 1,
                                                            type = "btn",
                                                            x = 20,
                                                            y = 20,
                                                            width = 120,
                                                            height = 40,
                                                            text = name,
                                                            entityId = entityId
                                                    )
                                            designerState.pages[designerState.currentPageIndex]
                                                    .objects.add(newObj)
                                            selectedObject = newObj
                                            designerState = designerState.copy()
                                        },
                                backgroundColor = Color(0xFF3D3D3D),
                                contentColor = Color.White,
                                elevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(entityId, fontSize = 10.sp, color = Color.LightGray)
                                Text(
                                        (entity["attributes"] as? Map<*, *>)?.get(
                                                "friendly_name"
                                        ) as?
                                                String
                                                ?: "",
                                        fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Central Area
            Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF1E1E1E)),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Designer")
                                Spacer(Modifier.width(16.dp))
                                // Page Switcher
                                designerState.pages.forEachIndexed { index, _ ->
                                    Button(
                                            onClick = {
                                                designerState =
                                                        designerState.copy(currentPageIndex = index)
                                            },
                                            colors =
                                                    ButtonDefaults.buttonColors(
                                                            backgroundColor =
                                                                    if (designerState
                                                                                    .currentPageIndex ==
                                                                                    index
                                                                    )
                                                                            Color(0xFF2196F3)
                                                                    else Color(0xFF444444)
                                                    ),
                                            modifier =
                                                    Modifier.padding(horizontal = 4.dp)
                                                            .height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("P${index + 1}", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                                IconButton(
                                        onClick = {
                                            designerState.pages.add(
                                                    DesignerPage(designerState.pages.size + 1)
                                            )
                                            designerState =
                                                    designerState.copy(
                                                            currentPageIndex =
                                                                    designerState.pages.size - 1
                                                    )
                                        }
                                ) {
                                    Icon(
                                            androidx.compose.material.icons.Icons.Default.Add,
                                            "Add Page",
                                            tint = Color.White
                                    )
                                }
                            }
                        },
                        backgroundColor = Color(0xFF333333),
                        contentColor = Color.White
                )

                Box(
                        modifier =
                                Modifier.weight(1f).fillMaxWidth().clickable {
                                    selectedObject = null
                                },
                        contentAlignment = Alignment.Center
                ) {
                    CanvasView(
                            screenWidth = designerState.screenWidth,
                            screenHeight = designerState.screenHeight,
                            objects = designerState.pages[designerState.currentPageIndex].objects,
                            selectedObjectId = selectedObject?.id,
                            onObjectClicked = { selectedObject = it },
                            onObjectMoved = { obj, newX, newY ->
                                obj.x = newX
                                obj.y = newY
                                designerState = designerState.copy()
                            }
                    )
                }

                BottomAppBar(backgroundColor = Color(0xFF333333)) {
                    var isPublishing by remember { mutableStateOf(false) }
                    var isSaving by remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()

                    Spacer(Modifier.weight(1f))
                    Button(
                            onClick = {
                                isSaving = true
                                coroutineScope.launch {
                                    val status =
                                            ApiClient.saveLayout(
                                                    designerState.pages.flatMap { it.objects }
                                            )
                                    errorMessage = "Status: $status"
                                    isSaving = false
                                }
                            },
                            enabled = !isSaving,
                            colors =
                                    ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFF2196F3)
                                    ),
                            modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                            )
                        } else {
                            Text("Save Layout", color = Color.White)
                        }
                    }

                    Button(
                            onClick = {
                                isPublishing = true
                                coroutineScope.launch {
                                    val status =
                                            ApiClient.publishConfig(
                                                    designerState.pages.flatMap { it.objects }
                                            )
                                    errorMessage = "Status: $status"
                                    isPublishing = false
                                }
                            },
                            enabled = !isPublishing,
                            colors =
                                    ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                            )
                        } else {
                            Text("Deploy to Device", color = Color.White)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                }
            }

            // Sidebar: Properties
            Column(
                    modifier =
                            Modifier.width(280.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFF2D2D2D))
                                    .padding(12.dp)
            ) {
                Text("Properties", color = Color.White, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))

                selectedObject?.let { obj ->
                    Text("Type:", color = Color.LightGray, fontSize = 12.sp)
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("btn", "label", "slider", "checkbox", "sw", "dropdown").forEach {
                                type ->
                            Button(
                                    onClick = {
                                        obj.type = type
                                        designerState = designerState.copy()
                                    },
                                    colors =
                                            ButtonDefaults.buttonColors(
                                                    backgroundColor =
                                                            if (obj.type == type) Color(0xFF2196F3)
                                                            else Color(0xFF444444)
                                            ),
                                    contentPadding = PaddingValues(4.dp),
                                    modifier = Modifier.weight(1f).height(24.dp)
                            ) { Text(type, fontSize = 9.sp, color = Color.White) }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Bound Entity:", color = Color.LightGray, fontSize = 12.sp)
                    Text(obj.entityId ?: "None", color = Color(0xFF4CAF50), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                            value = obj.text ?: "",
                            onValueChange = {
                                obj.text = it
                                designerState = designerState.copy()
                            },
                            label = { Text("Text") },
                            colors =
                                    TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = Color.White,
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.LightGray
                                    )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        OutlinedTextField(
                                value = obj.x.toString(),
                                onValueChange = {
                                    it.toIntOrNull()?.let { v ->
                                        obj.x = v
                                        designerState = designerState.copy()
                                    }
                                },
                                label = { Text("X") },
                                modifier = Modifier.weight(1f),
                                colors =
                                        TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = Color.White
                                        )
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                                value = obj.y.toString(),
                                onValueChange = {
                                    it.toIntOrNull()?.let { v ->
                                        obj.y = v
                                        designerState = designerState.copy()
                                    }
                                },
                                label = { Text("Y") },
                                modifier = Modifier.weight(1f),
                                colors =
                                        TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = Color.White
                                        )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        OutlinedTextField(
                                value = obj.width.toString(),
                                onValueChange = {
                                    it.toIntOrNull()?.let { v ->
                                        obj.width = v
                                        designerState = designerState.copy()
                                    }
                                },
                                label = { Text("W") },
                                modifier = Modifier.weight(1f),
                                colors =
                                        TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = Color.White
                                        )
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                                value = obj.height.toString(),
                                onValueChange = {
                                    it.toIntOrNull()?.let { v ->
                                        obj.height = v
                                        designerState = designerState.copy()
                                    }
                                },
                                label = { Text("H") },
                                modifier = Modifier.weight(1f),
                                colors =
                                        TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = Color.White
                                        )
                        )
                    }

                    if (obj.type == "slider") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            OutlinedTextField(
                                    value = obj.min?.toString() ?: "0",
                                    onValueChange = {
                                        it.toIntOrNull()?.let { v ->
                                            obj.min = v
                                            designerState = designerState.copy()
                                        }
                                    },
                                    label = { Text("Min") },
                                    modifier = Modifier.weight(1f),
                                    colors =
                                            TextFieldDefaults.outlinedTextFieldColors(
                                                    textColor = Color.White
                                            )
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                    value = obj.max?.toString() ?: "100",
                                    onValueChange = {
                                        it.toIntOrNull()?.let { v ->
                                            obj.max = v
                                            designerState = designerState.copy()
                                        }
                                    },
                                    label = { Text("Max") },
                                    modifier = Modifier.weight(1f),
                                    colors =
                                            TextFieldDefaults.outlinedTextFieldColors(
                                                    textColor = Color.White
                                            )
                            )
                        }
                    }

                    if (obj.type == "dropdown") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                                value = obj.options ?: "",
                                onValueChange = {
                                    obj.options = it
                                    designerState = designerState.copy()
                                },
                                label = { Text("Options (comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = Color.White
                                        )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                designerState.pages[designerState.currentPageIndex].objects.remove(
                                        obj
                                )
                                selectedObject = null
                                designerState = designerState.copy()
                            },
                            colors =
                                    ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F))
                    ) { Text("Delete Object", color = Color.White) }
                }
                        ?: run {
                            Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text(
                                        "Select an object to edit",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                )
                            }
                        }
            }
        }
    }
}
