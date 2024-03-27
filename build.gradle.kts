tasks.wrapper {
    gradleVersion = "8.7"
    // You can either download the binary-only version of Gradle (BIN) or
    // the full version (with sources and documentation) of Gradle (ALL)
    distributionType = Wrapper.DistributionType.BIN
}

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"



repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mysql:mysql-connector-j:8.3.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}