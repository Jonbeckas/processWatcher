import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    `maven-publish`


}
group = "net.tetraowl"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven ( url="https://jitpack.io" )
}
dependencies {
    implementation("co.gongzh.procbridge:procbridge:1.1.1")
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
}
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Jonbeckas/processWatcher")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }

    publications {
        register("gpr",MavenPublication::class) {
            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
