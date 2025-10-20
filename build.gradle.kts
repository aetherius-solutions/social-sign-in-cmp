import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.1.20"
    id("com.android.library") version "8.9.2"
    id("org.jetbrains.compose") version "1.8.0-beta02"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.kotlin.native.cocoapods") version "2.1.20"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    cocoapods {
        version = "1.0.0"
        summary = "Social Sign In module"
        homepage = "https://your.website"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "socialsignin"
            isStatic = true
        }

        pod("GoogleSignIn") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

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
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                api(compose.components.resources)
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.10.1")
                api("androidx.compose.ui:ui-tooling-preview:1.8.0-beta02")
                implementation("com.google.android.gms:play-services-auth:21.0.0")
            }
        }
    }
}

android {
    namespace = "com.cmpmodular.socialsignin"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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
