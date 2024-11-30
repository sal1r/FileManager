package salir.fm.presentation.screens.filebrowser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import salir.fm.R
import salir.fm.domain.models.FileModel
import salir.fm.presentation.ui.theme.FileManagerTheme

@Composable
fun File(
    model: FileModel,
    filePreview: ImageBitmap? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filePreview?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        } ?: Icon(
            painter = with(model.mimeType) {
                when {
                    this == "application/pdf" -> painterResource(R.drawable.ic_file_pdf_48)
                    this == "application/zip" -> painterResource(R.drawable.ic_file_zip_48)
                    this == "application/vnd.android.package-archive" -> painterResource(R.drawable.ic_file_apk_48)
                    startsWith("text") || this == "application/msword" -> painterResource(R.drawable.ic_file_doc_48)
                    startsWith("video") -> painterResource(R.drawable.ic_file_video_48)
                    startsWith("audio") -> painterResource(R.drawable.ic_file_audio_48)
                    startsWith("image") -> painterResource(R.drawable.ic_file_image_48)
                    else -> with(model) {
                        when {
                            extension == "docx" -> painterResource(R.drawable.ic_file_doc_48)
                            else -> painterResource(R.drawable.ic_file_48)
                        }
                    }
                }
            },
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            Text(
                text = model.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = model.size.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Preview
@Composable
private fun FilePreview() {
    FileManagerTheme {
        Surface {
            File(
                model = FileModel.EMPTY
            )
        }
    }
}