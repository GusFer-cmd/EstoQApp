plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt") // KAPT para Room
}

android {
    namespace = "com.example.estoq"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.estoq"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.3.0")) //BOM do Firebase para gerenciar versões
    implementation("com.google.firebase:firebase-auth") //Autenticação do Firebase
    implementation("com.google.android.gms:play-services-auth:21.2.0") //Autenticação com Google Play Services

    val credentialsManagerVersion = "1.3.0" //Gerenciamento de credenciais
    implementation("androidx.credentials:credentials:$credentialsManagerVersion") //Gerenciamento de credenciais
    implementation("androidx.credentials:credentials-play-services-auth:$credentialsManagerVersion") //Gerenciamento de credenciais com Play Services Auth
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("androidx.compose.material:material-icons-extended") //Ícones
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0") //Viewmodel
    implementation("androidx.compose.runtime:runtime-livedata:1.10.1") //Data em tempo real
    implementation("androidx.navigation:navigation-compose:2.7.7") //Navegação
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.1") //Google Fonts

    val room_version = "2.8.4" //Room
    implementation("androidx.room:room-runtime:$room_version") //Room
    kapt("androidx.room:room-compiler:$room_version") //KAPT para Room

    implementation("io.insert-koin:koin-android:4.1.0")//Koin para injeção de dependências
    implementation("io.insert-koin:koin-androidx-compose:4.1.0") //Koin para Compose

    implementation("io.coil-kt:coil-compose:2.7.0") // Coil

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Logs
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}