package son.ysy.creator.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import son.ysy.creator.annotations.KeyCreator
import java.security.MessageDigest
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = ["son.ysy.creator.annotations.KeyCreator"])
class KeyCreatorPlugin : AbstractProcessor() {

    private lateinit var elementsUtils: Elements
    private lateinit var messager: Messager
    private lateinit var filer: Filer
    private lateinit var md5Digest: MessageDigest

    private val keyConfigs = mutableListOf<KeyConfig>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementsUtils = processingEnv.elementUtils
        messager = processingEnv.messager
        filer = processingEnv.filer
        md5Digest = MessageDigest.getInstance("MD5")
        keyConfigs.clear()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (roundEnv.processingOver()) {
            createKtFile()
        } else {
            readAnnotationData(roundEnv)
        }
        return true
    }

    private fun readAnnotationData(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(KeyCreator::class.java)
            .map { element ->
                val pkg = elementsUtils.getPackageOf(element).qualifiedName.toString()
                val parentClassList = element.toString()
                    .replace(pkg, "")
                    .split(".")
                    .filterNot { it.isBlank() }
                val className = if (parentClassList.size == 1) {
                    "$pkg.${parentClassList.first()}"
                } else {
                    "$pkg.${parentClassList.first()}${
                        parentClassList.subList(1, parentClassList.size)
                            .joinToString("$", prefix = "$")
                    }"
                }
                element.getAnnotation(KeyCreator::class.java).keys.toList().run {

                    if (any { it.isBlank() }) {
                        messager.printMessage(
                            Diagnostic.Kind.ERROR, "has blank key in class[$className]", element
                        )
                    }

                    val repeatKeyList = asSequence().groupBy { it }
                        .filter { it.value.size > 1 }
                        .map { it.key }
                    if (repeatKeyList.isNotEmpty()) {
                        messager.printMessage(
                            Diagnostic.Kind.ERROR, repeatKeyList.joinToString(
                                ",",
                                prefix = "keys:[",
                                postfix = "] in class[$className] has more than two!"
                            ), element
                        )
                    }

                    val containsEmptyKeys = asSequence().filter {
                        it.contains(" ", true)
                    }.toList()

                    if (containsEmptyKeys.isNotEmpty()) {
                        messager.printMessage(
                            Diagnostic.Kind.ERROR, containsEmptyKeys.joinToString(
                                ",",
                                prefix = "keys:[",
                                postfix = "] in class[$className] contains empty!Keys must start with letter ,and keys can just include letter,number and _."
                            ), element
                        )
                    }
                    val incorrectKeys = asSequence().filterNot {
                        it.matches(Regex("^[A-z][\\w]*\$"))
                    }.toList()

                    if (incorrectKeys.isNotEmpty()) {
                        messager.printMessage(
                            Diagnostic.Kind.ERROR, incorrectKeys.joinToString(
                                ",",
                                prefix = "keys:[",
                                postfix = "] in class $className incorrect!Keys must start with letter ,and keys can just include letter,number and _."
                            ), element
                        )
                    }
                    KeyConfig(pkg, this, parentClassList)
                }
            }
            .apply(keyConfigs::addAll)
    }

    private fun createKtFile() {
        keyConfigs.distinctBy {
            it.pkg + "." + it.classList.first()
        }.map {
            KeyFileConfig(it.pkg, it.classList.first(), getTopClass(it))
        }.forEach { fileConfig ->
            FileSpec.builder(fileConfig.pkg, fileConfig.keyFileName)
                .apply {
                    addType(createObjetClass(fileConfig.classConfig, fileConfig.pkg, emptyList()))
                }.build()
                .apply {
                    writeTo(filer)
                }
        }
    }

    private fun getTopClass(keyConfig: KeyConfig): KeyClassConfig {
        val className = keyConfig.classList.first()
        val childClass = getChildClass(keyConfig, listOf(className))
        return KeyClassConfig(className, keyConfig.keyNames, childClass)
    }

    private fun getChildClass(
        keyConfig: KeyConfig,
        parentClassName: List<String>
    ): List<KeyClassConfig> {
        return keyConfigs.filter {
            it.pkg == keyConfig.pkg && it.isDirectChildOf(parentClassName)
        }.map {
            val className = it.classList[parentClassName.size]
            KeyClassConfig(className, it.keyNames, getChildClass(it, parentClassName + className))
        }
    }

    private fun createObjetClass(
        classConfig: KeyClassConfig,
        pkg: String,
        parentClass: List<String>
    ): TypeSpec {
        return TypeSpec.objectBuilder(classConfig.keyClassName)
            .apply {
                classConfig.keys
                    .forEach { key ->
                        addProperty(
                            PropertySpec.builder(key, String::class, KModifier.CONST)
                                .initializer(
                                    "%S",
                                    parentClass.joinToString(
                                        ".",
                                        prefix = pkg,
                                        postfix = ".${classConfig.className}.${key}"
                                    ).toByteArray()
                                        .run(md5Digest::digest)
                                        .joinToString("") {
                                            "%02X".format(it)
                                        }
                                )
                                .build()
                        )
                    }
                classConfig.childClasses
                    .map {
                        createObjetClass(it, pkg, parentClass + classConfig.className)
                    }.apply(::addTypes)
            }
            .build()
    }
}