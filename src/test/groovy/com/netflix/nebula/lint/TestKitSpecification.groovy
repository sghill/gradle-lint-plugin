package com.netflix.nebula.lint

import com.netflix.nebula.lint.rule.dependency.JavaFixture
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Things that really belong in the unfinished Gradle Testkit
 */
abstract class TestKitSpecification extends Specification {
    @Rule final TemporaryFolder temp = new TemporaryFolder()
    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = temp.root
        buildFile = new File(projectDir, 'build.gradle')
        settingsFile = new File(projectDir, 'settings.gradle')
    }

    def runTasksSuccessfully(String... tasks) {
        return GradleRunner.create()
                .withDebug(true)
                .withProjectDir(projectDir)
                .withArguments(*(tasks + '--stacktrace'))
                .withPluginClasspath()
                .build()
    }

    File addSubproject(String name) {
        def subprojectDir = new File(projectDir, name)
        subprojectDir.mkdirs()
        settingsFile << "include '$name'\n"
        return subprojectDir
    }

    File addSubproject(String name, String buildGradleContents) {
        def subprojectDir = new File(projectDir, name)
        subprojectDir.mkdirs()
        new File(subprojectDir, 'build.gradle').text = buildGradleContents
        settingsFile << "include '$name'\n"
        return subprojectDir
    }

    def dependencies(File _buildFile, String... confs = ['compile', 'testCompile']) {
        _buildFile.text.readLines()
                .collect { it.trim() }
                .findAll { line -> confs.any { c -> line.startsWith(c) } }
                .collect { it.split(/\s+/)[1].replaceAll(/['"]/, '') }
                .sort()
    }

    def createJavaSourceFile(String source) {
        createJavaSourceFile(projectDir, source)
    }

    def createJavaSourceFile(File projectDir, String source) {
        createJavaFile(projectDir, source, 'src/main/java')
    }

    def createJavaTestFile(File projectDir, String source) {
        createJavaFile(projectDir, source, 'src/test/java')
    }

    def createJavaIntegTestFile(File projectDir, String source) {
        createJavaFile(projectDir, source, 'src/integTest/java')
    }

    def createJavaTestFile(String source) {
        createJavaTestFile(projectDir, source)
    }

    def createJavaIntegTestFile(String source) {
        createJavaIntegTestFile(projectDir, source)
    }

    def createJavaFile(File projectDir, String source, String sourceFolderPath) {
        def sourceFolder = new File(projectDir, sourceFolderPath)
        sourceFolder.mkdirs()
        new File(sourceFolder, JavaFixture.fullyQualifiedName(source).replaceAll(/\./, '/') + '.java').text = source
    }
}
