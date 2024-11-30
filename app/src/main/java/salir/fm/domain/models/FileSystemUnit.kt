package salir.fm.domain.models

abstract class FileSystemUnit {

    abstract val name: String

    abstract val path: String

    abstract val parentPath: String
}