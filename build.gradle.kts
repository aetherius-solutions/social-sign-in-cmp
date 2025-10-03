import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "socialsignin"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(compose.preview)
            api(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
           api(compose.runtime)
           api(compose.foundation)
           api(compose.material3)
           api(compose.ui)
           api(compose.components.resources)
           api(compose.components.uiToolingPreview)

            implementation(libs.kotlinx.datetime)
        }
    }
}

android {
    namespace = "com.cmpmodular.socialsignin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"http://209.38.227.237/\"")
        }

        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"http://209.38.227.237/\"")
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}