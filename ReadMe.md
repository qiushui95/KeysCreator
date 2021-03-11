# KeyCreator(Key生成器)[![](https://jitpack.io/v/qiushui95/KeysCreator.svg)](https://jitpack.io/#qiushui95/KeysCreator)

解放双手,去除烦恼，不在为key相同造成的bug烦恼。

## 1.安装

### 1.1 项目build.gradle.kts

```gradle
allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }
}
```

### 1.2模块build.gradle.kts

```gradle
plugins {
    #略..
    id("kotlin-kapt")
}

dependencies {
	val keyVersion=last_version
	implementation("son.ysy.key.creator:annotations:$keyVersion")
    kapt("son.ysy.key.creator:compiler:$keyVersion")
}
```

## 2.使用

### 2.1

在需要的类上方加入注解@KeyCreator

```kotlin
@KeyCreator(keys = ["key1", "key2"])
class TestKeyCreator {

}
```



### 2.2

按ctrl+f9或者Build-Make Project

构建完成后会生成

```kotlin
package son.ysy.creator.keys

import kotlin.String

public object TestKeyCreatorKeys {
  public const val key1: String = "5A83447DFE52DA50C596984B3AF34197"

  public const val key2: String = "A73939408336ABEA1A1F316CFA959458"
}
```

### 2.3

直接食用

```kotlin
@KeyCreator(keys = ["key1", "key2"])
class TestKeyCreator {

    init {
        val key1 = TestKeyCreatorKeys.key1
    }
}
```

