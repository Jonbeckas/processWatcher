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
    jcenter()
    maven ( url="https://jitpack.io" )
}
dependencies {
    implementation("com.beust:klaxon:5.4")
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    implementation("co.gongzh.procbridge:procbridge:1.1.1")
    implementation(project(":communication"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":cli"))
    implementation(kotlin("stdlib-jdk8"))
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

task<Exec>("testInstallUnix") {
    dependsOn("buildUnix")
    workingDir("./build/libs")
    dependsOn("buildUnix")
    commandLine("mv","watcher","/usr/bin/watcher")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
