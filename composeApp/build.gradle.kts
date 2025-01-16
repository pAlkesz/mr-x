import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.squareup.wire)
}

kotlin {
    androidTarget {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Common module for the Mr. X app"
        homepage = "Common Module homepage"
        version = "1.0"
        ios.deploymentTarget = "17.0"
        pod("FirebaseCore", linkOnly = true)
        pod("FirebaseFirestore", linkOnly = true)
        pod("FirebaseAuth", linkOnly = true)
        pod("FirebaseCrashlytics", linkOnly = true)
        pod("FirebaseMessaging", linkOnly = true)
        pod("FirebaseAppCheck", linkOnly = true)
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.rinku)
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.animation.ExperimentalAnimationApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("kotlin.experimental.ExperimentalNativeApi")
            }
        }
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.firebase.bom.get()))
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.firebase.crashlytics.android)
            implementation(libs.firebase.analytics.android)
            implementation(libs.firebase.messaging.android)
            implementation(libs.firebase.appcheck.playintegrity.android)
            implementation(libs.firebase.appcheck.debug.android)
        }
        commonMain.dependencies {
            api(project.dependencies.platform(libs.androidx.compose.composeBom))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.compose.navigation)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.firebase.firestore)
            implementation(libs.firebase.common)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.crashlytics)

            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.qr.kit)
            api(libs.rinku)
            implementation(libs.rinku.compose.ext)
            implementation(libs.aakira.napier)
            implementation(libs.plusmobileapps.konnectivity)
            implementation(libs.crashkios.crashlytics)
            implementation(libs.doist.normalize)
            implementation(libs.androidx.datastore.okio)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotest.engine)
            implementation(libs.kotest.assertions)
            implementation(libs.kotest.property)
        }
    }
}

wire {
    kotlin {}
    sourcePath {
        srcDir("src/commonMain/proto")
    }
}

android {
    namespace = "com.palkesz.mr.x"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.palkesz.mr.x"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = System.getenv("MRX_MOBILE_APP_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("MRX_MOBILE_APP_VERSION_NAME") ?: "0.0.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    val localProperties = Properties().apply {
        with(File(rootProject.projectDir, "local.properties")) {
            if (exists()) {
                load(reader())
            }
        }
    }
    val isLocalBuild = !System.getenv("CI").toBoolean()
    if (isLocalBuild) {
        signingConfigs {
            register("release") {
                storeFile = File(localProperties.getProperty("keystore.path"))
                keyAlias = localProperties.getProperty("key.alias")
                storePassword = localProperties.getProperty("keystore.password")
                keyPassword = localProperties.getProperty("key.password")
            }
        }
    }
    buildTypes {
        release {
            if (isLocalBuild) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)
    }
}
