import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    application
}
group = "net.tetraowl"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.beust:klaxon:5.4")
    implementation("commons-io:commons-io:2.4")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task<Exec>("buildUnix") {
    workingDir("./build/libs")
    commandLine("rm *.jar")
    dependsOn("shadowJar")
    commandLine("bash","-c","echo \"#!/usr/bin/java -jar\" > watcher")
    commandLine("bash","-c","cat *.jar >> watcher")
}

task<Exec>("testUnix") {
    workingDir("./build/libs")
    dependsOn("buildUnix")
    commandLine("./watcher")
}

