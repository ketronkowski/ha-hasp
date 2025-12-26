import androidx.compose.material.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import net.tronkowski.hahasp.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("HA-HASP Designer") {
        Text("Hello from Wasm!")
    }
}
