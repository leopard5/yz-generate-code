package com.yz.code.main;

import com.yz.code.constant.Constants;
import com.yz.code.db.DBHelper;
import com.yz.code.entity.CreAttr;
import com.yz.code.entity.DbTableColumn;
import com.yz.code.entity.MysqlDbColumn;
import com.yz.code.form.MySqlSetPanel;
import com.yz.code.util.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Creater {
    private static Logger log = Logger.getLogger(Creater.class);

    CreAttr ca = null;
    String tableName = "", tableComment = "";
    List<DbTableColumn> columnList = null;
    String dir = "template/";
    private String entityName = null;
    private String primaryKey = null;
    private String primaryColumn = null;
    private String primaryKeyJdbcType = null;
    private String primaryKeyJaveType = null;
    boolean hasDate = false,
            hasTimestamp = false;

    public Creater(CreAttr ca, String tname, String tdesc, List<DbTableColumn> columnList) {
        this.ca = ca;
        this.tableName = tname;
        this.tableComment = tdesc;
        this.columnList = columnList;
        getEntityName();
    }

    public String getEntityName() {
        if (entityName == null) {
            entityName = Utililies.tableToEntity(tableName);
            ca.replaceAll(tableName);
        }
        return entityName;
    }

    public String getTlpPath(String name) {
        String fix = "mysql_";
        if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
            fix = "mysql_";
        }
        return dir + fix + name;
    }

    public void run() {
        String result = createEntity();
        log.info(result);
        //result = createArgument();
        //result = createResponse();
        log.info(result);
        // 持久层框架
        if (ca.getDaoFrame().equalsIgnoreCase("mybatis")) {
            if (ca.isCreateBaseDao()) {
                result = createIBaseDao();
                log.info(result);
                result = createBaseDao();
                log.info(result);
            }
            if (ca.isCreateDao()) {
                result = createDaoMapper();
                log.info(result);
            }
            if (ca.isCreateBaseService()) {
                result = createIBaseService();
                log.info(result);
                result = createBaseService();
                log.info(result);
            }
            if (ca.isCreateService()) {
                result = createIService();
                log.info(result);
                result = createService();
                log.info(result);
            }
            if (ca.isCreateMapper()) {
                result = createMapper();
                log.info(result);
            }
        } else {

        }
        System.out.println(ca.getConFrame());
        // 控制层框架
        if (ca.getConFrame().equalsIgnoreCase("SpringMVC")) {
            if (ca.isCreateBaseController()) {
                result = createBaseController();
                log.info(result);
            }
            if (ca.isCreateController()) {
                result = createController();
                log.info(result);
            }
        } else {

        }

    }

    public String createEntity() {
        String result = "实体生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), ca.getEntityFilePath());
        try {
            String content = Utililies.readResourceFile(dir + "entity.tlp");
            // package
            content = Utililies.parseTemplate(content, "package", Utililies.getSuperPackage(ca.getEntityPackage()));
            // EntityName
            content = Utililies.parseTemplate(content, "EntityName", getEntityName());
            // attr_list

            content = Utililies.parseTemplate(content, "attr_list", createAttrList());
            content = Utililies.parseTemplate(content, "importDate", hasDate ? Constants.IMPORT_DATE + ";" : "");
            content = Utililies.parseTemplate(content, "importTimestamp", hasTimestamp ? Constants.IMPORT_TIMESTAMP + ";" : "");
            // attr_getset_list
            content = Utililies.parseTemplate(content, "attr_getset_list", createAttrGetsetList());

            content = Utililies.parseTemplate(content, "attr_tostring_list", createAttrToStringList());

            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));

            Utililies.writeContentToFile(Utililies.parseFilePath(ca.getSaveDir(), ca.getEntityFilePath()), content);

        } catch (Exception e) {
            result = "实体生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createArgument() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getEntityFilePath()) + "base/Argument.java");
        String result = "出入参对象类生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "出入参对象类已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(dir + "baseArg.tlp");
                // EntitySuperPackage
                content = Utililies.parseTemplate(content, "EntitySuperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
                content = Utililies.parseTemplate(content, "PrimaryJavaType", getPrimaryJavaType());
                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "出入参对象类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createResponse() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getEntityFilePath()) + "base/Response.java");
        String result = "结果对象类生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "结果对象类已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(dir + "baseResult.tlp");
                // EntitySuperPackage
                content = Utililies.parseTemplate(content, "EntitySuperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "结果对象类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createIBaseDao() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getDaoFilePath()) + "base/IBaseDao.java");
        String result = "IBaseDao接口生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "IBaseDao接口已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(getTlpPath("ibase_dao.tlp"));
                // package
                content = Utililies.parseTemplate(content, "DaoSuperPackage", Utililies.getSuperPackage(ca.getDaoPackage()));

                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));

                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "IBaseDao接口生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createBaseDao() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getDaoFilePath()) + "base/BaseDao.java");
        String result = "BaseDao类生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "BaseDao类已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(getTlpPath("base_dao.tlp"));
                // package
                content = Utililies.parseTemplate(content, "DaoSuperPackage", Utililies.getSuperPackage(ca.getDaoPackage()));

                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));

                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "BaseDao接口生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createDaoMapper() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), ca.getDaoFilePath());
        String result = ca.getDaoName() + "类生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(getTlpPath("dao.tlp"));
            // package
            content = Utililies.parseTemplate(content, "DaoSuperPackage", Utililies.getSuperPackage(ca.getDaoPackage()));
            content = Utililies.parseTemplate(content, "EntityPackage", ca.getEntityPackage());
            content = Utililies.parseTemplate(content, "DaoClassName", ca.getDaoName());
            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));

            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = ca.getDaoName() + "类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createIBaseService() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getServiceFilePath()) + "base/IBaseService.java");
        String result = "IBaseService接口生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "IBaseService接口已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(getTlpPath("ibase_service.tlp"));
                // package
                content = Utililies.parseTemplate(content, "ServiceSuperPackage", Utililies.getSuperPackage(ca.getServicePackage()));
                content = Utililies.parseTemplate(content, "DaoPackage", ca.getDaoPackage());
                content = Utililies.parseTemplate(content, "EntitySuperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));

                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "IBaseService接口生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createBaseService() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getServiceFilePath()) + "base/BaseService.java");
        String result = "BaseService类生成完成：" + Utililies.parseFilePath(ca.getSaveDir(), javaPath);
        try {
            File javaFile = new File(javaPath);
            if (javaFile.exists()) {
                result = "BaseService类已存在，无须重新生成";
            } else {
                String content = Utililies.readResourceFile(getTlpPath("base_service.tlp"));
                // package
                content = Utililies.parseTemplate(content, "ServiceSuperPackage", Utililies.getSuperPackage(ca.getServicePackage()));
                content = Utililies.parseTemplate(content, "DaoSupperPackage", Utililies.getSuperPackage(ca.getDaoPackage()));
                content = Utililies.parseTemplate(content, "EntityName", getEntityName());
                content = Utililies.parseTemplate(content, "EntityPackage", ca.getEntityPackage());
                content = Utililies.parseTemplate(content, "EntitySupperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
                content = Utililies.parseTemplate(content, "DaoPackage", ca.getDaoPackage());
                content = Utililies.parseTemplate(content, "DaoClassName", ca.getDaoName());

                content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
                Utililies.writeContentToFile(javaPath, content);
            }
        } catch (Exception e) {
            result = "BaseService类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createIService() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), Utililies.parseTemplate(ca.getIserviceFilePath(), "EntityName", getEntityName()));
        String result = "I" + ca.getEntityName() + "接口生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(getTlpPath("iservice.tlp"));
            // package
            content = Utililies.parseTemplate(content, "IServiceSuperPackage", Utililies.getSuperPackage(ca.getIservicePackage()));
            content = Utililies.parseTemplate(content, "EntitySupperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
            content = Utililies.parseTemplate(content, "EntityPackage", ca.getEntityPackage());
            content = Utililies.parseTemplate(content, "EntityName", getEntityName());
            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = "I" + ca.getEntityName() + "接口生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createService() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), ca.getServiceFilePath());
        String result = ca.getServiceName() + "类生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(getTlpPath("service.tlp"));
            // package
            content = Utililies.parseTemplate(content, "ServiceSuperPackage", Utililies.getSuperPackage(ca.getServicePackage()));
            content = Utililies.parseTemplate(content, "EntitySupperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
            content = Utililies.parseTemplate(content, "DaoSuperPackage", ca.getEntityPackage());
            content = Utililies.parseTemplate(content, "EntityPackage", ca.getEntityPackage());
            content = Utililies.parseTemplate(content, "EntityName", getEntityName());
            content = Utililies.parseTemplate(content, "IServicePackage", ca.getIservicePackage());
            content = Utililies.parseTemplate(content, "ServiceName", ca.getServiceName());
            content = Utililies.parseTemplate(content, "IServiceName", ca.getIserviceName());
            content = Utililies.parseTemplate(content, "ServiceNameLower", CommonUtils.firstCharToLowerCase(ca.getServiceName()));
            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            content = Utililies.parseTemplate(content, "PrimaryJavaType", getPrimaryJavaType());
            content = Utililies.parseTemplate(content, "PrimaryFeild", Utililies.columnToFeild(getPrimaryColumn()));
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = ca.getServiceName() + "类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createMapper() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), ca.getMapFilePath());
        if (javaPath != null && javaPath.indexOf("main/java/main/") != -1) {
            javaPath = Utililies.parseFilePath(ca.getSaveDir2(), ca.getMapFilePath());
        }
        String result = ca.getMapName() + ".xml生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(getTlpPath("mapper.tlp"));
            // package
            content = Utililies.parseTemplate(content, "MapperPackage", ca.getDaoPackage());
            content = Utililies.parseTemplate(content, "EntityPackage", ca.getEntityPackage());
            content = Utililies.parseTemplate(content, "PrimaryColumn", getPrimaryColumn());
            content = Utililies.parseTemplate(content, "PrimaryJdbcType", getPrimaryJdbcType());
            content = Utililies.parseTemplate(content, "PrimaryJavaType", getPrimaryJavaType());
            content = Utililies.parseTemplate(content, "PrimaryFeild", Utililies.columnToFeild(getPrimaryColumn()));
            content = Utililies.parseTemplate(content, "FeildMapList", getFeildMapList());
            content = Utililies.parseTemplate(content, "FeildJoinId", getFeildJoinId());
            content = Utililies.parseTemplate(content, "TableName", tableName);
            content = Utililies.parseTemplate(content, "FeildIfList", getFeildIfList());
            content = Utililies.parseTemplate(content, "FeildJoin", getFeildJoin());
            content = Utililies.parseTemplate(content, "FeildMapJoin", getFeildMapJoin());
            content = Utililies.parseTemplate(content, "FeildIfJoin", getFeildIfJoin());
            content = Utililies.parseTemplate(content, "FeildIfMapJoin", getFeildIfMapJoin());
            content = Utililies.parseTemplate(content, "FeildIfSetList", getFeildIfSetList());
            content = Utililies.parseTemplate(content, "FeildSetList", getFeildSetList());
            content = Utililies.parseTemplate(content, "ForeachFeildMapJoin", getForeachFeildMapJoin());
            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = ca.getMapName() + ".xml生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createBaseController() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), FileUtils.getFileDir(ca.getControllerFilePath()) + "base/BaseController.java");
        String result = "BaseController类生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(dir + "baseController.tlp");
            // package
            content = Utililies.parseTemplate(content, "ControllerSuperPackage", Utililies.getSuperPackage(ca.getControllerPackage()));
            content = Utililies.parseTemplate(content, "EntitySuperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
            content = Utililies.parseTemplate(content, "ServiceSuperPackage", Utililies.getSuperPackage(ca.getServicePackage()));
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = "BaseController类生成失败：" + e.getMessage();
        }
        return result;
    }

    public String createController() {
        String javaPath = Utililies.parseFilePath(ca.getSaveDir(), ca.getControllerFilePath());
        String result = ca.getControllerName() + "生成完成：" + javaPath;
        try {
            String content = Utililies.readResourceFile(dir + "controller.tlp");
            // package
            content = Utililies.parseTemplate(content, "ControllerSuperPackage", Utililies.getSuperPackage(ca.getControllerPackage()));
            content = Utililies.parseTemplate(content, "ControllerNameLower", CommonUtils.firstCharToLowerCase(ca.getControllerName()));
            content = Utililies.parseTemplate(content, "ControllerName", ca.getControllerName());
            content = Utililies.parseTemplate(content, "ServiceName", ca.getServiceName());
            content = Utililies.parseTemplate(content, "ServiceNameLower", CommonUtils.firstCharToLowerCase(ca.getServiceName()));
            content = Utililies.parseTemplate(content, "EntityName", getEntityName());
            content = Utililies.parseTemplate(content, "EntitySuperPackage", Utililies.getSuperPackage(ca.getEntityPackage()));
            content = Utililies.parseTemplate(content, "IServicePackage", ca.getIservicePackage());
            content = Utililies.parseTemplate(content, "EntityComment", tableComment);
            content = Utililies.parseTemplate(content, "Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            Utililies.writeContentToFile(javaPath, content);
        } catch (Exception e) {
            result = ca.getControllerName() + "生成失败：" + e.getMessage();
        }
        return result;
    }

    public void createHbm() {

    }

    public void createAction() {

    }

    /**************************/

    public String createAttrList() {
        StringBuffer sb = new StringBuffer();
        if (columnList != null) {
            MysqlDbColumn column = null;
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    column = (MysqlDbColumn) columnList.get(i);
                    if (column != null) {
                        if (!hasDate && column.getDataType().equalsIgnoreCase("date")) {
                            hasDate = true;
                        }
                        if (!hasTimestamp && column.getDataType().equalsIgnoreCase("timestamp")
                                || column.getDataType().equalsIgnoreCase("time")) {
                            hasTimestamp = true;
                        }
                        sb.append(Constants.TAB1).append("/** ")
                                .append(CommonUtils.isBlank(column.getColumnComment()) ? column.getColumnName()
                                        : column.getColumnComment())
                                .append(" */").append(Constants.ENTER).append(Constants.TAB1)
                                .append(Utililies.getAttrDeclare(Utililies.getVarJavaType(column.getDataType()),
                                        Utililies.columnToFeild(column.getColumnName()),
                                        column.getColumnDefault()))
                                .append(Constants.ENTER).append(Constants.ENTER);
                    }
                }
            }
        }
        return sb.toString();
    }

    public String createAttrGetsetList() {
        StringBuffer sb = new StringBuffer();
        if (columnList != null) {
            MysqlDbColumn column = null;
            String content = null, attr = null;
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    column = (MysqlDbColumn) columnList.get(i);
                    if (column != null) {
                        attr = Utililies.tableToEntity(column.getColumnName());
                        content = Utililies.readResourceFile(dir + "getset.tlp");
                        content = Utililies.parseTemplate(content, "EntityName", getEntityName());
                        content = Utililies.parseTemplate(content, "AttrName", CommonUtils.firstCharToUpperCase(attr));
                        content = Utililies.parseTemplate(content, "attrName", CommonUtils.firstCharToLowerCase(attr));
                        content = Utililies.parseTemplate(content, "comment", CommonUtils.isBlank(column.getColumnComment())
                                ? column.getColumnName() : column.getColumnComment());
                        content = Utililies.parseTemplate(content, "JavaType", Utililies.getJavaType(column.getDataType()));

                        sb.append(content).append(Constants.ENTER);
                    }
                }
            }
        }
        return sb.toString();
    }

    public String createAttrToStringList() {
        StringBuffer sb = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                //sb.append("; ordinalPosition=" + (ordinalPosition == null ? "null" : ordinalPosition.toString()));
                String maps = "sb.append(\"; {0}=\" + ({0} == null ? \"null\" : {0}.toString()));\r\n";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    sb.append(Constants.TAB2).append(CommonUtils.format(maps, Utililies.columnToFeild(mdc.getColumnName())));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取主键字段
     */
    public DbTableColumn getPrimaryKeyColumn() {
        DbTableColumn dc = null;
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        dc = mdc;
                        break;
                    }
                }
            }
        }
        return dc;
    }

    /**
     * 获取主键字段名
     */
    public String getPrimaryColumn() {
        if (primaryColumn == null) {
            DbTableColumn dc = getPrimaryKeyColumn();
            if (dc != null) {
                if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                    MysqlDbColumn mdc = (MysqlDbColumn) dc;
                    if (mdc.isPrimaryKey()) {
                        primaryColumn = mdc.getColumnName();
                    }
                }
            }
            if (primaryColumn == null) {
                primaryColumn = "";
            }
        }
        return primaryColumn;
    }

    public String getPrimaryKey() {
        if (primaryKey == null) {
            String column = getPrimaryColumn();
            if (column != null) {
                primaryKey = Utililies.columnToFeild(column);
            }
        }
        return primaryKey;
    }

    public String getPrimaryJdbcType() {
        if (primaryKeyJdbcType == null) {
            DbTableColumn dc = getPrimaryKeyColumn();
            if (dc != null) {
                if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                    MysqlDbColumn mdc = (MysqlDbColumn) dc;
                    primaryKeyJdbcType = mdc.getDataType();
                }
            }
        }
        return primaryKeyJdbcType == null ? "varchar" : primaryKeyJdbcType;
    }

    public String getPrimaryJavaType() {
        if (primaryKeyJaveType == null) {
            DbTableColumn dc = getPrimaryKeyColumn();
            if (dc != null) {
                if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                    MysqlDbColumn mdc = (MysqlDbColumn) dc;
                    primaryKeyJaveType = Utililies.getJavaType(mdc.getDataType());
                }
            }
        }
        return primaryKeyJaveType == null ? "varchar" : primaryKeyJaveType;
    }

    public String getFeildMapList() {
        StringBuffer feildMapList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                //<id column="{PrimaryColumn}" jdbcType="{PrimaryJdbcType}" property="{PrimaryFeild}" />
                String maps = "<result column=\"{0}\" jdbcType=\"{1}\" property=\"{2}\" />";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (!mdc.isPrimaryKey()) {
                        feildMapList.append(Constants.TAB2)
                                .append(CommonUtils.format(maps, mdc.getColumnName(), mdc.getDataType(), Utililies.columnToFeild(mdc.getColumnName())))
                                .append(Constants.ENTER);
                    }
                }
            }
        }
        return feildMapList.toString();
    }

    public String getFeildJoinId() {
        StringBuffer feildJoinId = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    feildJoinId.append(mdc.getColumnName());
                    if (i < (k - 1)) {
                        feildJoinId.append(", ");
                    }
                }
                feildJoinId.append(Constants.ENTER);
            }
        }
        return feildJoinId.toString();
    }

    public String getFeildIfList() {
        StringBuffer feildIfList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*<if test="groupId != null" >
                    and group_id = #{groupId,jdbcType=BIGINT}
                </if>*/
                StringBuffer feild = new StringBuffer(Constants.TAB2);
                feild.append("<if test=\"{0} != null\" >\r\n")
                        .append(Constants.TAB3).append("and {1} = #{{2},jdbcType={3}}\r\n")
                        .append(Constants.TAB2).append("</if>\r\n");
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    feildIfList.append(
                            CommonUtils.format(
                                    feild.toString(),
                                    Utililies.columnToFeild(mdc.getColumnName()),
                                    mdc.getColumnName(),
                                    Utililies.columnToFeild(mdc.getColumnName()),
                                    mdc.getDataType()
                            )
                    );
                }
            }
        }
        return feildIfList.toString();
    }

    public String getFeildJoin() {
        StringBuffer feildJoin = new StringBuffer(Constants.TAB3);
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        if (mdc.getExtra() == null || !mdc.getExtra().equalsIgnoreCase("auto_increment")) {
                            feildJoin.append(mdc.getColumnName()).append(", ");
                        }
                    } else {
                        feildJoin.append(mdc.getColumnName()).append(", ");
                    }
                }
                feildJoin.append(Constants.ENTER);
            }
        }
        return feildJoin.toString().replaceFirst(", $", "");
    }

    public String getFeildMapJoin() {
        StringBuffer feildMapJoin = new StringBuffer(Constants.TAB3);
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*#{rowId,jdbcType=BIGINT}*/
                String map = "#{{0},jdbcType={1}}";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        if (mdc.getExtra() == null || !mdc.getExtra().equalsIgnoreCase("auto_increment")) {
                            feildMapJoin.append(
                                    CommonUtils.format(map, Utililies.columnToFeild(mdc.getColumnName()), mdc.getDataType())
                            ).append(", ");
                        }
                    } else {
                        feildMapJoin.append(
                                CommonUtils.format(map, Utililies.columnToFeild(mdc.getColumnName()), mdc.getDataType())
                        ).append(", ");
                    }
                }
            }
        }
        return feildMapJoin.toString().replaceFirst(", $", "");
    }

    public String getFeildIfJoin() {
        StringBuffer feildIfList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*<if test="appSysName != null">
                    APP_SYS_NAME,
                  </if>*/
                StringBuffer feild = new StringBuffer(Constants.TAB2);
                feild.append("<if test=\"{0} != null\">\r\n")
                        .append(Constants.TAB3).append("{1},\r\n")
                        .append(Constants.TAB2).append("</if>\r\n");
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        if (mdc.getExtra() == null || !mdc.getExtra().equalsIgnoreCase("auto_increment")) {
                            feildIfList.append(Constants.TAB3)
                                    .append(Utililies.columnToFeild(mdc.getColumnName()))
                                    .append(mdc.getColumnName())
                                    .append(",\r\n");
                        }
                    } else {
                        feildIfList.append(
                                CommonUtils.format(
                                        feild.toString(),
                                        Utililies.columnToFeild(mdc.getColumnName()),
                                        mdc.getColumnName()
                                )
                        );
                    }
                }
            }
        }
        return feildIfList.toString();
    }

    public String getFeildIfMapJoin() {
        StringBuffer feildIfMapJoin = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*<if test="appSysCode != null">
                    #{appSysCode,jdbcType=VARCHAR},
                  </if>*/
                StringBuffer feild = new StringBuffer(Constants.TAB2);
                feild.append("<if test=\"{0} != null\">\r\n")
                        .append(Constants.TAB3).append("#{{0},jdbcType={1}},\r\n")
                        .append(Constants.TAB2).append("</if>\r\n");
                String pmap = "#{{0},jdbcType={1}},\r\n";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        if (mdc.getExtra() == null || !mdc.getExtra().equalsIgnoreCase("auto_increment")) {
                            feildIfMapJoin.append(CommonUtils.format(
                                    pmap,
                                    Utililies.columnToFeild(mdc.getColumnName()),
                                    mdc.getDataType()
                            ));
                        }
                    } else {
                        feildIfMapJoin.append(
                                CommonUtils.format(
                                        feild.toString(),
                                        Utililies.columnToFeild(mdc.getColumnName()),
                                        mdc.getDataType()
                                )
                        );
                    }
                }
            }
        }
        return feildIfMapJoin.toString();
    }

    public String getFeildIfSetList() {
        StringBuffer feildIfSetList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*<if test="appSysCode != null">
                    APP_SYS_CODE = #{appSysCode,jdbcType=VARCHAR},
                  </if>*/
                StringBuffer feild = new StringBuffer(Constants.TAB2);
                feild.append("<if test=\"{0} != null\">\r\n")
                        .append(Constants.TAB3).append("{1} = #{{0},jdbcType={2}},\r\n")
                        .append(Constants.TAB2).append("</if>\r\n");
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (!mdc.isPrimaryKey()) {
                        feildIfSetList.append(
                                CommonUtils.format(
                                        feild.toString(),
                                        Utililies.columnToFeild(mdc.getColumnName()),
                                        mdc.getColumnName(),
                                        mdc.getDataType()
                                )
                        );
                    }
                }
            }
        }
        return feildIfSetList.toString();
    }

    public String getFeildSetList() {
        StringBuffer feildSetList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*APP_SYS_NAME = #{appSysName,jdbcType=VARCHAR},*/
                String map = "{0} = #{{1},jdbcType={2}}, \r\n";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (!mdc.isPrimaryKey()) {
                        feildSetList.append(Constants.TAB2).append(
                                CommonUtils.format(
                                        map,
                                        mdc.getColumnName(),
                                        Utililies.columnToFeild(mdc.getColumnName()),
                                        mdc.getDataType()
                                )
                        );
                    }
                }
            }
        }
        return feildSetList.toString().replaceFirst(", $", "");
    }

    public String getForeachFeildMapJoin() {
        StringBuffer feildSetList = new StringBuffer();
        if (columnList != null) {
            if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
                MysqlDbColumn mdc = null;
                /*#{item.rowId,jdbcType=BIGINT}, */
                String map = "#{item.{0},jdbcType={1}}, \r\n";
                for (int i = 0, k = columnList.size(); i < k; i++) {
                    mdc = (MysqlDbColumn) columnList.get(i);
                    if (mdc.isPrimaryKey()) {
                        if (mdc.getExtra() == null || !mdc.getExtra().equalsIgnoreCase("auto_increment")) {
                            feildSetList.append(Constants.TAB3).append(
                                    CommonUtils.format(
                                            map,
                                            Utililies.columnToFeild(mdc.getColumnName()),
                                            mdc.getDataType()
                                    )
                            );
                        }
                    } else {
                        feildSetList.append(Constants.TAB2).append(
                                CommonUtils.format(
                                        map,
                                        Utililies.columnToFeild(mdc.getColumnName()),
                                        mdc.getDataType()
                                )
                        );
                    }
                }
            }
        }
        return feildSetList.toString().replaceFirst(", $", "");
    }

}
