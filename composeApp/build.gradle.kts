plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.jetbrainsCompose)
	alias(libs.plugins.googleServices)
	alias(libs.plugins.serialization)
}

kotlin {
	androidTarget {
		compilations.all {
			kotlinOptions {
				jvmTarget = "11"
			}
		}
	}

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "ComposeApp"
			isStatic = true
			export(libs.rinqu)
		}
	}

	sourceSets {
		all {
			languageSettings {
				// More compiler arguments can be added later if needed
				optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
				optIn("androidx.compose.material3.ExperimentalMaterial3Api")
				optIn("androidx.compose.animation.ExperimentalAnimationApi")
				optIn("androidx.compose.foundation.ExperimentalFoundationApi")
				optIn("kotlinx.cinterop.ExperimentalForeignApi")
				optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
			}
		}

		androidMain.dependencies {
			implementation(libs.compose.ui.tooling.preview)
			implementation(libs.androidx.activity.compose)
			implementation(project.dependencies.platform(libs.firebase.bom.get()))
			implementation(libs.koin.android)
			implementation(libs.koin.androidx.compose)
		}
		commonMain.dependencies {
			api(libs.rinqu)
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material)
			implementation(compose.ui)
			implementation(compose.material3)
			implementation(compose.components.resources) {
				version {
					strictly("1.6.2")
				}
			}
			implementation(compose.components.uiToolingPreview)

			/* If material3 isn't recognized at imports:
			Sync Gradle again or if it still doesn't work then:
			uncomment the next line->sync->comment/delete the line->sync again */
//			implementation("org.jetbrains.compose.material3:material3")
			implementation(libs.androidx.lifecycle.viewmodel.compose)

			implementation(libs.firebase.firestore)
			implementation(libs.firebase.common)
			implementation(libs.firebase.auth)
			implementation(libs.kotlinx.serialization.json)
			api(libs.koin.core)
			implementation(libs.koin.compose)
			implementation(libs.lifecycle.viewmodel)
			implementation(libs.navigation.compose)
			implementation(libs.kotlinx.collections.immutable)
			implementation(libs.qr.kit)
			implementation(libs.rinqu.compose.ext)
		}

	}
	/* This lines ensures that the app can be rebuilt, otherwise build fails due to not finding a
	Task named testClasses	*/
	task("testClasses") {}
}

android {
	namespace = "com.mr.x"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
	sourceSets["main"].res.srcDirs("src/androidMain/res")
	sourceSets["main"].resources.srcDirs("src/commonMain/resources")

	defaultConfig {
		applicationId = "com.mr.x"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 1
		versionName = "1.0"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	dependencies {
		debugImplementation(libs.compose.ui.tooling)
		implementation(libs.firebase.common.ktx)

	}
}
