package dev.pk7r.spigot.starter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "weave", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class WeaveMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("[Spigot Starter] Starting AspectJ weaving...");

        String outputDirectory = project.getBuild().getOutputDirectory();

        // Gera aspectPath pegando o próprio outputDirectory + todos os artifacts do classpath
        List<String> aspectPathEntries = Stream.concat(
                        Stream.of(new File(outputDirectory)), // adiciona target/classes
                        project.getArtifacts().stream()
                                .filter(artifact -> artifact.getScope() != null &&
                                        (artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_COMPILE)
                                                || artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_PROVIDED)))
                                .map(Artifact::getFile)
                )
                .filter(file -> file != null && file.exists())
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        String aspectPath = String.join(File.pathSeparator, aspectPathEntries);

        getLog().info("[Spigot Starter] Aspect path resolved: " + aspectPath);

        MojoExecutor.executeMojo(
                plugin(
                        groupId("org.codehaus.mojo"),
                        artifactId("aspectj-maven-plugin"),
                        version("1.15.0")
                ),
                goal("compile"),
                configuration(
                        element(name("encoding"), "UTF-8"),
                        element(name("verbose"), "true"),
                        element(name("source"), "1.8"),
                        element(name("target"), "1.8"),
                        element(name("complianceLevel"), "1.8"),
                        element(name("sources"), ""),
                        element(name("weaveDirectories"), outputDirectory),
                        element(name("outputDirectory"), outputDirectory),
                        element(name("forceAjcCompile"), "true"),
                        element(name("showWeaveInfo"), "true"),
                        element(name("Xlint"), "ignore"),
                        element(name("aspectPath"), aspectPath)
                ),
                executionEnvironment(
                        project,
                        session,
                        pluginManager
                )
        );

        getLog().info("[Spigot Starter] AspectJ weaving completed!");
    }
}