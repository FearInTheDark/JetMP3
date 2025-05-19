import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.google.ksp)
	alias(libs.plugins.dagger.hilt)
	alias(libs.plugins.google.services)
	alias(libs.plugins.parcelize)
}

val localProperties = Properties().apply {
	load(rootProject.file("local.properties").inputStream())
}
val spotifyClientId = localProperties["SPOTIFY_CLIENT_ID"] as String?
val spotifyClientSecret = localProperties["SPOTIFY_CLIENT_SECRET"] as String?

android {
	namespace = "com.vincent.jetmp3"
	compileSdk = 35

	buildFeatures {
		buildConfig = true
	}

	defaultConfig {
		applicationId = "com.vincent.jetmp3"
		minSdk = 26
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$spotifyClientId\"")
		buildConfigField("String", "SPOTIFY_CLIENT_SECRET", "\"$spotifyClientSecret\"")
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
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.espresso.core)
	implementation(libs.androidx.material)
	implementation(libs.androidx.palette.ktx)
	implementation(libs.lottie.compose)
	implementation(libs.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
	implementation(libs.androidx.material.icons.extended)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.accompanist.permissions)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.androidx.core.splashscreen)

	// Media3 - ExoPlayer
	implementation(libs.androidx.media3.exoplayer)
	implementation(libs.androidx.media3.session)
	implementation(libs.androidx.media3.common)
	implementation(libs.androidx.media3.ui)


	// DataStore
	implementation(libs.androidx.datastore)
	implementation(libs.androidx.datastore.preferences)

	// Firebase
//	implementation(platform(libs.firebase.bom))
//	implementation(libs.firebase.analytics)
//	implementation(libs.firebase.firestore.ktx)

	// Coil
	implementation(libs.accompanist.navigation.animation)
	implementation(libs.coil.compose)

	// Hilt
	implementation(libs.hilt.android)
	implementation(libs.androidx.hilt.navigation.compose)
	ksp(libs.hilt.android.compiler)
	ksp(libs.androidx.hilt.compiler)

	// Media 3 x ExoPlayer
	implementation(libs.glide)
	implementation(libs.androidx.legacy.support.v4)
	implementation(libs.androidx.media3.datasource.okhttp)

	// Retrofit
	implementation(libs.retrofit)
	implementation(libs.converter.gson)

	// Splash Screen
	implementation(libs.androidx.core.splashscreen)

	// Cloudinary
	implementation(libs.cloudinary.android)

	// Moshi
	implementation(libs.converter.moshi)
	implementation(libs.moshi.kotlin)

	// Arrow KT
	implementation(libs.arrow.kt.arrow.core)
	implementation(libs.arrow.fx.coroutines)

}
