package son.ysy.creator.compiler

data class KeyConfig(
    val pkg: String,
    val name: String,
    val originClass: List<String>,
    val keys: List<String>
) {

    val clzName: String = name.ifBlank { originClass.joinToString("_", postfix = "Keys") }
}