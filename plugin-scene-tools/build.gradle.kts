plugins { id("com.android.application") }
android {
    namespace = "com.fstream.plugins.scenetools"
    compileSdk = 36
    defaultConfig { applicationId = "com.fstream.plugins.scenetools"; minSdk = 30; targetSdk = 36; versionCode = 1; versionName = "0.1.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    providers.environmentVariable("FSTREAM_PLUGIN_KEYSTORE").orNull?.let { path ->
        signingConfigs.create("publisher") {
            storeFile = file(path)
            storePassword = providers.environmentVariable("FSTREAM_PLUGIN_STORE_PASSWORD").get()
            keyAlias = providers.environmentVariable("FSTREAM_PLUGIN_KEY_ALIAS").get()
            keyPassword = providers.environmentVariable("FSTREAM_PLUGIN_KEY_PASSWORD").get()
        }
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("publisher")
    }
}
dependencies { compileOnly(project(":plugin-sdk")) }
