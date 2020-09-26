import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    application
}


group = "net.tetraowl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven ( url="https://jitpack.io" )
}

dependencies {
    implementation("co.gongzh.procbridge:procbridge:1.1.1")
    implementation(project(":communication"))
    implementation(kotlin("stdlib"))
    implementation("com.beust:klaxon:5.4")
    implementation("junit:junit:4.12")
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "net.tetraowl.processwatcher.cli.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

task<Exec>("buildUnix") {
    workingDir("./build/libs")
    commandLine("rm *.jar")
    dependsOn("shadowJar")
    commandLine("bash","-c","echo \"#!/usr/bin/java -jar\" > watchercli")
    commandLine("bash","-c","cat *.jar >> watchercli")
}

task<Exec>("testInstallUnix") {
    dependsOn("buildUnix")
    workingDir("./build/libs")
    dependsOn("buildUnix")
    commandLine("mv","watchercli","/usr/bin/watchercli")
}
