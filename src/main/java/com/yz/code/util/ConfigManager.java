package com.yz.code.util;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

public final class ConfigManager extends PropertyPlaceholderConfigurer {
	static final ConcurrentHashMap<String, String> ALL_PROPERTIES = new ConcurrentHashMap<String, String>();
	
	public static String getProperty(String propertyName) {
		return ALL_PROPERTIES.get(propertyName);
	}
	
	@Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
            Properties props) throws BeansException {
        super.processProperties(beanFactory, props);
        CollectionUtils.mergePropertiesIntoMap(props, ConfigManager.ALL_PROPERTIES);
//        System.out.println("Properties=" + JSON.toJSONString(ALL_PROPERTIES));
    }
}
