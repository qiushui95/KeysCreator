import son.ysy.useful.dependencies.AndroidDependency

plugins {
    id("java-library")
    id("kotlin")
    id("com.github.dcendents.android-maven")
}

group = rootProject.extra["groupId"].toString()
setProperty("archivesBaseName", "annotations")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


dependencies {
    implementation(AndroidDependency.Kotlin.Stdlib.fullGradle)
}