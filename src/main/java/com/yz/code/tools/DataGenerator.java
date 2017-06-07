package com.yz.code.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolInfo;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.Toolbox;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yz.code.schema.ColumnSchema;
import com.yz.code.schema.DatabaseSchema;
import com.yz.code.schema.Dictionary;
import com.yz.code.schema.TableSchema;
import com.yz.code.util.CodeUtil;
import com.yz.code.util.ConfigManager;
import com.yz.code.util.FileUtil;
import com.yz.code.util.NameUtil;
import com.yz.code.util.SqlUtil;
import com.yz.code.util.StringUtil;

public class DataGenerator {
	public static ClassPathXmlApplicationContext applicationContext = null;
	public static Boolean isGeneratedDict = false;
	
	public static void main(String[] args) {
		try {
			applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			DataSchema dataSchema = applicationContext.getBean("dataSchema", DataSchema.class);
			DatabaseSchema databaseSchema = dataSchema.getDatabaseSchema();
			// System.out.print(JSON.toJSONString(databaseSchema));
			System.out.println("------------生成代码开始------------");
			StopWatch timeWatch = new StopWatch("generate_code");
			timeWatch.start();
			generateCode(databaseSchema);
			timeWatch.stop();
			System.out.println(timeWatch.prettyPrint());
			System.out.println("------------生成代码结束------------");
			// generateTableDict(applicationContext, databaseSchema);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("main error!!!");
		} finally {
			applicationContext.close();
		}
	}

	private static void generateCode(DatabaseSchema databaseSchema) throws IOException {
		String[] tables = ConfigManager.getProperty("tables").split("\\,");
		String rootDir = ConfigManager.getProperty("output.root.dir");
		deleteSubFiles(new File(rootDir));
		for (TableSchema tableSchema : databaseSchema.getTables()) {
			for (String tableName : tables) {
				if (tableSchema.getTableName().equalsIgnoreCase(tableName)) {
					generate(tableSchema, rootDir);
				}
			}
		}
		
		// 生成字典类
		
	}

	private static Map generateDictionary() {
		DataSchema dataSchema = applicationContext.getBean("dataSchema", DataSchema.class);
		BasicDataSource datasource = dataSchema.getDatasource();
		java.sql.Connection connection = null;
		Map<String, Object> mapDataMap = null;
		try {
			connection = datasource.getConnection();
			PreparedStatement queryStatement = connection.prepareStatement("SELECT dict_item,dict_id,dict_value,dict_name FROM mmc_dictionary WHERE STATUS = 1");
//			queryStatement.setString(1, "aaa");

			ResultSet rs = queryStatement.executeQuery();
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
					dictListTemp = (List)mapDataMap.get(dict.getDictItem());
					dictListTemp.add(dict);
				}else {
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
		}
		return mapDataMap;
	}
	
	private static void generateTableDict(ApplicationContext applicationContext, DatabaseSchema databaseSchema) {

		BasicDataSource registryDatasource = applicationContext.getBean("registryDatasource", BasicDataSource.class);
		java.sql.Connection connection = null;
		try {
			connection = registryDatasource.getConnection();
			PreparedStatement queryStatement = connection.prepareStatement("SELECT 1 from service_dict_head WHERE db_name=? and table_name=? and filed_name=?");
			PreparedStatement insertStatement = connection
					.prepareStatement(
					"INSERT INTO service_dict_head"
							+ "(db_name,table_name,filed_name,dataType,length,is_key,is_null,default_value,COMMENT,create_userid,create_time,update_userid,update_time,property_name)"
							+ "VALUE(?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			for (TableSchema tableSchema : databaseSchema.getTables()) {

				for (ColumnSchema columnSchema : tableSchema.getColumns()) {
					queryStatement.setString(1, tableSchema.getTableCatalog());
					queryStatement.setString(2, tableSchema.getTableName());
					queryStatement.setString(3, columnSchema.getColumnName());

					ResultSet rs = queryStatement.executeQuery();
					if (rs.next()) {
						continue;
					}
					insertStatement.setString(1, tableSchema.getTableCatalog());
					insertStatement.setString(2, tableSchema.getTableName());
					insertStatement.setString(3, columnSchema.getColumnName());
					insertStatement.setString(4, columnSchema.getDataTypeName());
					insertStatement.setDouble(5, columnSchema.getColumnLength());
					insertStatement.setBoolean(6, columnSchema.isPrimary());
					insertStatement.setBoolean(7, columnSchema.isNotNull());
					insertStatement.setString(8, columnSchema.getDefaultValue());
					insertStatement.setString(9, columnSchema.getDisplayName());
					insertStatement.setString(10, "system");
					insertStatement.setLong(11, System.currentTimeMillis());
					insertStatement.setString(12, "system");
					insertStatement.setLong(13, System.currentTimeMillis());
					insertStatement.setString(14, columnSchema.getPropertyName());
					insertStatement.execute();
				}

			}
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

		}
	}

