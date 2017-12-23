/*
 * Copyright 2012-2017 yazhong.qi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yz.code.tools;

import com.yz.code.constant.Constants;
import com.yz.code.util.FileUtil;
import groovy.ui.SystemOutputInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.IOException;

/**
 * @author yazhong.qi
 * @since 1.6.0
 */
public class ProjectGenerator implements InitializingBean, BeanPostProcessor {

    // D:\output\lds
    public static String projectBasePath = null;



    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerator.class);

    public void afterPropertiesSet() throws Exception {

    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    private static void ProjectClassVariableSettings(){
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append(DataGenerator.outputRootDir).append(Constants.FILE_PATH_SEPARATOR);
        stringBuilder.append(DataGenerator.projectAbbreviation).append(Constants.FILE_PATH_SEPARATOR);
        projectBasePath = stringBuilder.toString();
    }

    /**
     * all pom
     * modules
     * 1. web
     * 2. api
     * 3. service
     * 4. biz
     * 5. dal
     * 6. core
     * <p>
     * <p>
     * resource
     * 1. parent pom
     * 2. modules all
     * 3.
     * 4.
     */

    public static void generateProjectFile() {
        ProjectClassVariableSettings();
        //
        coreLayerFile();
        apiLayerFile();
        dalLayerFile();
        bizLayerFile();
        serviceLayerFile();
        webLayerFile();
    }

    private static void coreLayerFile() {
        // pom.xml
        createBaseMavenDir(Constants.MODULE_CORE);
    }

    private static void apiLayerFile() {
        // pom.xml
    }

    private static void dalLayerFile() {

    }

    private static void bizLayerFile() {

    }

    private static void serviceLayerFile() {

    }

    private static void webLayerFile() {

    }

    private static void createBaseMavenDir(String moduleName) {
//─src
//    ├─main
//    │  ├─java
//    │  │  └─com
//    │  │      └─company abbreviation
//    │  │          └─project name abbreviation
//    │  └─resources
//    │      ├─env
//    │      └─spring
//    └─test
//        ├─java
//        │  └─com
//        └─resources
//            ├─spring
//            └─testData
//                └─json

        try {
            StringBuilder sbSrc = new StringBuilder(200);
            sbSrc.append(projectBasePath);
            sbSrc.append(moduleName).append(Constants.FILE_PATH_SEPARATOR);
            sbSrc.append(Constants.RES_DIR_SRC).append(Constants.FILE_PATH_SEPARATOR);


            sbSrc.append(Constants.RES_DIR_MAIN).append(Constants.FILE_PATH_SEPARATOR);


            sbSrc.append(Constants.RES_DIR_JAVA).append(Constants.FILE_PATH_SEPARATOR);
            sbSrc.append(DataGenerator.basePackagePath).append(Constants.FILE_PATH_SEPARATOR);

            System.out.println("dir created[" + sbSrc.toString() + "]");
            FileUtil.createDirectories(sbSrc.toString());
            LOGGER.info("dir created[" + sbSrc.toString() + "]");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("maven create folder error");
            System.out.println("maven create folder error");
        } finally {
        }
    }
}
