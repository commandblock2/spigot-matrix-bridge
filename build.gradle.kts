import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenLocal()
    mavenCentral()
}

val trixnityVersion = "3.0.0" // get version from https://gitlab.com/benkuly/trixnity/-/releases
fun trixnity(module: String, version: String = trixnityVersion) =
    "net.folivo:trixnity-$module:$version"

dependencies {
    implementation(trixnity("applicationservice"))

    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        }
    }
}


