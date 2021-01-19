package son.ysy.creator.compiler


data class KeyConfigs(
    val pkg: String,
    val fileName: String,
    val keyNames: List<String>,
    val classList: List<String>
) {
    fun isDirectChildOf(parentClasses: List<String>) = classList.size == parentClasses.size + 1 &&
            classList.subList(0, parentClasses.size) == parentClasses
}