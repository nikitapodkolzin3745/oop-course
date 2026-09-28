plugins {
    id("java")
    id("jacoco")
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}


tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
