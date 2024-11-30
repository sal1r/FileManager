package salir.fm.presentation.screens.filebrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import salir.fm.R
import salir.fm.domain.models.DirectoryModel
import salir.fm.presentation.ui.theme.FileManagerTheme

@Composable
fun Directory(
    model: DirectoryModel,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_folder_48),
            contentDescription = model.name,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = model.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )

    }
}

@Preview
@Composable
private fun DirectoryPreview() {
    FileManagerTheme {
        Surface {
            Directory(
                model = DirectoryModel(
                    name = "Documents",
                    path = "",
                    parentPath = ""
                )
            )
        }
    }
}