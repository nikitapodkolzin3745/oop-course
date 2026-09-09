plugins {
    id("java")
    id("jacoco")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
}

repositories {
    mavenCentral()
}