	public static void generate(TableSchema tableSchema, String rootDir) throws IOException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Velocity.init();
		ToolManager manager = new ToolManager();
		// manager.
		ToolContext ctx = manager.createContext();
		addToolBox(ctx);
		ctx.put("table", tableSchema);
		// 加入当前时间
		ctx.put("nowtime", sdf.format(new Date()));
		
		if (!isGeneratedDict) {
			Map<String,List<Object>> data = generateDictionary();
			String className = "";
			for (Map.Entry<String, List<Object>> entry:data.entrySet()) {
				className = entry.getKey().toLowerCase();
				String[] itemsStrings = className.split("_");
				StringBuffer sbBuffer = new StringBuffer();
				for (String string : itemsStrings) {
					sbBuffer.append(StringUtils.capitalize(string));
				}
				ctx.put("class", sbBuffer.toString());
				ctx.put("list_dict", entry.getValue());
				ctx.put("caption", ConfigManager.getProperty(entry.getKey()));
				generateDictionary(tableSchema, ctx, rootDir, sbBuffer.toString());
				ctx.remove("class");
				ctx.remove("list_dict");
				ctx.remove("caption");
			}
			isGeneratedDict = true;
		}
		
		generateAll(tableSchema, ctx, rootDir);
		// generateViewAndController(tableSchema, ctx, rootDir);
		// generate(
		// "/src/main/resources/templates/index_edit.vm",
		// rootDir + "views\\" + NameUtil.getModelVarName(tableSchema) +
		// "\\edit.html",
		// tableSchema,
		// ctx);
		// generateMessageQuery(tableSchema, ctx, rootDir);
		// generateMapperXml(tableSchema, ctx, rootDir);
		// generateMapper(tableSchema, ctx, rootDir);
		// generateControllerBS(tableSchema, ctx, rootDir);
		// generateViewBS(tableSchema, ctx, rootDir);
		// generateMapper(tableSchema, ctx, rootDir);
		// generateMessageQuery(tableSchema, ctx, rootDir);
		
		//OK generateException(tableSchema, ctx, rootDir);
		//OK generateBiz(tableSchema, ctx, rootDir);
		//OK generateValidate(tableSchema, ctx, rootDir);
		//OK generateServiceInterface(tableSchema, ctx, rootDir);
		//OK generateServiceImpl(tableSchema, ctx, rootDir);
		//OK generateConvertor(tableSchema, ctx, rootDir);
		
