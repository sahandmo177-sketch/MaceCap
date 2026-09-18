plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
}

group = "de.steak"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("26.2.build.+")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.jar {
    archiveBaseName.set("MaceCap")
}
