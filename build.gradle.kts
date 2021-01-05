// Top-level build file where you can add configuration options common to all sub-projects/modules.

ext {
    set("userOrg", "974577817")
    set("repoName", "release")
    set("groupId", "son.ysy.key.creator")
    set("publishVersion", "1.0.2")
    set("website", "https://github.com/qiushui95/KeysCreator")
}

buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.github.qiushui95:AndroidDependencies:1.0.6")
        classpath("com.novoda:bintray-release:0.9.2")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}