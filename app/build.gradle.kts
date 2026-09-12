plugins {
alias(libs.plugins.android.application)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.compose)
alias(libs.plugins.ksp)
}

android {
namespace = "com.example"
compileSdk = 34

defaultConfig {
applicationId = "com.example"
minSdk = 24
targetSdk = 34
versionCode = 1
versionName = "1.0"

testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

buildConfigField("String", "TAPSELL_APP_KEY", "\"YOUR_TAPSELL_APP_KEY\"")
buildConfigField("String", "TAPSELL_REWARDED_ZONE_ID", "\"YOUR_TAPSELL_REWARDED_ZONE_ID\"")
buildConfigField("String", "TAPSELL_BANNER_ZONE_ID", "\"YOUR_TAPSELL_BANNER_ZONE_ID\"")
buildConfigField("String", "BAZAAR_RSA_PUBLIC_KEY", "\"YOUR_BAZAAR_RSA_PUBLIC_KEY\"")
buildConfigField("String", "SKU_VIP_MONTHLY", "\"vip_monthly\"")
buildConfigField("String", "SKU_VIP_QUARTERLY", "\"vip_quarterly\"")
buildConfigField("String", "SKU_VIP_LIFETIME", "\"vip_lifetime\"")
buildConfigField("String", "SKU_COINS_100", "\"coins_pack_100\"")
buildConfigField("String", "SKU_COINS_500", "\"coins_pack_500\"")
buildConfigField("String", "SKU_COINS_1500", "\"coins_pack_1500\"")

}

signingConfigs {
create("release") {
val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
storeFile = file(keystorePath)
storePassword = System.getenv("STORE_PASSWORD")
keyAlias = "upload"
keyPassword = System.getenv("KEY_PASSWORD")
}

create("debugConfig") {
  storeFile = file("${rootDir}/debug.keystore")
  storePassword = "android"
  keyAlias = "androiddebugkey"
  keyPassword = "android"
}

}

buildTypes {
release {
isCrunchPngs = false
isMinifyEnabled = false
proguardFiles(
getDefaultProguardFile("proguard-android-optimize.txt"),
"proguard-rules.pro"
)
signingConfig = signingConfigs.getByName("release")
}

debug {
  signingConfig = signingConfigs.getByName("debugConfig")
}

}

compileOptions {
sourceCompatibility = JavaVersion.VERSION_21
targetCompatibility = JavaVersion.VERSION_21
}

kotlinOptions {
jvmTarget = "21"
}

buildFeatures {
compose = true
buildConfig = true
}

testOptions {
unitTests {
isIncludeAndroidResources = true
}
}

dependenciesInfo {
includeInApk = false
includeInBundle = true
}
}

dependencies {
implementation(platform(libs.androidx.compose.bom))

implementation(libs.androidx.activity.compose)
implementation(libs.androidx.compose.material.icons.extended)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.graphics)
implementation(libs.androidx.compose.ui.tooling.preview)

implementation(libs.androidx.core.ktx)

implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.compose)

implementation(libs.androidx.room.ktx)
implementation(libs.androidx.room.runtime)

implementation(libs.coil.compose)
implementation(libs.okhttp)

// Tapsell Plus SDK
implementation(libs.tapsell.plus.sdk)

testImplementation(libs.junit)
testImplementation(libs.androidx.junit)

androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.androidx.espresso.core)

debugImplementation(libs.androidx.compose.ui.test.manifest)
debugImplementation(libs.androidx.compose.ui.tooling)

ksp(libs.androidx.room.compiler)
}

kotlin {
jvmToolchain(21)
}
