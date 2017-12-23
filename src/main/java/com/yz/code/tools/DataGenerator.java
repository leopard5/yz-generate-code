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

import com.yz.code.config.ConfigManager;
import com.yz.code.constant.Constants;
import com.yz.code.enums.UItype;
import com.yz.code.schema.DatabaseSchema;
import com.yz.code.schema.Dictionary;
import com.yz.code.schema.TableSchema;
import com.yz.code.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolInfo;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.Toolbox;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yazhong.qi
 * @since 1.6.0
 */
public class DataGenerator {
    private final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

    public static ClassPathXmlApplicationContext applicationContext = null;
    public static String dictionaryTableName = null;
    public static List<String> tableNames = new ArrayList<String>();
    public static String outputRootDir = null;
    public static String templateDir = null;
    public static Byte uiType = null;
    public static String organizationAbbreviation = null;
    public static String companyAbbreviation = null;
    public static String projectAbbreviation = null;
    public static String basePackage = null;
    public static String basePackagePath = null;
    public static String generatorPackageFileDir = null;

    public static void main(String[] args) {
        try {
            applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
            DataSchema dataSchema = applicationContext.getBean("dataSchema", DataSchema.class);

            globalVariableSettings();

            deleteSubFiles(new File(outputRootDir));
            deleteSubFiles(new File(generatorPackageFileDir));

            // project struct layer
            // core dal biz service web junit api
            ProjectGenerator.generateProjectFile();

            // mybatis-generator-maven-plugin 1.3.5
            mavenPluginsMyBatisGenerator(ConfigManager.getProperty("basePackage"));

            DatabaseSchema databaseSchema = dataSchema.getDatabaseSchema();
            // System.out.print(JSON.toJSONString(databaseSchema));
            System.out.println("------------generate start...");
            StopWatch timeWatch = new StopWatch("generate_code");
            timeWatch.start();
            generateCode(databaseSchema);
            timeWatch.stop();
            System.out.println(timeWatch.prettyPrint());
            System.out.println("------------generate end...");
            // generateTableDict(applicationContext, databaseSchema);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main error!!!");
        } finally {
            applicationContext.close();
        }
    }

    private static void globalVariableSettings() throws Exception {
        outputRootDir = ConfigManager.getProperty("output.root.dir");
        if (outputRootDir == null || !StringUtils.hasText(outputRootDir)) {
            throw new IOException("output dir not setting");
        }

        // setting template dir
        templateDir = ConfigManager.getProperty("template.file.dir");
        if (!templateDir.endsWith("\\/")) {
            templateDir += "/";
        }
        uiType = Byte.valueOf(ConfigManager.getProperty("ui.config") == null ? "0" : ConfigManager.getProperty("ui.config"));
        if (!UItype.exists(uiType)) {
            throw new Exception("ui selected not config!");
        }

        companyAbbreviation = ConfigManager.getProperty("company.abbreviation");
        if (companyAbbreviation == null || !StringUtils.hasText(companyAbbreviation)) {
            throw new Exception("Required items [company.abbreviation]");
        }
        projectAbbreviation = ConfigManager.getProperty("project.abbreviation");
        if (projectAbbreviation == null || !StringUtils.hasText(projectAbbreviation)) {
            throw new Exception("Required items [project.abbreviation]");
        }
        organizationAbbreviation = ConfigManager.getProperty("organization.abbreviation");
        if (organizationAbbreviation == null || !StringUtils.hasText(organizationAbbreviation)) {
            throw new Exception("Required items [organization.abbreviation]");
        }

        dictionaryTableName = ConfigManager.getProperty("dict.table.name");

        basePackage = organizationAbbreviation + Constants.FOLDER_SEPARATOR + companyAbbreviation + Constants.FOLDER_SEPARATOR + projectAbbreviation;
        basePackagePath = organizationAbbreviation + Constants.FILE_PATH_SEPARATOR + companyAbbreviation + Constants.FILE_PATH_SEPARATOR + projectAbbreviation;

        ConfigManager.setProperty(Constants.BASE_PACKAGE, basePackage);

        generatorPackageFileDir = ConfigManager.getProperty("output.generator.project");
        if (generatorPackageFileDir == null || !StringUtils.hasText(generatorPackageFileDir)) {
            throw new Exception("Required items [output.generator.project]");
        }
    }

