plugins { id("com.android.library"); id("maven-publish") }
group = "com.fstream"
version = "0.4.0"
android {
    namespace = "com.fstream.plugin.sdk"
    compileSdk = 36
    defaultConfig { minSdk = 26; consumerProguardFiles("consumer-rules.pro") }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing { singleVariant("release") { withSourcesJar() } }
}
afterEvaluate {
    publishing.publications.create<MavenPublication>("release") {
        from(components["release"])
        artifactId = "plugin-sdk"
    }
}
