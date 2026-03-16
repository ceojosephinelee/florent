plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// --dart-define 값을 파싱하여 Map으로 반환
fun parseDartDefines(): Map<String, String> {
    val encoded = project.findProperty("dart-defines") as String? ?: return emptyMap()
    return encoded.split(",").mapNotNull { token ->
        try {
            val decoded = String(java.util.Base64.getDecoder().decode(token))
            val parts = decoded.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        } catch (_: Exception) { null }
    }.toMap()
}

android {
    namespace = "com.florent.florent"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.florent.florent"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName

        // --dart-define=KAKAO_NATIVE_KEY=xxx → AndroidManifest에서 ${KAKAO_NATIVE_KEY}로 참조
        val kakaoNativeKey = parseDartDefines()["KAKAO_NATIVE_KEY"] ?: ""
        manifestPlaceholders["KAKAO_NATIVE_KEY"] = kakaoNativeKey
    }

    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

flutter {
    source = "../.."
}
