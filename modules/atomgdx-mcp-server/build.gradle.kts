plugins {
    `java-library`
}

dependencies {
    api(project(":modules:atomgdx-core"))
    implementation("com.google.code.gson:gson:2.10.1")
}