    private static void mavenPluginsMyBatisGenerator(String generatorPackagePrefix) throws Exception {
        if (generatorPackagePrefix == null) {
            throw new Exception("generatorPackagePrefix is null");
        }
        if (!generatorPackagePrefix.endsWith(Constants.FOLDER_SEPARATOR)) {
            generatorPackagePrefix = generatorPackagePrefix + Constants.FOLDER_SEPARATOR;
        }
        ClassPathResource classPathResource = new ClassPathResource(
                Constants.GENERATOR_CONFIG_FILE);
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = classPathResource.getFile();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        String clientPackage = generatorPackagePrefix + config.getContexts().get(0).getJavaClientGeneratorConfiguration().getTargetPackage();
        String modelPackage = generatorPackagePrefix + config.getContexts().get(0).getJavaModelGeneratorConfiguration().getTargetPackage();
        String sqlMapPackage = generatorPackagePrefix + config.getContexts().get(0).getSqlMapGeneratorConfiguration().getTargetPackage();
        config.getContexts().get(0).getJavaClientGeneratorConfiguration().setTargetPackage(clientPackage);
        config.getContexts().get(0).getJavaModelGeneratorConfiguration().setTargetPackage(modelPackage);
        config.getContexts().get(0).getSqlMapGeneratorConfiguration().setTargetPackage(sqlMapPackage);

        List<TableConfiguration> tableConfigurations = config.getContexts().get(0).getTableConfigurations();
        for (TableConfiguration tableConfiguration : tableConfigurations) {
            tableNames.add(tableConfiguration.getTableName());
        }

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    private static void generateCode(DatabaseSchema databaseSchema) throws IOException {
        String[] tables = ConfigManager.getProperty("limit.tables").split(Constants.TABLE_NAME_SEPARATOR);
        for (TableSchema tableSchema : databaseSchema.getTables()) {
            //
            if (tables != null && tables.length > 0 && StringUtils.hasText(tables[0])) {
                for (String tableName : tables) {
                    if (StringUtils.hasText(tableName)
                            && tableSchema.getTableName().equalsIgnoreCase(tableName)
                            && tableNames.contains(tableName)) {
                        generate(tableSchema);
                    }
                }
            } else {
                generate(tableSchema);
            }
        }
    }

    private static Map generateDictionary() {
        DataSchema dataSchema = applicationContext.getBean("dataSchema", DataSchema.class);
        BasicDataSource datasource = dataSchema.getDatasource();
        java.sql.Connection connection = null;
        Map<String, Object> mapDataMap = null;
        PreparedStatement queryStatement = null;
        ResultSet rs = null;
        try {
            connection = datasource.getConnection();
            queryStatement = connection.prepareStatement(SqlUtil.getDictSQL());
//			queryStatement.setString(1, "aaa");

            rs = queryStatement.executeQuery();
            mapDataMap = new HashMap<String, Object>();

            List<Dictionary> dictListTemp = null;
            while (rs.next()) {
//				System.out.println(rs.getString("DICT_ITEM"));
//				System.out.println(rs.getInt("DICT_ID"));
//				System.out.println(rs.getString("DICT_VALUE"));
//				System.out.println(rs.getString("DICT_NAME"));

                Dictionary dict = new Dictionary();
                dict.setDictId(rs.getInt("DICT_ID"));
                dict.setDictItem(rs.getString("DICT_ITEM"));
                String enumKeyString = rs.getString("DICT_VALUE");
                // 右边填充空格到30位
                dict.setDictValue(String.format("%-30s", enumKeyString));
                dict.setDictName(rs.getString("DICT_NAME"));
                if (mapDataMap.containsKey(dict.getDictItem())) {
                    dictListTemp = (List) mapDataMap.get(dict.getDictItem());
                    dictListTemp.add(dict);
                } else {
                    List<Dictionary> dictList = new ArrayList<Dictionary>();
                    dictList.add(dict);
                    mapDataMap.put(dict.getDictItem(), dictList);
                }
            }
//			System.out.println(JSON.toJSONString(mapDataMap));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            if (queryStatement != null) {
                try {
                    queryStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return mapDataMap;
    }

//    private static void generateTableDict(ApplicationContext applicationContext, DatabaseSchema databaseSchema) {
//
//        BasicDataSource registryDatasource = applicationContext.getBean("registryDatasource", BasicDataSource.class);
//        java.sql.Connection connection = null;
//        try {
//            connection = registryDatasource.getConnection();
//            PreparedStatement queryStatement = connection.prepareStatement("SELECT 1 from service_dict_head WHERE db_name=? and table_name=? and filed_name=?");
//            PreparedStatement insertStatement = connection
//                    .prepareStatement(
//                            "INSERT INTO service_dict_head"
//                                    + "(db_name,table_name,filed_name,dataType,length,is_key,is_null,default_value,COMMENT,create_userid,create_time,update_userid,update_time,property_name)"
//                                    + "VALUE(?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//            for (TableSchema tableSchema : databaseSchema.getTables()) {
//
//                for (ColumnSchema columnSchema : tableSchema.getColumns()) {
//                    queryStatement.setString(1, tableSchema.getTableCatalog());
//                    queryStatement.setString(2, tableSchema.getTableName());
//                    queryStatement.setString(3, columnSchema.getColumnName());
//
//                    ResultSet rs = queryStatement.executeQuery();
//                    if (rs.next()) {
//                        continue;
//                    }
//                    insertStatement.setString(1, tableSchema.getTableCatalog());
//                    insertStatement.setString(2, tableSchema.getTableName());
//                    insertStatement.setString(3, columnSchema.getColumnName());
//                    insertStatement.setString(4, columnSchema.getDataTypeName());
//                    insertStatement.setDouble(5, columnSchema.getColumnLength());
//                    insertStatement.setBoolean(6, columnSchema.isPrimary());
//                    insertStatement.setBoolean(7, columnSchema.isNotNull());
//                    insertStatement.setString(8, columnSchema.getDefaultValue());
//                    insertStatement.setString(9, columnSchema.getDisplayName());
//                    insertStatement.setString(10, "system");
//                    insertStatement.setLong(11, System.currentTimeMillis());
//                    insertStatement.setString(12, "system");
//                    insertStatement.setLong(13, System.currentTimeMillis());
//                    insertStatement.setString(14, columnSchema.getPropertyName());
//                    insertStatement.execute();
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }

    public static void generate(TableSchema tableSchema) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Velocity.init();
        ToolManager manager = new ToolManager();
        // manager.
        ToolContext ctx = manager.createContext();
        addToolBox(ctx);
        ctx.put("table", tableSchema);

        String codeDate = ConfigManager.getProperty("code.generation.date");
        if (!StringUtils.hasText(codeDate)) {
            codeDate = sdf.format(new Date()).toString();
        }
        ctx.put("nowtime", codeDate);

        String companyName = ConfigManager.getProperty("company.name");
        ctx.put("companyname", companyName);
        ctx.put("projectname", getProjectNameFromConfigFile());

        if (dictionaryTableName != null || StringUtils.hasText(dictionaryTableName)) {
            Map<String, List<Object>> data = generateDictionary();
            String className = "";
            for (Map.Entry<String, List<Object>> entry : data.entrySet()) {
                className = entry.getKey().toLowerCase();
                String[] itemsStrings = className.split("_");
                StringBuffer sbBuffer = new StringBuffer();
                for (String string : itemsStrings) {
                    sbBuffer.append(StringUtils.capitalize(string));
                }
                ctx.put("class", NameUtil.getEnumClassName(sbBuffer.toString()));
                ctx.put("list_dict", entry.getValue());
                ctx.put("caption", ConfigManager.getProperty(entry.getKey()));
                generateDictionary(tableSchema, ctx, sbBuffer.toString());
                ctx.remove("class");
                ctx.remove("list_dict");
                ctx.remove("caption");
            }
        }
        generateAll(tableSchema, ctx);
    }

    // 生成字典类
    public static void generateDictionary(TableSchema tableSchema, ToolContext ctx, String className) throws IOException {
        generate(
                "api/dict.vm",
                outputRootDir + ConfigManager.getProperty("package.name.enums")
                        + "\\" + NameUtil.getEnumClassName(className) + ".java",
                tableSchema,
                ctx);
    }

    // 生成数据验证类
    public static void generateServiceValidate(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "service/validate.vm",
                outputRootDir + ConfigManager.getProperty("package.name.service.validate") + "\\" + NameUtil.getValidateClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    public static void generateControllerBS(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "web/bs_controller.vm",
                outputRootDir + ConfigManager.getProperty("package.name.web.controller") + "\\" + NameUtil.getControllerClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    public static void generateControllerVoBS(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "web/bs_controller_vo.vm",
                outputRootDir + ConfigManager.getProperty("package.name.web.vo") + "\\" + NameUtil.getControllerVoClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    public static void generateViewBS(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "bs_index_view.vm",
                outputRootDir + ConfigManager.getProperty("package.name.web.vo") + "\\" + NameUtil.getModelVarName(tableSchema) + ".html",
                tableSchema,
                ctx);
    }

    public static void generateController(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "controller.vm",
                outputRootDir + "controller\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    public static void generateView(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "index_view.vm",
                outputRootDir + "view\\" + NameUtil.getModelVarName(tableSchema) + ".html",
                tableSchema,
                ctx);
    }

    private static String getProjectNameFromConfigFile() {
        String[] basePackages = ConfigManager.getProperty("basePackage").split("\\.");
        if (basePackages != null) {
            List<String> basePackageList = Arrays.asList(basePackages);
            return basePackageList.get(basePackageList.size() - 1);
        }
        return "";
    }

    public static void generateAll(TableSchema tableSchema, ToolContext ctx) throws IOException {

        // dal
        generateDalModel(tableSchema, ctx);
        /**
         * never used
         * remove query vo
         */
        // generateDalModelQuery(tableSchema, ctx);

        // api
        generateApiReq(tableSchema, ctx);
        generateApiReqQuery(tableSchema, ctx);
        generateApiResp(tableSchema, ctx);

        if (!tableSchema.isView()) {
            // dal
            generateDalMapper(tableSchema, ctx);
            generateDalMapperXml(tableSchema, ctx);

            // biz
            generateBiz(tableSchema, ctx);
            generateBizConvertor(tableSchema, ctx);

            // api
            generateApiServiceInterface(tableSchema, ctx);

            // service
            generateServiceValidate(tableSchema, ctx);
            generateServiceImpl(tableSchema, ctx);
            generateServiceTest(tableSchema, ctx);

            // web
            generateControllerVoBS(tableSchema, ctx);
            generateControllerQueryVoBS(tableSchema, ctx);
            generateControllerConvertor(tableSchema, ctx);
            generateControllerBS(tableSchema, ctx);

        }
    }

    private static String getMapperBaseInterception(TableSchema tableSchema) {
        String fileNameDaoBase = null;
        String contextBase = null;
        String interception = null;
        try {
            fileNameDaoBase = CodeUtil.getFilePathOfMyBatisGenerator(ConfigManager.getProperty("output.mapper.package")) + NameUtil.getMapperClassName(tableSchema) + ".java";
            contextBase = FileUtil.readStringUseNio(fileNameDaoBase);
            int beginIndex = contextBase.indexOf('{') + 1;
            int endIndex = contextBase.indexOf('}');
            interception = contextBase.substring(beginIndex, endIndex);
        } catch (Exception e) {
            System.out.println("get file[" + fileNameDaoBase + "] Interception context error!!!!");
        }
        return interception;
    }

    private static String getMapperXmlBaseInterception(TableSchema tableSchema) {
        String fileNameDaoBase = null;
        String contextBase = null;
        String Interception = null;
        try {
            fileNameDaoBase = CodeUtil.getFilePathOfMyBatisGenerator(ConfigManager.getProperty("output.mapperxml.package")) + NameUtil.getMapperClassName(tableSchema) + ".xml";
            contextBase = FileUtil.readStringUseNio(fileNameDaoBase);
            String StringStart = "<resultMap";
            int beginIndex = contextBase.indexOf(StringStart);
            int endIndex = contextBase.indexOf("</mapper>");
            Interception = contextBase.substring(beginIndex, endIndex);
//			System.out.println(Interception);
        } catch (Exception e) {
            System.out.println("get file[" + fileNameDaoBase + "] Interception context error!!!!");
        }
        return Interception;
    }

    private static void generateServiceTest(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "service/service_test.vm",
                outputRootDir + ConfigManager.getProperty("package.name.service.test") + "\\" + NameUtil.getTestClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateBizConvertor(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "biz/biz_convertor.vm",
                outputRootDir + ConfigManager.getProperty("package.name.biz.convertor") + "\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateControllerConvertor(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "web/bs_controller_convertor.vm",
                outputRootDir + ConfigManager.getProperty("package.name.web.vo.convertor") + "\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateServiceImpl(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "service/service_impl.vm",
                outputRootDir + ConfigManager.getProperty("package.name.service.impl") + "\\" + NameUtil.getServiceImplClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateControllerQueryVoBS(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "web/bs_controllerQuery_vo.vm",
                outputRootDir + ConfigManager.getProperty("package.name.web.vo") + "\\" + NameUtil.getControllerQueryVoClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateApiServiceInterface(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "api/interface.vm",
                outputRootDir + ConfigManager.getProperty("package.name.service") + "\\" + NameUtil.getServiceClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateDalMapper(TableSchema tableSchema, ToolContext ctx) throws IOException {
        ctx.put("mapperbase", getMapperBaseInterception(tableSchema));
        generate(
                "dal/mapper.vm",
                outputRootDir + ConfigManager.getProperty("package.name.mapper") + "\\" + NameUtil.getMapperClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
        ctx.remove("mapperbase");
    }

    private static void generateDalMapperXml(TableSchema tableSchema, ToolContext ctx) throws IOException {
        ctx.put("mapperbasexml", getMapperXmlBaseInterception(tableSchema));
        generate(
                "dal/mapper_xml.vm",
                outputRootDir + ConfigManager.getProperty("package.name.mapper.xml") + "\\" + NameUtil.getModelClassName(tableSchema) + "Mapper.xml",
                tableSchema,
                ctx);
        ctx.remove("mapperbasexml");
    }

    private static void generateBiz(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "biz/biz.vm",
                outputRootDir + ConfigManager.getProperty("package.name.biz") + "\\" + NameUtil.getBizClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateApiResp(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "api/api_resp.vm",
                outputRootDir + ConfigManager.getProperty("package.name.resp") + "\\" + NameUtil.getMessageClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateApiReq(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "api/api_req.vm",
                outputRootDir + ConfigManager.getProperty("package.name.req") + "\\" + NameUtil.getMessageReqClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateApiReqQuery(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "api/api_reqQuery.vm",
                outputRootDir + ConfigManager.getProperty("package.name.req") + "\\" + NameUtil.getMessageQueryClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generateDalModel(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "dal/model.vm",
                outputRootDir + ConfigManager.getProperty("package.name.model") + "\\" + NameUtil.getModelClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
        // 拷贝xxExample.java到model目录
        String xxExamplePath = null;
        String context = null;
        String outPutJavaFile = outputRootDir + ConfigManager.getProperty("package.name.model") + "\\" + NameUtil.getModelClassName(tableSchema) + "Example.java";
        try {
            xxExamplePath = CodeUtil.getFilePathOfMyBatisGenerator(ConfigManager.getProperty("output.model.package")) + NameUtil.getModelClassName(tableSchema) + "Example.java";
            context = FileUtil.readStringUseNio(xxExamplePath);
            FileUtil.writeUseBio(outPutJavaFile, context);
        } catch (Exception e) {
            System.out.println("copy file[" + outPutJavaFile + "] error!!!");
        }
    }

    private static void generateDalModelQuery(TableSchema tableSchema, ToolContext ctx) throws IOException {
        generate(
                "dal/modelQuery.vm",
                outputRootDir + ConfigManager.getProperty("package.name.model") + "\\" + NameUtil.getModelQueryClassName(tableSchema) + ".java",
                tableSchema,
                ctx);
    }

    private static void generate(
            String templateName,
            String fileName,
            TableSchema tableSchema,
            ToolContext ctx) throws IOException {
        PrintWriter filewriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            String templatePath = templateDir + templateName;
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(fileName);
            filewriter = new PrintWriter(fileOutputStream, true);
            filewriter.print(generateCode(templatePath, tableSchema, ctx));
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } finally {
            filewriter.close();
            fileOutputStream.close();
        }
    }

    public static String generateCode(
            String templateName,
            TableSchema tableSchema,
            ToolContext ctx) {
        StringWriter writer = new StringWriter();
        Template template = Velocity.getTemplate(templateName, "utf-8");
        template.merge(ctx, writer);
        return writer.toString().replaceAll("\t", "    ");
    }

    private static void deleteSubFiles(File parentFile) {
        if (!parentFile.isDirectory()) {
            return;
        }
        String[] subFiles = parentFile.list();
        for (String pathname : subFiles) {
            File file = new File(parentFile, pathname);
            if (file.isDirectory()) {
                deleteSubFiles(file);
            }
            file.delete();
        }
    }

    private static void addToolBox(ToolContext ctx) {
        Map<String, ToolInfo> toolMap = new HashMap<String, ToolInfo>();
        toolMap.put("config", new ToolInfo("config", ConfigManager.class));
        toolMap.put("sql", new ToolInfo("sql", SqlUtil.class));
        toolMap.put("stringUtil", new ToolInfo("stringUtil", StringUtil.class));
        toolMap.put("name", new ToolInfo("name", NameUtil.class));
        toolMap.put("code", new ToolInfo("code", CodeUtil.class));
        Toolbox toolbox = new Toolbox(toolMap);
        ctx.addToolbox(toolbox);


    }
}
