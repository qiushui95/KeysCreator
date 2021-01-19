package son.ysy.creator.compiler

data class KeyFileConfig(
    val pkg: String,
    val fileName: String,
    val classConfig: KeyClassConfig
)