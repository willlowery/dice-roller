plugins {
    id("java")
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

group = "com.tomakeitgo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    
    implementation("com.googlecode.lanterna:lanterna:3.1.2")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        csv.required.set(true)
    }
}