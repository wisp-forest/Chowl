import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.configure
import net.fabricmc.loom.api.LoomGradleExtensionAPI

fun DependencyHandler.useModule(module: String): Dependency? {
    return add("api", project(":$module", "namedElements"))
}

fun Project.accessWiden() {
    extensions.configure<LoomGradleExtensionAPI> {
        accessWidenerPath.set(file("src/main/resources/chowl-${project.name}.accesswidener"))
    }
}
