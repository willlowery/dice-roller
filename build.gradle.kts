plugins {
    id("java")
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
}