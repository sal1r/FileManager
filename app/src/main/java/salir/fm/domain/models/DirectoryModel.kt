package salir.fm.domain.models

class DirectoryModel(
    override val name: String,
    override val path: String,
    override val parentPath: String
) : FileSystemUnit() {

    companion object {
        val EMPTY = DirectoryModel("", "", "")
    }
}