package dev.pk7r.spigot.starter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "shade", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class ShadeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("[Spigot Starter] Starting shading...");

        MojoExecutor.executeMojo(
            plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-shade-plugin"),
                version("3.5.3")
            ),
            goal("shade"),
            configuration(
                element(name("createDependencyReducedPom"), "false"),
                element(name("minimizeJar"), "false")
            ),
            executionEnvironment(project, session, pluginManager)
        );

        getLog().info("[Spigot Starter] Shading completed!");
    }
}