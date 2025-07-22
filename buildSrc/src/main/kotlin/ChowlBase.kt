import gradle.kotlin.dsl.accessors._f2e482c4e157e5f06d5caef5b57c4397.loom
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.useModule(module: String): Dependency? {
    return add("api", project(":$module", "namedElements"))
}

fun Project.accessWiden() {
    project.loom {
        accessWidenerPath.set(file("src/main/resources/chowl-${project.name}.accesswidener"))
    }
}