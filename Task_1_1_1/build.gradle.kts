plugins {
    id("java")
    id("jacoco")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}