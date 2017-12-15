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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author yazhong.qi
 * @since 1.6.0
 */
public class ProjectGenerator implements InitializingBean, BeanPostProcessor {
    public void afterPropertiesSet() throws Exception {

    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
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
     *
     *
     * resource
     * 1. parent pom
     * 2. modules all
     * 3.
     * 4.
     */

    public static void generateProjectFile() {
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

    }

    private static void apiLayerFile(){
        // pom.xml
    }

    private static void dalLayerFile() {

    }

    private static void bizLayerFile() {

    }

    private static void serviceLayerFile() {

    }

    private static void webLayerFile(){

    }
}
