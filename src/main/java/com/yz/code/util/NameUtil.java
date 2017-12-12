package com.yz.code.util;

import com.yz.code.config.ConfigManager;
import com.yz.code.constant.Constants;
import com.yz.code.schema.TableSchema;
import org.springframework.util.StringUtils;

public class NameUtil {

    public static String getMapperClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Mapper";
    }

    public static String getSlaveMapperName(TableSchema tableSchema) {
        return "Slave" + tableSchema.getModelName() + "Mapper";
    }

    public static String getMasterMapperName(TableSchema tableSchema) {
        return "Master" + tableSchema.getModelName() + "Mapper";
    }

    public static String getMapperVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getMapperClassName(tableSchema));
    }

    public static String getSlaveMapperVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getSlaveMapperName(tableSchema));
    }

    public static String getMasterMapperVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getMasterMapperName(tableSchema));
    }

    public static String getFullMapperClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".dao."
                + getMapperClassName(tableSchema);
    }

    public static String getFullExceptionClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".biz.exception."
                + getExceptionClassName(tableSchema);
    }

    public static String getModelVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getModelClassName(tableSchema));
    }

    public static String getModelClassName(TableSchema tableSchema) {
        return tableSchema.getModelName();
    }

    public static String getFullModelClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".model."
                + getModelClassName(tableSchema);
    }

    public static String getFullModelExampleClassName(TableSchema tableSchema) {
        return getFullModelClassName(tableSchema) + "Example";
    }

    public static String getFullControllerVoClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".vo."
                + getControllerVoClassName(tableSchema);
    }

    public static String getFullControllerQueryVoClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".vo."
                + getControllerQueryVoClassName(tableSchema);
    }

    public static String getModelQueryClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Query";
    }

    public static String getModelQueryVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getModelQueryClassName(tableSchema));
    }

    public static String getFullModelQueryClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".model."
                + getModelQueryClassName(tableSchema);
    }

    public static String getConvertorClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Convertor";
    }

    public static String getConvertorVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getConvertorClassName(tableSchema));
    }

    public static String getFullConvertorClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".biz.convertor."
                + getConvertorClassName(tableSchema);
    }

    public static String getFullControllerConvertorClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".convertor."
                + getConvertorClassName(tableSchema);
    }

    public static String getMessageQueryClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "QueryDTO";
    }

    public static String getControllerQueryVoClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "QueryVO";
    }

    public static String getControllerQueryVoVar(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getControllerQueryVoClassName(tableSchema));
    }

    public static String getMessageQueryVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getMessageQueryClassName(tableSchema));
    }

    public static String getFullMessageQueryClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".req."
                + getMessageQueryClassName(tableSchema);
    }

    public static String getBizClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Biz";
    }

    public static String getBizVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getBizClassName(tableSchema));
    }

    public static String getFullBizClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".biz."
                + getBizClassName(tableSchema);
    }

    public static String getMessageReqClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "DTO";
    }

    public static String getMessageReqVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getMessageReqClassName(tableSchema));
    }

    public static String getFullMessageReqClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".req."
                + getMessageReqClassName(tableSchema);
    }

    public static String getMessageClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "ODTO";
    }

    public static String getMessageVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getMessageClassName(tableSchema));
    }

    public static String getFullMessageClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".resp."
                + getMessageClassName(tableSchema);
    }

    public static String getServiceClassName(TableSchema tableSchema) {
        return "I" + tableSchema.getModelName() + "Service";
    }

    public static String getServiceVarName(TableSchema tableSchema) {
        String serviceImplClassName = getServiceClassName(tableSchema);
        serviceImplClassName = serviceImplClassName.substring(1);
        return StringUtils.uncapitalize(serviceImplClassName);
    }

    public static String getFullServiceClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".service."
                + getServiceClassName(tableSchema);
    }

    public static String getServiceImplClassName(TableSchema tableSchema) {
        String serviceImplClassName = getServiceClassName(tableSchema);
        serviceImplClassName = serviceImplClassName.substring(1);
        return serviceImplClassName + "Impl";
    }

    public static String getFullServiceImplClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".service.impl."
                + getServiceImplClassName(tableSchema);
    }

    public static String getValidateClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Validate";
    }

    public static String getExceptionClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Exception";
    }

    public static String getControllerClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Controller";
    }

    public static String getControllerVoClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "VO";
    }

    public static String getControllerVoVar(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getControllerVoClassName(tableSchema));
    }

    public static String getControllerVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getControllerClassName(tableSchema));
    }

    public static String getFullControllerClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".service.controller."
                + getControllerClassName(tableSchema);
    }

    // --
    public static String getTestClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Test";
    }

    public static String getTestVarName(TableSchema tableSchema) {
        return StringUtils.uncapitalize(getTestClassName(tableSchema));
    }

    public static String getFullTestClassName(TableSchema tableSchema) {
        return ConfigManager.getProperty("basePackage")
                + ".service.test."
                + getTestClassName(tableSchema);
    }

    public static String getTableComment(TableSchema tableSchema) {
        return tableSchema.getComment();
    }

    public static String getEnumClassName(String prefix) {
        return prefix + "Enum";
    }

    public static String getProjectAuthor(TableSchema tableSchema) {
        return ConfigManager.getProperty("project.Author");
    }

    public static String getProjectEmail(TableSchema tableSchema) {
        return ConfigManager.getProperty("project.email");
    }

    public static String getFullProjectResultClassName() {
        return ConfigManager.getProperty("basePackage")
                + ".result." + getProjectResultClassName();
    }

    public static String getFullProjectResultCodeClassName() {
        return ConfigManager.getProperty("basePackage")
                + ".result." + getProjectResultCodeClassName();
    }

    public static String getProjectResultClassName() {
        return StringUtils.capitalize(ConfigManager.getProperty("project.abbreviation")) + "Result";
    }

    public static String getProjectResultCodeClassName() {
        return StringUtils.capitalize(ConfigManager.getProperty("project.abbreviation")) + "ResultCode";
    }

    public static String getProjectVersionName(String type) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ConfigManager.getProperty("project.abbreviation"));
        stringBuilder.append(".");
        stringBuilder.append(switchTypeString(type));
        stringBuilder.append(".");
        stringBuilder.append("version");
        return stringBuilder.toString();
    }

    public static String getModulesName(String type){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ConfigManager.getProperty("project.abbreviation"));
        stringBuilder.append("-");
        stringBuilder.append(switchTypeString(type));
        return stringBuilder.toString();
    }

    private static String switchTypeString(String type){
        switch (type) {
            case Constants.MODULE_API:
                return Constants.MODULE_API;
            case Constants.MODULE_CORE:
                return Constants.MODULE_CORE;
            case Constants.MODULE_DAL:
                return Constants.MODULE_DAL;
            case Constants.MODULE_BIZ:
                return Constants.MODULE_BIZ;
            case Constants.MODULE_SERVICE:
                return Constants.MODULE_SERVICE;
            case Constants.MODULE_WEB:
                return Constants.MODULE_WEB;
            default:
                return "";
        }
    }
}
