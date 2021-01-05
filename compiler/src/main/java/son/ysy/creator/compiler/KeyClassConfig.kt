package son.ysy.creator.compiler

data class KeyClassConfig(
     val className: String,
    val keys: List<String>,
    val childClasses: List<KeyClassConfig>
) {
    val keyClassName: String = "${className}Keys"
}