plugins {
    alias(libs.plugins.android.application)
}

// Tự động cập nhật DB_ASSETS_VERSION trong MyDatabaseHelper.java
// mỗi khi file sqlite trong assets thay đổi.
tasks.register("syncDbVersion") {
    val sqliteFile = file("src/main/assets/k23411.sqlite")
    val helperFile = file("src/main/java/com/nhynhi/models/MyDatabaseHelper.java")

    inputs.file(sqliteFile)
    outputs.file(helperFile)

    doLast {
        if (!sqliteFile.exists()) return@doLast

        // Dùng lastModified (giây) làm version — tăng tự động mỗi khi file thay đổi
        val newVersion = (sqliteFile.lastModified() / 1000).toInt()

        val content = helperFile.readText()
        val updated = content.replace(
            Regex("private static final int DB_ASSETS_VERSION = \\d+;"),
            "private static final int DB_ASSETS_VERSION = $newVersion;"
        )

        if (content != updated) {
            helperFile.writeText(updated)
            println("DB_ASSETS_VERSION updated to $newVersion")
        } else {
            println("DB_ASSETS_VERSION unchanged ($newVersion)")
        }
    }
}

// Chạy syncDbVersion trước khi compile Java
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn("syncDbVersion")
}

android {
    namespace = "com.nhynhi.k23411tapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.nhynhi.k23411tapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.exp4j)
    implementation(libs.volley)
    implementation(libs.jsoup)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}