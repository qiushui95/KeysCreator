// Top-level build file where you can add configuration options common to all sub-projects/modules.

ext {
    set("groupId", "son.ysy.key.creator")
    set("publishVersion", "1.0.4")
}

buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("com.github.qiushui95:AndroidDependencies:1.2.6")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")

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