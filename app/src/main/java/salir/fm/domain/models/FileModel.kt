package salir.fm.domain.models

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

class FileModel(
    override val name: String,
    override val path: String,
    override val parentPath: String,
    val mimeType: String,
    val extension: String,
    val size: Long
) : FileSystemUnit() {

    companion object {
        val EMPTY = FileModel("", "", "", "", "", 0)
    }
}