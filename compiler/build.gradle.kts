import son.ysy.useful.dependencies.AndroidDependency

plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
    id("com.novoda.bintray-release")
}

val userOrgR: String = rootProject.ext.get("userOrg").toString()
val repoNameR: String = rootProject.ext.get("repoName").toString()
val groupIdR: String = rootProject.ext.get("groupId").toString()
val publishVersionR: String = rootProject.ext.get("publishVersion").toString()
val websiteR: String = rootProject.ext.get("website").toString()

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

configure<com.novoda.gradle.release.PublishExtension> {
    userOrg = userOrgR
    repoName = repoNameR
    groupId = groupIdR
    artifactId = "compiler"
    publishVersion = publishVersionR
    desc = "key creator compiler"
    website = websiteR
}