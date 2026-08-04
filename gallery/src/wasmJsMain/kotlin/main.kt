import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.composefluent.gallery.App
import io.github.composefluent.gallery.GalleryTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // `CanvasBasedWindow` was removed in Compose Multiplatform 1.11. ComposeViewport
    // creates and sizes its own canvas inside the container, so the title now comes
    // from the <title> element in index.html.
    val body = document.body ?: return
    ComposeViewport(body) {
        GalleryTheme {
            App()
        }
    }
}
