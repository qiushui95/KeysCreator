import son.ysy.useful.dependencies.AndroidDependency

plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
    id("com.github.dcendents.android-maven")
}

group = extra["groupId"].toString()
setProperty("archivesBaseName", "compiler")


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kapt {
    includeCompileClasspath = false
}


dependencies {
    implementation(AndroidDependency.Kotlin.Stdlib.fullGradle)
    implementation("com.google.auto.service:auto-service-annotations:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation(project(":annotations"))
}
