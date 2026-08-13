pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net")
            }
            filter {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.unpick")
                includeGroup("net.fabricmc.fabric-loom")
                includeGroup("net.fabricmc.fabric-loom-remap")
                includeGroup("fabric-loom")
            }
        }
        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public")
            }
            filter {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "simulated-project"

include("all-neoforge")
include("all-fabric")
include("simulated:common")
include("simulated:neoforge")
include("simulated:fabric")
include("aeronautics:common")
include("aeronautics:neoforge")
include("aeronautics:fabric")
include("offroad:common")
include("offroad:neoforge")
include("offroad:fabric")

include("aeronautics-bundled")
include("aeronautics-bundled-fabric")
