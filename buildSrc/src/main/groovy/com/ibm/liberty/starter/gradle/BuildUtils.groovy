/*
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.ibm.liberty.starter.gradle

import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin

class BuildUtils implements Plugin<Project> {

	void apply(Project project) {
		def outputDir = project.projectDir.getAbsolutePath() + "/src/main/webapp/WEB-INF/classes"
		//this is required for the Swagger generation
		project.getDependencies().add("compile", [group:'com.sebastian-daschner', name:'jaxrs-analyzer-maven-plugin', version:'0.9'])
		project.getPlugins().apply(JavaPlugin.class)
		project.extensions.create("buildutils", BuildUtilsProperties)

		//creates the JSON to be returned which lists all files under the samples directory
		project.task('createSampleJSON') << {
			def base = 'src/main/webapp/sample'
			def osName = System.getProperty("os.name").toLowerCase()
		    if (osName.contains("windows")) {
		        base = base.replace('/', '\\')
		    }
			def files =  project.fileTree(base).filter { it.isFile() }.files
			def header = '"locations": [\n'
			def content = ''
			def json = new File(project.buildDir.getAbsolutePath() + "/war-generated/WEB-INF/classes/locations.json")
			if(!json.getParentFile().exists()) {
				json.getParentFile().mkdirs();
			}
			files.each {File file ->
				def relative = project.relativePath(file).replace('\\', '/')
				content = content + "{\"url\" : \"" + relative.substring(base.length()) + "\"},\n"
			}
			if (content.length() > 2) {
				content = content.substring(0, content.length() - 2) + '\n]\n'
			} else {
				//empty array
				content = content + '\n]\n'
			}
			json.write(header + content)
		}

		/*
		Create the swagger UI JSON which means that
			- build needs Java 8 as the generator lib is built against this
			- we have to initially generate to stdout so that we can correctly specify the base path
			- write a file into the webapp section of the source tree so that is is pacakged
		*/
		project.task('createSwaggerJSON') << {
 			if(new File(project.buildDir.getAbsolutePath() + "/classes/main").exists()) {
				new ByteArrayOutputStream().withStream { osOut ->
					def result = project.javaexec {
						classpath = project.sourceSets.main.compileClasspath
						main = 'com.sebastian_daschner.jaxrs_analyzer.Main'
						standardOutput = osOut
						args(['-b', 'swagger', project.buildDir.getAbsolutePath() + "/classes/main"])
					}
					def content = osOut.toString().replace('"basePath":"/api"', '"basePath":"' + project.buildutils.contextRoot + '/api"')
					def json = new File(project.buildDir.getAbsolutePath() + "/war-generated/META-INF/swagger.json")
					if(!json.getParentFile().exists()) {
						json.getParentFile().mkdirs();
					}
					json.write(content)
				}
			}
		}
	}
}

class BuildUtilsProperties {
	//the context root under which it is going to be deployed
    def String contextRoot = ''
}
