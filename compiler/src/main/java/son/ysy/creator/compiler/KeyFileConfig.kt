package son.ysy.creator.compiler

data class KeyFileConfig(
    val pkg: String,
    private val fileName: String,
    val classConfig: KeyClassConfig
) {

    val keyFileName: String = "${fileName}Keys"
}