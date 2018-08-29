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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.IOException;

/**
 * @author yazhong.qi
 * @since 1.8.0
 */
public class ProjectGenerator implements InitializingBean, BeanPostProcessor {

    /**
     * D:\output\lds
     */
    private static String projectBasePath = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerator.class);

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    private static void ProjectClassVariableSettings() {
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append(CodeGenerator.outputRootDir).append(Constants.FILE_PATH_SEPARATOR);
        stringBuilder.append(CodeGenerator.projectAbbreviation).append(Constants.FILE_PATH_SEPARATOR);
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
        createBaseMavenDir(Constants.MODULE_API);

    }

    private static void dalLayerFile() {
        createBaseMavenDir(Constants.MODULE_DAL);
    }

    private static void bizLayerFile() {
        createBaseMavenDir(Constants.MODULE_BIZ);
    }

    private static void serviceLayerFile() {
        createBaseMavenDir(Constants.MODULE_SERVICE);
    }

    private static void webLayerFile() {
        createBaseMavenDir(Constants.MODULE_WEB);
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

        StringBuilder sbSrc = new StringBuilder(200);
        sbSrc.append(projectBasePath);
        sbSrc.append(CodeGenerator.projectAbbreviation + Constants.ARTIFACT_SEPARATOR + moduleName).append(Constants.FILE_PATH_SEPARATOR);
        sbSrc.append(Constants.RES_DIR_SRC).append(Constants.FILE_PATH_SEPARATOR);

        createJavaResourcesDirectories(sbSrc.toString() + Constants.RES_DIR_MAIN, moduleName, true);
        if (!Constants.MODULE_API.equals(moduleName)) {
            createJavaResourcesDirectories(sbSrc.toString() + Constants.RES_DIR_TEST, moduleName, false);
        }
    }

    private static void createJavaResourcesDirectories(String basePath, String moduleName, boolean mainBool) {
        if (!basePath.endsWith(Constants.FILE_PATH_SEPARATOR)) {
            basePath += "/";
        }
        try {
            FileUtil.createDirectories(basePath + Constants.RES_DIR_JAVA);
            LOGGER.info("dir created[" + basePath + Constants.RES_DIR_JAVA + "]");
            FileUtil.createDirectories(basePath + Constants.RES_DIR_JAVA + Constants.FILE_PATH_SEPARATOR + CodeGenerator.basePackagePath);
            LOGGER.info("dir created[" + basePath + Constants.RES_DIR_JAVA + Constants.FILE_PATH_SEPARATOR + CodeGenerator.basePackagePath + "]");
            if (Constants.MODULE_API.equals(moduleName) && mainBool) {
                createApiBaseDirectories(basePath + Constants.RES_DIR_JAVA + Constants.FILE_PATH_SEPARATOR + CodeGenerator.basePackagePath);
            }
            if (!Constants.MODULE_API.equals(moduleName)) {
                FileUtil.createDirectories(basePath + Constants.RES_DIR_RESOURCES);
                LOGGER.info("dir created[" + basePath + Constants.RES_DIR_RESOURCES + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("createJavaResourcesPath error");
            System.out.println("createJavaResourcesPath create folder error");
        } finally {
        }
    }

    private static void createApiBaseDirectories(String apiBasePath) {
        try {
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "constant");
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "enums");
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "req");
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "resp");
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "result");
            FileUtil.createDirectories(apiBasePath + Constants.FILE_PATH_SEPARATOR + "service");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("createApiBaseDirectories error");
            System.out.println("createApiBaseDirectories create folder error");
        } finally {
        }
    }
}
