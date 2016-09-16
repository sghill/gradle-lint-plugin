package com.netflix.nebula.lint.issues

import com.netflix.nebula.lint.TestKitSpecification
import spock.lang.Issue

class Issue53Spec extends TestKitSpecification {
    
    @Issue('53')
    def 'only one violation reported when a dependency is unused by multiple configurations'() {
        when:
        buildFile << '''
            plugins {
                id 'nebula.lint'
                id 'java'
                id 'nebula.integtest' version '3.3.0'
            }
            
            repositories { mavenCentral() }
            
            dependencies {
                testCompile 'junit:junit:4.11'
            }
            
            gradleLint.rules = ['unused-dependency']
        '''

        createJavaTestFile('@org.junit.Ignore public class A { org.hamcrest.CoreMatchers cm; }')
        createJavaIntegTestFile('@org.junit.Ignore public class B { org.hamcrest.CoreMatchers cm; }')

        then:
        def results = runTasksSuccessfully('compileIntegTestJava', 'lintGradle')
        println(results.output)
        results.output.readLines().count { it.contains('unused-dependency') } == 1
    }
}
