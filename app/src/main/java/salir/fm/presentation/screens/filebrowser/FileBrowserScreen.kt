package salir.fm.presentation.screens.filebrowser

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import salir.fm.R
import salir.fm.domain.models.DirectoryModel
import salir.fm.domain.models.FileModel
import salir.fm.domain.models.FileSystemUnit
import salir.fm.presentation.ui.theme.FileManagerTheme
import salir.fm.presentation.viewmodels.FileBrowserViewModel

@Composable
fun FileBrowserScreen(vm: FileBrowserViewModel = koinViewModel()) {
    val files by vm.files.collectAsStateWithLifecycle()
    val dirs by vm.dirs.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    BackHandler {
        vm.returnToParentDir()
    }

    FileBrowserScreenContent(
        files = files,
        currentDir = dirs.last(),
        showBackButton = dirs.size > 1,
        onBackClick = vm::returnToParentDir,
        onDirClick = vm::goToDir,
        onFileClick = {
            try {
                launcher.launch(vm.getIntentForOpenFile(file = it, withMimeType = true))
            } catch (_: Exception) {
                try {
                    launcher.launch(vm.getIntentForOpenFile(file = it, withMimeType = false))
                } catch (_: Exception) { }
            }
        },
        loadFilePreview = vm::loadFilePreview
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileBrowserScreenContent(
    files: List<FileSystemUnit> = emptyList(),
    currentDir: String = "",
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onDirClick: (DirectoryModel) -> Unit = {},
    onFileClick: (FileModel) -> Unit = {},
    loadFilePreview: suspend (FileModel) -> ImageBitmap? = { null }
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentDir,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back_24),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onBackClick)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn {
                items(
                    items = files,
                    key = { it.path }
                ) {
                    when (it) {
                        is DirectoryModel -> Directory(
                            model = it,
                            onClick = { onDirClick(it) }
                        )
                        is FileModel -> {
                            var filePreview: ImageBitmap? by remember { mutableStateOf(null) }
                            LaunchedEffect(Unit) {
                                filePreview = loadFilePreview(it)
                            }

                            File(
                                model = it,
                                filePreview = filePreview,
                                onClick = { onFileClick(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FileBrowserScreenPreview() {
    FileManagerTheme {
        Surface {
            FileBrowserScreenContent(
                currentDir = "/storage/emulated/0",
                files = listOf(
                    FileModel.EMPTY,
                    DirectoryModel.EMPTY
                )
            )
        }
    }
}