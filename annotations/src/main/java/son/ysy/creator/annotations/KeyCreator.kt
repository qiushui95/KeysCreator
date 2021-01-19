package son.ysy.creator.annotations

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class KeyCreator(vararg val keys: String, val name: String = "")