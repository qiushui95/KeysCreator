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
            verifyData()
            createKtFile()
        } else {
            readAnnotationData(roundEnv)
        }
        return true
    }

    private fun readAnnotationData(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(KeyCreator::class.java)
            .map { element ->
                val keyCreator = element.getAnnotation(KeyCreator::class.java)

                val pkg = elementsUtils.getPackageOf(element).qualifiedName.toString()

                val parentClassList = element.toString()
                    .replaceFirst(pkg, "")
                    .split(".")
                    .filterNot { it.isBlank() }

                KeyConfig(pkg, keyCreator.name, parentClassList, keyCreator.keys.toList())
//
//                val className = if (parentClassList.size == 1) {
//                    "$pkg.${parentClassList.first()}"
//                } else {
//                    "$pkg.${parentClassList.first()}${
//                        parentClassList.subList(1, parentClassList.size)
//                            .joinToString("$", prefix = "$")
//                    }"
//                }
//                keyCreator.keys.toList().run {
//
//                    if (any { it.isBlank() }) {
//                        messager.printMessage(
//                            Diagnostic.Kind.ERROR, "has blank key in class[$className]", element
//                        )
//                    }
//
//                    val repeatKeyList = asSequence().groupBy { it }
//                        .filter { it.value.size > 1 }
//                        .map { it.key }
//                    if (repeatKeyList.isNotEmpty()) {
//                        messager.printMessage(
//                            Diagnostic.Kind.ERROR, repeatKeyList.joinToString(
//                                ",",
//                                prefix = "keys:[",
//                                postfix = "] in class[$className] has more than two!"
//                            ), element
//                        )
//                    }
//
//                    val containsEmptyKeys = asSequence().filter {
//                        it.contains(" ", true)
//                    }.toList()
//
//                    if (containsEmptyKeys.isNotEmpty()) {
//                        messager.printMessage(
//                            Diagnostic.Kind.ERROR, containsEmptyKeys.joinToString(
//                                ",",
//                                prefix = "keys:[",
//                                postfix = "] in class[$className] contains empty!Keys must start with letter ,and keys can just include letter,number and _."
//                            ), element
//                        )
//                    }
//                    val incorrectKeys = asSequence().filterNot {
//                        it.matches(Regex("^[A-z][\\w]*\$"))
//                    }.toList()
//
//                    if (incorrectKeys.isNotEmpty()) {
//                        messager.printMessage(
//                            Diagnostic.Kind.ERROR, incorrectKeys.joinToString(
//                                ",",
//                                prefix = "keys:[",
//                                postfix = "] in class $className incorrect!Keys must start with letter ,and keys can just include letter,number and _."
//                            ), element
//                        )
//                    }
//                    if (keyCreator.name.isNotBlank() && keyCreator.name.matches(Regex("^[A-Z][A-z0-9_]*\$"))) {
//                        messager.printMessage(
//                            Diagnostic.Kind.ERROR,
//                            "KeyCreator.name must start with uppercase letter",
//                            element
//                        )
//                    }
//                    val clzName = keyCreator.name.ifBlank { parentClassList.first() + "Keys" }
//                    KeyConfigs(pkg, clzName, this, parentClassList)
//                }
            }
            .apply(keyConfigs::addAll)
    }

    private fun verifyData() {
        keyConfigs.groupBy {
            it.pkg + it.clzName
        }.filter {
            it.value.size > 1
        }.map {
            it.value.first()
        }.forEach {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "more one class named with ${it.clzName} in ${it.pkg}",
            )
        }


        keyConfigs.forEach { keyConfig ->

            val originClzName =
                keyConfig.originClass.joinToString("$")

            if (keyConfig.name.isNotBlank() && !keyConfig.name.matches(Regex("^[A-Z][A-z0-9_]*\$"))) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "KeyCreator.name must start with uppercase letter in ${keyConfig.pkg}.${originClzName}",
                )
            }


            keyConfig.keys.forEach { key ->
                if (!key.matches(Regex("^[A-z][A-z0-9_]*\$"))) {
                    messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "key must start with letter,so [$key] in ${keyConfig.pkg}.${originClzName} is incorrect!!",
                    )
                }
            }
            keyConfig.keys.groupBy { it }
                .filter { it.value.size > 1 }
                .map { it.key }
                .forEach {
                    messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "more than 1 same key[$it] in ${keyConfig.pkg}.${originClzName}!!",
                    )
                }
        }
    }

    private fun createKtFile() {
        keyConfigs.forEach { keyConfig ->
            FileSpec.builder(keyConfig.pkg, keyConfig.clzName)
                .addType(createObjectClass(keyConfig))
                .build()
                .writeTo(filer)
        }
    }

    private fun createObjectClass(keyConfig: KeyConfig): TypeSpec {
        return TypeSpec.objectBuilder(keyConfig.clzName)
            .addProperties(createProperties(keyConfig))
            .build()
    }

    private fun createProperties(keyConfig: KeyConfig): List<PropertySpec> {
        return keyConfig.keys.map {
            createProperty(it, keyConfig.pkg, keyConfig.originClass)
        }
    }

    private fun createProperty(key: String, pkg: String, originClass: List<String>): PropertySpec {
        return PropertySpec.builder(key, String::class, KModifier.CONST)
            .initializer(
                "%S",
                originClass.joinToString(
                    "$",
                    prefix = "${pkg}.",
                    postfix = ".$key"
                ).toByteArray()
                    .run(md5Digest::digest)
                    .joinToString("") {
                        "%02X".format(it)
                    }
            )
            .build()
    }
}