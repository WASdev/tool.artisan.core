/*******************************************************************************
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
 *******************************************************************************/
package com.ibm.liberty.starter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.ibm.liberty.starter.build.FeaturesToInstallProvider;
import com.ibm.liberty.starter.build.gradle.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.api.v1.temp.ServiceFinder;
import com.ibm.liberty.starter.build.maven.AddDependenciesCommand;
import com.ibm.liberty.starter.build.maven.AddFeaturesCommand;
import com.ibm.liberty.starter.build.maven.AppNameCommand;
import com.ibm.liberty.starter.build.maven.PomModifierCommand;
import com.ibm.liberty.starter.build.maven.SetDefaultProfileCommand;
import com.ibm.liberty.starter.build.maven.SetRepositoryCommand;
import com.ibm.liberty.starter.build.maven.PomModifier;

public class ProjectZipConstructor {
    
    private final ServiceConnector serviceConnector;
    
    private static final Logger log = Logger.getLogger(ServiceFinder.class.getName());
    private Services services;
    private ConcurrentHashMap<String, byte[]> fileMap = new ConcurrentHashMap<>();
    private static final String SKELETON_FILENAME = "services/skeletonLibertyBuildImage.zip";
    private static final String BASE_INDEX_HTML = "payloadIndex.html";
    private static final String INDEX_HTML_PATH = "src/main/webapp/index.html";
    private static final String POM_FILE = "pom.xml";
    private static final String GRADLE_BUILD_FILE = "build.gradle";
    private String appName;
    public enum DeployType {
        LOCAL, BLUEMIX
    }
    public enum BuildType {
        MAVEN, GRADLE
    }
    private DeployType deployType;
    private String workspace = null;
    private BuildType buildType;
    
    public ProjectZipConstructor(ServiceConnector serviceConnector, Services services, String appName, DeployType deployType, BuildType buildType, String workspace) {
        this.serviceConnector = serviceConnector;
        this.services = services;
        this.appName = appName;
        this.deployType = deployType;
        this.workspace = workspace;
        this.buildType = buildType;
    }
    
    public Map<String, byte[]> getFileMap() {
        return fileMap;
    }
    