		//OK generateTest(tableSchema, ctx, rootDir);
		//OK generateControllerVoBS(tableSchema, ctx, rootDir);
		//OK generateControllerQueryVoBS(tableSchema, ctx, rootDir);
		//OK generateControllerConvertor(tableSchema, ctx, rootDir);
		//OK generateControllerBS(tableSchema, ctx, rootDir);
		
		
	}

	// 生成字典类
	public static void generateDictionary(TableSchema tableSchema, ToolContext ctx, String rootDir, String className) throws IOException {
		generate(
				"/src/main/resources/templates/dict.vm",
				rootDir + "10_dict\\" + className + ".java",
				tableSchema,
				ctx);
	}
	
	// 生成数据验证类
	public static void generateValidate(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/validate.vm",
				rootDir + "18_validate\\" + NameUtil.getValidateClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	// 生成异常类
	public static void generateException(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/exception.vm",
				rootDir + "16_exception\\" + NameUtil.getExceptionClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	public static void generateControllerBS(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/bs_controller.vm",
				rootDir + "24_controller\\" + NameUtil.getControllerClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	public static void generateControllerVoBS(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/bs_controller_vo.vm",
				rootDir + "22_controller_vo\\" + NameUtil.getControllerVoClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	public static void generateViewBS(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/bs_index_view.vm",
				rootDir + "view\\" + NameUtil.getModelVarName(tableSchema) + ".html",
				tableSchema,
				ctx);
	}

	public static void generateController(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/controller.vm",
				rootDir + "controller\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	public static void generateView(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/index_view.vm",
				rootDir + "view\\" + NameUtil.getModelVarName(tableSchema) + ".html",
				tableSchema,
				ctx);
	}

	public static void generateAll(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {

		// 11
		generateModel(tableSchema, ctx, rootDir);
		generateModelQuery(tableSchema, ctx, rootDir);
		// 12
		generateMessage(tableSchema, ctx, rootDir);
		generateMessageReq(tableSchema, ctx, rootDir);
		generateMessageQuery(tableSchema, ctx, rootDir);
		if (!tableSchema.isView()) {
			// dal
			// 13
			generateMapper(tableSchema, ctx, rootDir);
			// 14
			generateMapperXml(tableSchema, ctx, rootDir);
			
			// biz
			// 15
			generateBiz(tableSchema, ctx, rootDir);
			// 16
			generateException(tableSchema, ctx, rootDir);
			
			// api
			// 17
			generateServiceInterface(tableSchema, ctx, rootDir);
			
			// service
			// 18
			generateValidate(tableSchema, ctx, rootDir);
			// 19
			generateServiceImpl(tableSchema, ctx, rootDir);
			// 20
			generateConvertor(tableSchema, ctx, rootDir);
			// 21
			generateTest(tableSchema, ctx, rootDir);
			
			// web
			// 22
			generateControllerVoBS(tableSchema, ctx, rootDir);
			generateControllerQueryVoBS(tableSchema, ctx, rootDir);
			// 23
			generateControllerConvertor(tableSchema, ctx, rootDir);
			// 24
			generateControllerBS(tableSchema, ctx, rootDir);
			
		}
	}

	private static String getMapperBaseInterception(TableSchema tableSchema){
		String fileNameDaoBase = "";
		String contextBase = "";
		String Interception = "";
		try {
			fileNameDaoBase = ConfigManager.getProperty("merge.dir") + "dao\\" + NameUtil.getMapperClassName(tableSchema) + ".java";
			contextBase = FileUtil.readStringUseNio(fileNameDaoBase);
			int beginIndex = contextBase.indexOf('{') + 1;
			int endIndex = contextBase.indexOf('}');
			Interception = contextBase.substring(beginIndex, endIndex);
		} catch (Exception e) {
			System.out.println( "get file[" + fileNameDaoBase + "] Interception context error!!!!");
		}
		return Interception;
	}
	
	private static String getMapperXmlBaseInterception(TableSchema tableSchema){
		String fileNameDaoBase = "";
		String contextBase = "";
		String Interception = "";
		try {
			fileNameDaoBase = ConfigManager.getProperty("merge.dir") + "mapping\\" + NameUtil.getMapperClassName(tableSchema) + ".xml";
			contextBase = FileUtil.readStringUseNio(fileNameDaoBase);
			String StringStart = "<resultMap";
			int beginIndex = contextBase.indexOf(StringStart);
			int endIndex = contextBase.indexOf("</mapper>");
			Interception = contextBase.substring(beginIndex, endIndex);
//			System.out.println(Interception);
		} catch (Exception e) {
			System.out.println( "get file[" + fileNameDaoBase + "] Interception context error!!!!");
		}
		return Interception;
	}

	private static void generateTest(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/service_test.vm",
				rootDir + "21_tests\\" + NameUtil.getTestClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateConvertor(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/convertor.vm",
				rootDir + "20_service_convertor\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateControllerConvertor(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/bs_controller_convertor.vm",
				rootDir + "23_controller_convertor\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	private static void generateServiceImpl(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/service_impl.vm",
				rootDir + "19_service_impl\\" + NameUtil.getServiceImplClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateMessageQuery(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/messageQuery.vm",
				rootDir + "12_req\\" + NameUtil.getMessageQueryClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateControllerQueryVoBS(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/bs_controllerQuery_vo.vm",
				rootDir + "22_controller_vo\\" + NameUtil.getControllerQueryVoClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	private static void generateServiceInterface(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/service.vm",
				rootDir + "17_service\\" + NameUtil.getServiceClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateModelQuery(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/modelQuery.vm",
				rootDir + "11_model\\" + NameUtil.getModelQueryClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateMapper(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		ctx.put("mapperbase", getMapperBaseInterception(tableSchema));
		generate(
				"/src/main/resources/templates/mapper.vm",
				rootDir + "13_dao\\" + NameUtil.getMapperClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
		ctx.remove("mapperbase");
	}

	private static void generateMapperXml(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		ctx.put("mapperbasexml", getMapperXmlBaseInterception(tableSchema));
		generate(
				"/src/main/resources/templates/mapper_xml.vm",
				rootDir + "14_mappers\\" + NameUtil.getModelClassName(tableSchema) + "Mapper.xml",
				tableSchema,
				ctx);
		ctx.remove("mapperbasexml");
	}
	
	private static void generateBiz(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/biz.vm",
				rootDir + "15_biz\\" + NameUtil.getBizClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateMessage(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/message.vm",
				rootDir + "12_message\\" + NameUtil.getMessageClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generateMessageReq(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/message_req.vm",
				rootDir + "12_req\\" + NameUtil.getMessageReqClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}
	
	private static void generateModel(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/model.vm",
				rootDir + "11_model\\" + NameUtil.getModelClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
		// 拷贝xxExample.java到model目录
		String xxExamplePath = "";
		String context = "";
		String outPutJavaFile = rootDir + "11_model\\" + NameUtil.getModelClassName(tableSchema) + "Example.java";
		try {
			xxExamplePath = ConfigManager.getProperty("merge.dir") + "model\\" + NameUtil.getModelClassName(tableSchema) + "Example.java";
			context = FileUtil.readStringUseNio(xxExamplePath);
			FileUtil.writeUseBio(outPutJavaFile, context);
		} catch (Exception e) {
			System.out.println("copy file[" + outPutJavaFile + "] error!!!") ;
		}
	}

	private static void generate(
			String templateName,
			String fileName,
			TableSchema tableSchema,
			ToolContext ctx) throws IOException {
		PrintWriter filewriter = null;
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileOutputStream = new FileOutputStream(fileName);
			filewriter = new PrintWriter(fileOutputStream, true);
			filewriter.print(generateCode(templateName, tableSchema, ctx));
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally
		{
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
		return writer.toString();
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
