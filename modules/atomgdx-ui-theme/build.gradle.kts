plugins {
    `java-library`
}

dependencies {
    api(project(":modules:atomgdx-core"))
    implementation("com.formdev:flatlaf:3.5.4")

    testImplementation(project(":modules:atomgdx-editor-particle2d"))
    testImplementation(project(":modules:atomgdx-languages"))
    testImplementation(project(":modules:atomgdx-editor-ui-skin"))
    testImplementation(project(":modules:atomgdx-editor-scene2d"))
    testImplementation(project(":modules:atomgdx-viewer-3d"))
    testImplementation(project(":modules:atomgdx-ai-assistant"))
    testImplementation(project(":modules:atomgdx-editor-texturepacker"))
}
