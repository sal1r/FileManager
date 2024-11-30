package salir.fm.presentation.viewmodels

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import salir.fm.domain.models.DirectoryModel
import salir.fm.domain.models.FileModel
import salir.fm.domain.models.FileSystemUnit
import java.io.File
import java.lang.ref.WeakReference
import java.util.WeakHashMap

class FileBrowserViewModel(
    val app: Application
): ViewModel() {

    private val _dirs = MutableStateFlow<List<String>>(listOf(Environment.getExternalStorageDirectory().absolutePath))
    val dirs = _dirs.asStateFlow()

    private val _files = MutableStateFlow<List<FileSystemUnit>>(emptyList())
    val files = _files.asStateFlow()

    init {
        loadDirFiles(Environment.getExternalStorageDirectory().absolutePath)

        viewModelScope.launch(Dispatchers.Default) {
            _dirs.collectLatest {
                loadDirFiles(it.last())
            }
        }

//        viewModelScope.launch(Dispatchers.IO) {
//            _files.collectLatest {
//                it.forEach { file ->
//                    yield()
//                    if (file !is FileModel) return@forEach
//
//                    with(file.mimeType) {
//                        when {
//                            this == "application/vnd.android.package-archive" -> {
//                                val packageInfo = try {
//                                    app.packageManager.getPackageArchiveInfo(
//                                        file.path, PackageManager.GET_META_DATA
//                                    )
//                                } catch (e: Exception) { return@forEach }
//
//                                packageInfo?.applicationInfo?.sourceDir = file.path
//                                packageInfo?.applicationInfo?.publicSourceDir = file.path
//
//                                val drawable = packageInfo
//                                    ?.applicationInfo
//                                    ?.loadIcon(app.packageManager)
//                                file.setImage(drawable?.toBitmap()?.asImageBitmap())
//                            }
//
//                            startsWith("image") -> {
//                                file.setImage(BitmapFactory.decodeFile(file.path)?.asImageBitmap())
//                            }
//
//                            startsWith("video") -> {
//                                file.setImage(
//                                    ThumbnailUtils.createVideoThumbnail(
//                                        file.path,
//                                        MediaStore.Images.Thumbnails.MINI_KIND
//                                    )?.asImageBitmap()
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    fun returnToParentDir() {
        viewModelScope.launch(Dispatchers.Default) {
            if (_dirs.value.size > 1) {
                _dirs.value = _dirs.value.toMutableList().apply { removeAt(lastIndex) }
            }
        }
    }

    fun goToDir(dir: DirectoryModel) {
        _dirs.value = _dirs.value.toMutableList().apply { add(dir.path) }
    }

    suspend fun loadFilePreview(file: FileModel): ImageBitmap? {
        val result = viewModelScope.async<ImageBitmap?>(Dispatchers.IO) {
            with (file.mimeType) {
                when {
                    this == "application/vnd.android.package-archive" -> {
                        val packageInfo = try {
                            app.packageManager.getPackageArchiveInfo(
                                file.path, PackageManager.GET_META_DATA
                            )
                        } catch (e: Exception) { return@async null }

                        packageInfo?.applicationInfo?.sourceDir = file.path
                        packageInfo?.applicationInfo?.publicSourceDir = file.path

                        val drawable = packageInfo
                            ?.applicationInfo
                            ?.loadIcon(app.packageManager)

                        return@async drawable?.toBitmap()?.asImageBitmap()
                    }

                    startsWith("image") -> {
                        return@async BitmapFactory.decodeFile(file.path)?.asImageBitmap()
                    }

                    startsWith("video") -> {
                        return@async ThumbnailUtils.createVideoThumbnail(
                        file.path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                        )?.asImageBitmap()
                    }

                    else -> { return@async null }
                }
            }
        }

        return result.await()
    }

    private fun loadDirFiles(dir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _files.value = File(dir).listFiles()?.mapNotNull {
                if (it.name.startsWith(".")) return@mapNotNull null

                if (it.isDirectory) {
                    DirectoryModel(
                        name = it.name,
                        path = it.absolutePath,
                        parentPath = it.parent ?: "/"
                    )
                } else {
                    FileModel(
                        name = it.name,
                        path = it.absolutePath,
                        parentPath = it.parent ?: "/",
                        mimeType = (MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension) ?: "*/*"),
                        extension = it.extension,
                        size = it.length()
                    )
                }
            }?.sortedWith { o1, o2 ->
                if (o1 is DirectoryModel && o2 is FileModel) -1
                else if (o1 is FileModel && o2 is DirectoryModel) 1
                else o1.name.compareTo(o2.name)
            } ?: emptyList()
        }
    }

    fun getIntentForOpenFile(file: FileModel, withMimeType: Boolean = true): Intent {
        val f = File(file.path)

        return Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(
                getUriForFile(file),
                if (withMimeType) file.mimeType else "*/*"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }

    private fun getUriForFile(file: FileModel): Uri {
        return FileProvider.getUriForFile(app, app.packageName +".provider", File(file.path))
    }
}