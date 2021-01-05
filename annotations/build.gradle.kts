import son.ysy.useful.dependencies.AndroidDependency

plugins {
    id("java-library")
    id("kotlin")
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


dependencies {
    implementation(AndroidDependency.Kotlin.Stdlib.fullGradle)
}


configure<com.novoda.gradle.release.PublishExtension> {
    userOrg = userOrgR
    repoName = repoNameR
    groupId = groupIdR
    artifactId = "annotations"
    publishVersion = publishVersionR
    desc = "key creator annotations"
    website = websiteR
}