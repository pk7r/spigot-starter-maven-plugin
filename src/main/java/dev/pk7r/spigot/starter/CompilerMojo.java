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

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class CompilerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("[Spigot Starter] Compiling sources...");

        MojoExecutor.executeMojo(
            plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-compiler-plugin"),
                version("3.13.0")
            ),
            goal("compile"),
            configuration(
                element(name("source"), "${maven.compiler.source}"),
                element(name("target"), "${maven.compiler.target}"),
                element(name("encoding"), "UTF-8")
            ),
            executionEnvironment(project, session, pluginManager)
        );

        getLog().info("[Spigot Starter] Compilation completed!");
    }
}