    public void buildZip(OutputStream os) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        initializeMap();
        addHtmlToMap();
        addTechSamplesToMap();
        addBuildFilesToMap();
        addDynamicPackages();
        ZipOutputStream zos = new ZipOutputStream(os);
        createZipFromMap(zos);
        zos.close();
        cleanup();
    }

    private void cleanup() throws IOException {
    	cleanUpDynamicPackages();    	
    }
    
    private void cleanUpDynamicPackages() throws IOException{
    	// Delete dynamically generated packages as they were already packaged.
    	// ** Note **: Don't delete these packages prior to this stage as other operations may depend on the existence of these packages to perform certain tasks.
    	for (Service service : services.getServices()) {
            String serviceId = service.getId();
            
            String packageLocation = workspace + "/" + serviceId + "/" + StarterUtil.PACKAGE_DIR;
            File packageDir = new File(packageLocation);
            
            if(packageDir.exists() && packageDir.isDirectory()){
            	FileUtils.deleteDirectory(packageDir);
            	log.log(Level.FINE, "Deleted package directory for " + serviceId + " technology. : " + packageLocation);
            }
        }
    }
    
    private void addDynamicPackages() throws IOException {
    	log.log(Level.FINE, "Entering method ProjectZipConstructor.addDynamicPackages()");
    	if(workspace == null || workspace.isEmpty() || !(new File(workspace).exists())){
    		log.log(Level.FINE, "No dynamic packages to add since workspace doesn't exist : " + workspace);
    		return;
    	}
    	
        for (Service service : services.getServices()) {
            String serviceId = service.getId();
            String packageLocation = workspace + "/" + serviceId + "/" + StarterUtil.PACKAGE_DIR;
            File packageDir = new File(packageLocation);
            
            if(packageDir.exists() && packageDir.isDirectory()){
            	log.log(Level.FINE, "Package directory for " + serviceId + " technology exists : " + packageLocation);
            	List<File> filesListInDir = new ArrayList<File>();
            	StarterUtil.populateFilesList(packageDir, filesListInDir);
            	
            	for(File aFile : filesListInDir){
            		String path = aFile.getAbsolutePath().replace('\\', '/').replace(packageLocation, "");

            		if(path.startsWith("/")){
            			path = path.substring(1);
            		}
            		putFileInMap(path, FileUtils.readFileToByteArray(aFile));
            		log.log(Level.FINE, "Packaged file " + aFile.getAbsolutePath() + " to " + path);
            	}
            }
        }
        log.log(Level.FINE, "Exiting method ProjectZipConstructor.addDynamicPackages()");
    }
    
    public void initializeMap() throws IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.initializeMap()");
        InputStream skeletonIS = this.getClass().getClassLoader().getResourceAsStream(SKELETON_FILENAME);
        ZipInputStream zis = new ZipInputStream(skeletonIS);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            String path = ze.getName();
            int length = 0;
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((length = zis.read(bytes)) != -1 ) {
                baos.write(bytes, 0, length);
            }
            putFileInMap(path, baos.toByteArray());
        }
        zis.close();
    }
    
    public void addHtmlToMap() throws IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.addHtmlToMap()");
        byte[] html = getHtmlFile();
        putFileInMap(INDEX_HTML_PATH, html);
    }
    
    public byte[] getHtmlFile() throws IOException {
        InputStream htmlIS = this.getClass().getClassLoader().getResourceAsStream(BASE_INDEX_HTML);
        HashMap<String, byte[]> techDescriptions = new HashMap<String, byte[]>();
        for (Service service : services.getServices()) {
            Provider provider = serviceConnector.getProvider(service);
            String description = provider.getDescription();
            byte[] bytes = description.getBytes();
            techDescriptions.put(service.getId(), bytes);
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int MAX_SIZE = 200000;
        char[] buffer = new char[MAX_SIZE];
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(htmlIS));) {
            reader.read(buffer, 0, MAX_SIZE);
        } catch (Exception e ){
            return null;
        }
        String contents = new String(buffer);
        int index = contents.indexOf("<div id=\"technologies\">");
        if (index != -1) {
            int length = contents.length();
            String first = contents.substring(0, index);
            String last = contents.substring(index, length);
            baos.write(first.getBytes());
            Set<String> keys = techDescriptions.keySet();
            for (String key : keys) {
                baos.write(techDescriptions.get(key));
            }
            baos.write(last.getBytes());
        }
        return baos.toByteArray();
    }
    
    public void addTechSamplesToMap() throws IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.addTechSamplesToMap()");
        for (Service service : services.getServices()) {
            Sample sample = serviceConnector.getSample(service);
            Location[] locations = sample.getLocations();
            String basePath = sample.getBase();
            for (Location location : locations) {
                String fileUrl = location.getUrl();
                InputStream is = serviceConnector.getResourceAsInputStream(basePath + fileUrl);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(is, baos);
                is.close();
                byte[] bytes = baos.toByteArray();
                baos.close();
                if (fileUrl.startsWith("/")) {
                    fileUrl = fileUrl.substring(1);
                }
                putFileInMap(fileUrl, bytes);
            }
        }
    }

    private void addBuildFilesToMap() throws SAXException, TransformerException, ParserConfigurationException, IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.addBuildFilesToMap()");
        if (BuildType.GRADLE.equals(buildType)) {
            addGradleFilesToMap();
        } else {
            addPomFileToMap();
        }
    }

    private void addGradleFilesToMap() throws IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.addGradleFilesToMap()");
        Map<String, String> buildTags = new HashMap<>();
        DependencyHandler depHand = new DependencyHandler(services, serviceConnector, appName);
        buildTags.putAll(new CreateAppNameTags(depHand).getTags());
        buildTags.putAll(new CreateDependencyTags(depHand).getTags());
        buildTags.putAll(new CreateFeaturesTags(new FeaturesToInstallProvider(services, serviceConnector)).getTags());
        buildTags.putAll(new CreateRepositoryTags(depHand).getTags());
        TemplatedFileToBytesConverter fileConverter = new TemplatedFileToBytesConverter(this.getClass().getClassLoader().getResourceAsStream(GRADLE_BUILD_FILE), buildTags);
        putFileInMap("build.gradle", fileConverter.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream settingsStream = this.getClass().getClassLoader().getResourceAsStream("settings.gradle");
        IOUtils.copy(settingsStream, baos);
        settingsStream.close();
        putFileInMap("settings.gradle", baos.toByteArray());
    }
    
    public void addPomFileToMap() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.addPomFileToMap()");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(POM_FILE);
        Set<PomModifierCommand> commands = new HashSet<>();
        DependencyHandler depHand = new DependencyHandler(services, serviceConnector, appName);
        commands.add(new AddDependenciesCommand(depHand));
        commands.add(new AppNameCommand(depHand));
        commands.add(new SetDefaultProfileCommand(deployType));
        commands.add(new SetRepositoryCommand(depHand));
        commands.add(new AddFeaturesCommand(new FeaturesToInstallProvider(services, serviceConnector)));
        PomModifier pomModifier = new PomModifier(inputStream, commands);
        byte[] bytes = pomModifier.getPomBytes();
        putFileInMap("pom.xml", bytes);
    }
    
    public void createZipFromMap(ZipOutputStream zos) throws IOException {
        log.log(Level.INFO, "Entering method ProjectZipConstructor.createZipFromMap()");
        Enumeration<String> en = fileMap.keys();
        while (en.hasMoreElements()) {
            String path = en.nextElement();
            byte[] byteArray = fileMap.get(path);
            ZipEntry entry = new ZipEntry(path);
            entry.setSize(byteArray.length);
            entry.setCompressedSize(-1);
            try {
                zos.putNextEntry(entry);
                zos.write(byteArray);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }
    
    public void putFileInMap(String path, byte[] file) {
        log.log(Level.INFO, "Inserting file " + path + " into map.");
        fileMap.put(path, file);
    }

}
