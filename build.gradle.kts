import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    /*
     As Spigot-API depends on the BungeeCord ChatComponent-API,
    we need to add the Sonatype OSS repository, as Gradle,
    in comparison to maven, doesn't want to understand the ~/.m2
    directory unless added using mavenLocal(). Maven usually just gets
    it from there, as most people have run the BuildTools at least once.
    This is therefore not needed if you're using the full Spigot/CraftBukkit,
    or if you're using the Bukkit API.
    */
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    mavenLocal()
    mavenCentral()
}



dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
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


