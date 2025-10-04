plugins {
    java
    id("jacoco")
}

group = "edu.trincoll"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val isJacocoReportRequested =
    gradle.startParameter.taskNames.any {
        it.contains("jacocoTestReport", ignoreCase = true)
    }

tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
    if (isJacocoReportRequested) {
        // When generating JaCoCo report explicitly, allow tests to fail but still produce coverage
        ignoreFailures = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                // Students should aim for 80% overall coverage
                minimum = "0.80".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            includes = listOf(
                "edu.trincoll.service.*",
                "edu.trincoll.processor.*"
            )
            excludes = listOf(
                "edu.trincoll.model.*",
                "edu.trincoll.functional.*"
            )
            limit {
                counter = "LINE"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// Optional: Add coverage check to build process
// Uncomment to enforce coverage requirements
// tasks.check {
//     dependsOn(tasks.jacocoTestCoverageVerification)
// }