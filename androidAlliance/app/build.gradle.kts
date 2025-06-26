plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.zhu.androidalliance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zhu.androidalliance"
        minSdk = 30
        targetSdk = 35
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
}

dependencies {
    // 基础依赖
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\gson-2.8.0.jar"))

    // UI组件
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // 网络和数据处理
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("org.json:json:20210307")

    // 图像加载
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")

    // 数据库
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // AndroidX组件
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Lombok
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // ===== 测试依赖 =====

    // 单元测试 (test)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.0.0") // 统一Mockito版本

    // Fragment测试支持
    debugImplementation("androidx.fragment:fragment-testing:1.6.1")

    // Android仪器测试 (androidTest)
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("org.mockito:mockito-android:4.0.0") // 仅用于androidTest

    // Fragment测试(仪器测试)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.1")
}