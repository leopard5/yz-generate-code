package com.yz.code.form;

import com.yz.code.db.DBHelper;
import com.yz.code.db.DbCache;
import com.yz.code.entity.CreAttr;
import com.yz.code.entity.DbTable;
import com.yz.code.entity.DbTableColumn;
import com.yz.code.entity.MysqlDbColumn;
import com.yz.code.main.CreateThread;
import com.yz.code.util.CommonUtils;
import com.yz.code.util.IniReader;
import com.yz.code.util.Utililies;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class MainForm extends BasePanel {

    private int width = 900;
    private int height = 600;
    private boolean configInited = false;
    private boolean loadding = false;
    private String config_section = "create_config_info";

    private static JTree tree = null;
    private DefaultMutableTreeNode dbNode;
    private JScrollPane jScrollPane1;
    public static Map<String, Vector<Object>> columnMap = new HashMap<String, Vector<Object>>();

    public MainForm() {
        this.setBackground(Color.white);
        this.setLayout(null);
        initTreeThread(1);
        initCompant();
    }

    public MainForm(int width, int height) {
        this.width = width;
        this.height = height;
        this.setBackground(Color.white);
        this.setLayout(null);
        initTreeThread(1);
        initCompant();
    }

    public List<DbTable> getDbTable() {
        DBHelper.initDbConfig();
        List<DbTable> list = DBHelper.getDbTables(DBHelper.schema);
        return list;
    }

    public List<?> getTableColumn(String table) {
        DBHelper.initDbConfig();
        if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
            return DBHelper.getTableColumns(DBHelper.schema, table);
        }
        return null;
    }

    public Map<String, List<DbTableColumn>> getTableColumn() {
        DBHelper.initDbConfig();
        if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
            return DBHelper.getMutilTableColumns(DBHelper.schema);
        }
        return null;
    }

    private JTextField entityNameText;
    private JTextField entPackageText;
    private JTextField daoPackageText;
    private JTextField serPackageText;
    private JTextField mapPackageText;
    private JTextField hbmPackageText;
    private JTextField cotPackageText;
    private JTextField actPackageText;
    private JComboBox<Object> dbFrameCombo;
    private JComboBox<Object> cFrameCombo;
    private JCheckBox daoBox;
    private JCheckBox baseDaoBox;
    private JCheckBox serviceBox;
    private JCheckBox baseServiceBox;
    private JCheckBox mapBox;
    private JCheckBox hbmBox;
    private JCheckBox actionBox;
    private JCheckBox controllerBox;
    private JCheckBox baseControllerBox;
    private JLabel perview1;
    private JLabel perview2;

    private JButton seekTableDataBtn;
    private JButton singleCreateBtn;
    private JButton mutilCreateBtn;
    private JButton chooseDirBtn;

    private JComboBox<Object> cigCombo;
    private JButton saveBtn;
    private JButton newSaveBtn;
    private String chooseTable = null;

    JFileChooser jfc = new JFileChooser();// 文件选择器
    private JTextField saveDirText;
    public static List<DbTable> tableDatas = new ArrayList<DbTable>();

    public void initCompant() {
        int left = 300, labelWidth = 55, labelTextHeight = 25, top = 60, textWidth = 150;

        // 按钮层
        seekTableDataBtn = new JButton("查看表数据");
        seekTableDataBtn.setBounds(left, 5, textWidth, labelTextHeight);
        add(seekTableDataBtn);
        seekTableDataBtn.setEnabled(false);

        mutilCreateBtn = new JButton("生成全部表");
        mutilCreateBtn.setBounds(left + textWidth + 10, 5, textWidth, labelTextHeight);
        add(mutilCreateBtn);

        singleCreateBtn = new JButton("生成选择的表");
        singleCreateBtn.setBounds(left + (textWidth + 10) * 2, 5, textWidth, labelTextHeight);
        add(singleCreateBtn);
        singleCreateBtn.setEnabled(false);

        //
        JLabel cigLabel = new JLabel("配置方案");
        cigLabel.setBounds(left + labelWidth, labelTextHeight + 20, labelWidth, labelTextHeight);
        add(cigLabel);
        cigCombo = new JComboBox<Object>();
        cigCombo.setName("cigCombo");
        cigCombo.setBounds(left + (labelWidth + 10) * 2, labelTextHeight + 20, textWidth, labelTextHeight);
        add(cigCombo);

        saveBtn = new JButton("保存");
        saveBtn.setBounds(left + (labelWidth + 10) * 2 + textWidth + 50, labelTextHeight + 20, textWidth - 60,
                labelTextHeight);
        add(saveBtn);

        newSaveBtn = new JButton("另存为");
        newSaveBtn.setBounds(left + (labelWidth + 10) * 2 + textWidth * 2, labelTextHeight + 20, textWidth - 60,
                labelTextHeight);
        add(newSaveBtn);

        // 持久层 111111
        JLabel dbFrameLabel = new JLabel("持久层");
        dbFrameLabel.setBounds(left, top + ((labelTextHeight + 10) * 1), labelWidth, labelTextHeight);
        add(dbFrameLabel);
        dbFrameCombo = new JComboBox<Object>(new Object[]{"Mybatis", "Hibernate"});
        dbFrameCombo.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 1), textWidth, labelTextHeight);
        add(dbFrameCombo);

        // 实体类 2222
        JLabel entityNameLabel = new JLabel("实体类");
        entityNameLabel.setBounds(left, top + ((labelTextHeight + 10) * 2), labelWidth, labelTextHeight);
        add(entityNameLabel);
        entityNameText = new JTextField("");
        entityNameText.setName("entiry");
        entityNameText.setEditable(false);
        entityNameText.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 2), textWidth, labelTextHeight);
        add(entityNameText);

        JLabel packageLabel = new JLabel("包路径");
        packageLabel.setBounds(left + labelWidth + textWidth + 10, top + ((labelTextHeight + 10) * 2), labelWidth,
                labelTextHeight);
        add(packageLabel);
        entPackageText = new JTextField("");
        entPackageText.setName("entityPackage");
        entPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 2),
                textWidth * 2, labelTextHeight);
        add(entPackageText);

        // Dao类 3333
        daoBox = new JCheckBox("生成Dao接口/类");
        daoBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 3), textWidth, labelTextHeight);
        add(daoBox);
        daoBox.setSelected(true);

        JLabel packageLabel2 = new JLabel("包路径");
        packageLabel2.setBounds(left + labelWidth + textWidth + 10, top + ((labelTextHeight + 10) * 3), labelWidth,
                labelTextHeight);
        add(packageLabel2);
        daoPackageText = new JTextField("");
        daoPackageText.setName("daoPackage");
        daoPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 3),
                textWidth * 2, labelTextHeight);
        add(daoPackageText);

        // ********************************
        baseDaoBox = new JCheckBox("生成BaseDao接口/类");
        baseDaoBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 4), textWidth, labelTextHeight);
        add(baseDaoBox);

        //////////////////
        mapBox = new JCheckBox("生成Mapper.xml");
        mapBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 5), textWidth, labelTextHeight);
        add(mapBox);
        mapBox.setSelected(true);

        hbmBox = new JCheckBox("生成Hbm.xml");
        hbmBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 5), textWidth, labelTextHeight);
        add(hbmBox);
        hbmBox.setVisible(false);

        JLabel packageLabel3 = new JLabel("包路径");
        packageLabel3.setBounds(left + labelWidth + textWidth + 10, top + ((labelTextHeight + 10) * 5), labelWidth,
                labelTextHeight);
        add(packageLabel3);
        mapPackageText = new JTextField("");
        mapPackageText.setName("mapPackage");
        mapPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 5),
                textWidth * 2, labelTextHeight);
        add(mapPackageText);
        hbmPackageText = new JTextField("");
        hbmPackageText.setName("hbmPackage");
        hbmPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 5),
                textWidth * 2, labelTextHeight);
        add(hbmPackageText);
        hbmPackageText.setVisible(false);

        ///////////
        serviceBox = new JCheckBox("生成Service接口/类");
        serviceBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 6), textWidth, labelTextHeight);
        add(serviceBox);
        serviceBox.setSelected(true);

        JLabel packageLabel5 = new JLabel("包路径");
        packageLabel5.setBounds(left + labelWidth + textWidth + 10, top + ((labelTextHeight + 10) * 6), labelWidth,
                labelTextHeight);
        add(packageLabel5);
        serPackageText = new JTextField("");
        serPackageText.setName("serPackage");
        serPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 6),
                textWidth * 2, labelTextHeight);
        add(serPackageText);

        ///***************
        baseServiceBox = new JCheckBox("生成BaseService接口/类");
        baseServiceBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 7), textWidth * 2, labelTextHeight);
        add(baseServiceBox);

        perview1 = new JLabel("");
        perview1.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 2), textWidth * 3, labelTextHeight);
        perview1.setForeground(Color.BLUE);
        add(perview1);

        //////////////////
        JLabel cFrameLabel = new JLabel("控制层");
        cFrameLabel.setBounds(left, top + ((labelTextHeight + 10) * 8), labelWidth, labelTextHeight);
        add(cFrameLabel);
        cFrameCombo = new JComboBox<Object>(new Object[]{"SpringMVC", "Struts2"});
        cFrameCombo.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 8), textWidth, labelTextHeight);
        add(cFrameCombo);

        // action
        controllerBox = new JCheckBox("生成Controller类");
        controllerBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 9), textWidth, labelTextHeight);
        add(controllerBox);
        controllerBox.setSelected(true);

        actionBox = new JCheckBox("生成Action类");
        actionBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 9), textWidth, labelTextHeight);
        add(actionBox);
        actionBox.setVisible(false);

        JLabel packageLabel7 = new JLabel("包路径");
        packageLabel7.setBounds(left + labelWidth + textWidth + 10, top + ((labelTextHeight + 10) * 9), labelWidth,
                labelTextHeight);
        add(packageLabel7);
        cotPackageText = new JTextField("");
        cotPackageText.setName("cotPackage");
        cotPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 9),
                textWidth * 2, labelTextHeight);
        add(cotPackageText);
        actPackageText = new JTextField("");
        actPackageText.setName("actPackage");
        actPackageText.setBounds(left + labelWidth * 2 + textWidth + 10, top + ((labelTextHeight + 10) * 9),
                textWidth * 2, labelTextHeight);
        add(actPackageText);
        actPackageText.setVisible(false);

        ///***************
        baseControllerBox = new JCheckBox("生成BaseController类");
        baseControllerBox.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 10), textWidth * 2, labelTextHeight);
        add(baseControllerBox);

        perview2 = new JLabel("保存位置");
        perview2.setBounds(left, top + ((labelTextHeight + 10) * 11), labelWidth, labelTextHeight);
        add(perview2);

        saveDirText = new JTextField("d:/");
        saveDirText.setName("saveDirText");
        saveDirText.setBounds(left + labelWidth, top + ((labelTextHeight + 10) * 11), textWidth * 3 - 50, labelTextHeight);
        saveDirText.setEditable(false);
        add(saveDirText);

        chooseDirBtn = new JButton("选择位置");
        chooseDirBtn.setBounds(left + labelWidth + textWidth * 3 - 40, top + ((labelTextHeight + 10) * 11), textWidth - 50, labelTextHeight);
        add(chooseDirBtn);
        chooseDirBtn.addActionListener(new BtnActionListener());

        dbFrameCombo.addItemListener(new ComboItemListener());
        cFrameCombo.addItemListener(new ComboItemListener());
        cigCombo.addItemListener(new ComboItemListener());

        entPackageText.addKeyListener(new InputKeyListener());
        daoPackageText.addKeyListener(new InputKeyListener());
        serPackageText.addKeyListener(new InputKeyListener());
        mapPackageText.addKeyListener(new InputKeyListener());
        actPackageText.addKeyListener(new InputKeyListener());

        daoBox.addActionListener(new BoxActionListener());
        serviceBox.addActionListener(new BoxActionListener());
        mapBox.addActionListener(new BoxActionListener());
        hbmBox.addActionListener(new BoxActionListener());
        controllerBox.addActionListener(new BoxActionListener());
        actionBox.addActionListener(new BoxActionListener());

        seekTableDataBtn.addActionListener(new BtnActionListener());
        singleCreateBtn.addActionListener(new BtnActionListener());
        mutilCreateBtn.addActionListener(new BtnActionListener());
        saveBtn.addActionListener(new BtnActionListener());
        newSaveBtn.addActionListener(new BtnActionListener());

        configInited = false;
        initCombo();
    }

    public void initCombo() {
        IniReader reader = IniReader.getIniReader();
        HashMap<String, HashMap<String, String>> configMap = reader.getConfig(config_section);
        if (configMap != null) {
            cigCombo.removeAllItems();
            String ise = null;
            HashMap<String, String> cfg = null;
            int index = 0, currIndex = 0;
            for (Map.Entry<String, HashMap<String, String>> entity : configMap.entrySet()) {
                cigCombo.addItem(entity.getKey());
                cfg = entity.getValue();
                if (cfg != null) {
                    ise = cfg.get("isSelected");
                    if (ise != null && ise.equals("true")) {
                        currIndex = index;
                    }
                }
                index++;
            }
            configInited = true;
            if (cigCombo.getItemCount() > currIndex) {
                cigCombo.setSelectedIndex(currIndex);
            }
            if (cigCombo.getItemCount() == 0) {
                cigCombo.addItem("Config 1");
                cigCombo.setSelectedIndex(0);
            }
            if (currIndex == 0) {
                changeConfig(cigCombo.getItemAt(0).toString());
            }
        } else {
            if (cigCombo.getItemCount() == 0) {
                cigCombo.addItem("Config 1");
                cigCombo.setSelectedIndex(0);
            }
        }
    }

    public void initValue(HashMap<String, String> viewCig) {
        if (viewCig != null) {
            try {
                if (viewCig.get("dao_frame").equals("Mybatis")) {
                    dbFrameCombo.setSelectedIndex(0);
                } else {
                    dbFrameCombo.setSelectedIndex(1);
                }
                if (viewCig.get("controll_frame").equals("SpringMVC")) {
                    cFrameCombo.setSelectedIndex(0);
                } else {
                    cFrameCombo.setSelectedIndex(1);
                }
                daoPackageText.setText(viewCig.get("dao_package"));
                serPackageText.setText(viewCig.get("service_package"));
                entPackageText.setText(viewCig.get("entity_package"));
                mapPackageText.setText(viewCig.get("map_package"));
                hbmPackageText.setText(viewCig.get("hbm_package"));
                cotPackageText.setText(viewCig.get("controller_package"));
                actPackageText.setText(viewCig.get("action_package"));
                saveDirText.setText(viewCig.get("save_dir"));
            } catch (Exception e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "操作异常：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveConfig(int flag) {
        try {
            IniReader reader = IniReader.getIniReader();
            String tag = "";
            int index = cigCombo.getSelectedIndex();
            if (flag == 2) { // 另存为
                index = cigCombo.getItemCount() + 1;
                tag = "Config " + index;
                cigCombo.addItem(tag);
                configInited = false;
                cigCombo.setSelectedIndex(index - 1);
                configInited = true;
            } else {
                tag = cigCombo.getSelectedItem().toString();
            }
            reader.putValue(config_section, tag, "isSelected", "true");
            reader.putValue(config_section, tag, "if_create_dao", daoBox.isSelected() ? "true" : "false");
            reader.putValue(config_section, tag, "if_create_map", mapBox.isSelected() ? "true" : "false");
            reader.putValue(config_section, tag, "if_create_hbm", hbmBox.isSelected() ? "true" : "false");
            reader.putValue(config_section, tag, "if_create_service", serviceBox.isSelected() ? "true" : "false");
            reader.putValue(config_section, tag, "if_create_controller", controllerBox.isSelected() ? "true" : "false");
            reader.putValue(config_section, tag, "if_create_action", actionBox.isSelected() ? "true" : "false");

            reader.putValue(config_section, tag, "service_package", serPackageText.getText());
            reader.putValue(config_section, tag, "entity_package", entPackageText.getText());
            reader.putValue(config_section, tag, "dao_package", daoPackageText.getText());
            reader.putValue(config_section, tag, "map_package", mapPackageText.getText());
            reader.putValue(config_section, tag, "hbm_package", hbmPackageText.getText());
            reader.putValue(config_section, tag, "controller_package", cotPackageText.getText());
            reader.putValue(config_section, tag, "action_package", actPackageText.getText());
            reader.putValue(config_section, tag, "save_dir", saveDirText.getText());

            reader.putValue(config_section, tag, "dao_frame", dbFrameCombo.getSelectedItem().toString());
            reader.putValue(config_section, tag, "controll_frame", cFrameCombo.getSelectedItem().toString());
            reader.save();
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "操作异常：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void initTreeThread(int flag) {
        if (loadding) {
            /*int count = dbNode.getChildCount();
            if(count > 0){
	        }*/
            return;
        }
        new Thread() {
            public void run() {
                loadding = true;
                MainWindow.mainWindow.showProcessPanel();
                initTree();
                MainWindow.mainWindow.hideProcessPanel();
                loadding = false;
            }
        }.start();
    }

    public void initTree() {

        // 创建没有父节点和子节点、但允许有子节点的树节点，并使用指定的用户对象对它进行初始化。
        // public DefaultMutableTreeNode(Object TreeObject)
        if (jScrollPane1 != null) {
            jScrollPane1.removeAll();
            this.remove(jScrollPane1);
        }

        tableDatas = getDbTable();
        int tableSize = -1;
        if (tableDatas != null) {
            tableSize = tableDatas.size();
        }

        dbNode = new DefaultMutableTreeNode(new User(DBHelper.schema));

        DbTable table = null;
        DefaultMutableTreeNode node1 = null;
        for (int i = 0; i < tableSize; i++) {
            table = tableDatas.get(i);
            node1 = new DefaultMutableTreeNode(new User(table.getTableName()));
            dbNode.add(node1);
        }

        tree = new JTree(dbNode);

        jScrollPane1 = new JScrollPane();
        jScrollPane1.getViewport().add(tree);
        jScrollPane1.setBounds(5, 5, 290, height - 45);
        this.add(jScrollPane1);

        // 添加选择事件
        tree.addTreeSelectionListener(new JTreeSelectionListener());

        if (tableSize > 0) {
            new Thread(new LoadColumnThread()).start();
        } else if (tableSize == -1) {
            new Thread(new AlertThread()).start();
        }
    }

    class JTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null)
                return;
            int count = node.getChildCount();
            Object object = node.getUserObject();
            if (object != null) { // node.isLeaf() //node.isRoot()
                User User = (User) object;
                if (User != null) {
                    String table = User.toString();
                    int level = node.getLevel();
                    if (level == 0) { // 数据库层
                        chooseTable = null;
                        seekTableDataBtn.setEnabled(false);
                        singleCreateBtn.setEnabled(false);
                        entityNameText.setEditable(false);
                        entityNameText.setText("");
                        setCompTooltip();
                        if (count == 0) {
                            initTreeThread(1);
                        }
                    } else if (level == 1) { // 表层
                        int selnum = tree.getSelectionCount();
                        chooseTable = table;
                        seekTableDataBtn.setEnabled(selnum == 1);
                        singleCreateBtn.setEnabled(true);
                        entityNameText.setText(Utililies.tableToEntity(table));
                        setCompTooltip();
                        if (count == 0) {
                            initTableFeild(node, table);
                        }
                    } else if (level == 2) { // 字段层
                        int selnum = tree.getSelectionCount();
                        chooseTable = table;
                        seekTableDataBtn.setEnabled(selnum == 1);
                        singleCreateBtn.setEnabled(true);
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        entityNameText.setText(Utililies.tableToEntity(parent.getUserObject().toString()));
                    }
                }
            }
        }
    }

    public void renderTableFeild(DefaultMutableTreeNode top, String table, List<?> columns) {
        if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
            if (columns != null) {
                MysqlDbColumn colum = null;
                DefaultMutableTreeNode node1 = null;
                String title = "";
                Vector<Object> vector = new Vector<Object>();
                for (int i = 0, k = columns.size(); i < k; i++) {
                    colum = (MysqlDbColumn) columns.get(i);
                    title = colum.getColumnName() + " - " + colum.getColumnType();
                    node1 = new DefaultMutableTreeNode(new User(title));
                    top.add(node1);
                    vector.add(colum.getColumnName());
                }
                if (!columnMap.containsKey(table)) {
                    columnMap.put(table, vector);
                }
            }
        }
    }

    public void initTableFeild(DefaultMutableTreeNode top, String table) {
        if (DBHelper.sections.equals(MySqlSetPanel.sections)) {
            List<MysqlDbColumn> columns = (List<MysqlDbColumn>) getTableColumn(table);
            if (columns != null) {
                MysqlDbColumn colum = null;
                DefaultMutableTreeNode node1 = null;
                String title = "";
                Vector<Object> vector = new Vector<Object>();
                // String[] column = new String[columns.size()];
                for (int i = 0, k = columns.size(); i < k; i++) {
                    colum = columns.get(i);
                    title = colum.getColumnName() + " - " + colum.getColumnType();
                    node1 = new DefaultMutableTreeNode(new User(title));
                    top.add(node1);
                    // column[i] = colum.getColumnName();
                    vector.add(colum.getColumnName());
                }
                if (!columnMap.containsKey(table)) {
                    columnMap.put(table, vector);
                }
            }
        }
    }

    class LoadColumnThread implements Runnable {

        public void run() {
            Map<String, List<DbTableColumn>> tabelMap = getTableColumn();
            if (tabelMap != null) {
                DbCache.mysqlDbColumnMap = tabelMap;
                List<?> comulnList = null;
                int size = dbNode.getChildCount();
                DefaultMutableTreeNode node;
                TreePath tpath = null;
                User user = null;
                for (int i = 0; i < size; i++) {
                    node = (DefaultMutableTreeNode) dbNode.getChildAt(i);
                    user = (User) node.getUserObject();
                    if (user != null) {
                        comulnList = tabelMap.get(user.toString());
                        if (comulnList != null) {
                            renderTableFeild(node, user.toString(), comulnList);
                        }
                    }
                    tpath = new TreePath(node);
                    tree.expandPath(tpath);
                    tree.collapsePath(tpath);
                }
            }
            JScrollBar sBar = jScrollPane1.getVerticalScrollBar(); // 得到JScrollPane中的JScrollBar
            sBar.setValue(sBar.getMaximum()); // 设置JScrollBar的位置到最后
            sBar.setValue(sBar.getMinimum()); // 设置JScrollBar的位置到最前
        }

    }

    public void setCompTooltip() {
        changeTips("entityPackage", entPackageText.getText());
        changeTips("daoPackage", daoPackageText.getText());
        changeTips("mapPackage", mapPackageText.getText());
        changeTips("serPackage", serPackageText.getText());
        changeTips("actPackage", actPackageText.getText());
    }

    public void changeTips(String name, String value) {
        String entity = entityNameText.getText();
        if (entity == null || entity.length() < 1) {
            entity = "[实体类名称]";
        }
        if (name.equals("entityPackage")) {
            entityNameText.setToolTipText(value + "." + entity + ".java");
        } else if (name.equals("daoPackage")) {
            daoBox.setToolTipText(value + "." + entity + "Mapper.java");
        } else if (name.equals("mapPackage")) {
            if (mapBox.isSelected()) {
                mapBox.setToolTipText(Utililies.packageToDir(value) + "/" + entity + "Mapper.xml");
            } else if (hbmBox.isSelected()) {
                hbmBox.setToolTipText(Utililies.packageToDir(value) + "/" + entity + ".hbm.xml");
            }
        } else if (name.equals("serPackage")) {
            serviceBox.setToolTipText(value + "." + entity + "Service.java");
        } else if (name.equals("actPackage")) {
            if (actionBox.isSelected()) {
                actionBox.setToolTipText(value + "/" + entity + "Controller.java");
            } else if (controllerBox.isSelected()) {
                controllerBox.setToolTipText(value + "/" + entity + "Controller.java");
            }
        }
    }

    public void changeConfig(String tag) {
        if (configInited) {
            IniReader reader = IniReader.getIniReader();
            reader.replaceActiveCfg(config_section, tag);
            initValue(reader.getConfig(config_section, tag));
        }
    }

    public void startCreate(boolean isall) {
        List<String> tables = null;
        if (isall) {
            tables = getAllTable(dbNode);
        } else {
            tables = getSelectedTable();
        }

        if (tables != null) {
            CreAttr ca = getCreAttr(tables);
            if (ca != null) {
                new CreateThread(tables, ca).start();
            }
        }
    }

    public CreAttr getCreAttr(List<String> tables) {
        CreAttr ca = new CreAttr();
        String entityName = "{EntityName}", maven = "";
        String entPackage = entPackageText.getText();
        if (entPackage.startsWith("main.java.")) {
            maven = "main/java/";
        }
        ca.setEntityName(entityName);
        ca.setEntityPackage(Utililies.getRealPackage(entPackageText.getText()) + "." + entityName);
        ca.setEntityFilePath(Utililies.packageToDir(Utililies.getRealPackage(entPackageText.getText()) + "/") + entityName + ".java");
        if (daoBox.isSelected()) {
            ca.setCreateDao(true);
            ca.setDaoName(entityName + "Mapper");
            ca.setDaoPackage(Utililies.getRealPackage(daoPackageText.getText()) + "." + ca.getDaoName());
            ca.setDaoFilePath(Utililies.packageToDir(Utililies.getRealPackage(daoPackageText.getText()) + "/") + ca.getDaoName() + ".java");
        }
        if (serviceBox.isSelected()) {
            ca.setCreateService(true);
            ca.setServiceName(entityName + "ServiceImpl");
            ca.setIserviceName("I" + entityName + "Service");
            ca.setServicePackage(Utililies.getRealPackage(serPackageText.getText()) + ".impl." + ca.getServiceName());
            ca.setIservicePackage(Utililies.getRealPackage(serPackageText.getText()) + "." + ca.getIserviceName());
            ca.setServiceFilePath(Utililies.packageToDir(Utililies.getRealPackage(serPackageText.getText()) + "/impl/") + ca.getServiceName() + ".java");
            ca.setIserviceFilePath(Utililies.packageToDir(Utililies.getRealPackage(serPackageText.getText()) + "/") + ca.getIserviceName() + ".java");
        }
        if (mapBox.isSelected()) {
            ca.setCreateMapper(true);
            ca.setMapName(entityName + "Mapper");
            ca.setMapPackage(Utililies.getRealPackage(mapPackageText.getText()));
            ca.setMapFilePath(Utililies.packageToDir(Utililies.getRealPackage(mapPackageText.getText()) + "/") + ca.getMapName() + ".xml");
        }
        if (hbmBox.isSelected()) {
            ca.setCreateMapper(true);
            ca.setHbmName(entityName + ".hbm");
            ca.setHbmPackage(Utililies.getRealPackage(hbmPackageText.getText()));
            ca.setHbmFilePath(Utililies.packageToDir(Utililies.getRealPackage(hbmPackageText.getText()) + "/") + ca.getHbmName() + ".xml");
        }
        if (controllerBox.isSelected()) {
            ca.setCreateController(true);
            ca.setControllerName(entityName + "Controller");
            ca.setControllerPackage(Utililies.getRealPackage(cotPackageText.getText()) + "." + ca.getControllerName());
            ca.setControllerFilePath(Utililies.packageToDir(Utililies.getRealPackage(cotPackageText.getText()) + "/") + ca.getControllerName() + ".java");
        }
        if (actionBox.isSelected()) {
            ca.setCreateController(true);
            ca.setActionName(entityName + "Action");
            ca.setActionPackage(Utililies.getRealPackage(actPackageText.getText()) + "." + ca.getActionName());
            ca.setActionFilePath(Utililies.packageToDir(Utililies.getRealPackage(actPackageText.getText()) + "/") + ca.getActionName() + ".java");
        }
        ca.setDaoFrame(dbFrameCombo.getSelectedItem().toString());
        ca.setConFrame(cFrameCombo.getSelectedItem().toString());
        ca.setSaveDir(CommonUtils.isBlank(saveDirText.getText()) ? "d:/code/" : saveDirText.getText() + maven);
        ca.setSaveDir2(CommonUtils.isBlank(saveDirText.getText()) ? "d:/code/" : saveDirText.getText());

        ca.setCreateBaseDao(baseDaoBox.isSelected());
        ca.setCreateBaseService(baseServiceBox.isSelected());
        ca.setCreateBaseController(baseControllerBox.isSelected());

        return ca;
    }

    // 获取所有表
    public List<String> getAllTable(DefaultMutableTreeNode node) {
        List<String> tableList = new ArrayList<String>();
        Enumeration<DefaultMutableTreeNode> children = node.children();
        User user = null;
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = children.nextElement();
            if (!child.isLeaf()) { // 是否叶子节点
                if (child.getLevel() == 1) { // 表
                    user = (User) child.getUserObject();
                    if (user != null) {
                        tableList.add(user.toString());
                    }
                }
            }
        }
        return tableList;
    }

    // 获取所有选中的表
    public List<String> getSelectedTable() {
        List<String> tableList = new ArrayList<String>();
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths != null) {
            DefaultMutableTreeNode node = null;
            User user = null;
            for (int i = 0; i < treePaths.length; i++) {
                node = (DefaultMutableTreeNode) treePaths[i].getLastPathComponent();
                if (node != null) {
                    if (node.getLevel() == 1) { // 表
                        user = (User) node.getUserObject();
                        if (user != null) {
                            tableList.add(user.toString());
                        }
                    }
                }
            }
        }
        return tableList;
    }

    class AlertThread implements Runnable {
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "未检测到有效配置，数据库表加载失败", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ComboItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String fname = e.getItem().toString();
                if (fname.equals("Hibernate")) {
                    mapBox.setSelected(false);
                    mapBox.setVisible(false);
                    mapPackageText.setVisible(false);
                    hbmBox.setSelected(true);
                    hbmBox.setVisible(true);
                    hbmPackageText.setVisible(true);
                } else if (fname.equals("Mybatis")) {
                    hbmBox.setSelected(false);
                    hbmBox.setVisible(false);
                    hbmPackageText.setVisible(false);
                    mapBox.setSelected(true);
                    mapBox.setVisible(true);
                    mapPackageText.setVisible(true);
                } else if (fname.equals("Struts2")) {
                    actionBox.setVisible(true);
                    actionBox.setSelected(true);
                    actPackageText.setVisible(true);
                    controllerBox.setSelected(false);
                    controllerBox.setVisible(false);
                    cotPackageText.setVisible(false);
                } else if (fname.equals("SpringMVC")) {
                    controllerBox.setVisible(true);
                    controllerBox.setSelected(true);
                    cotPackageText.setVisible(true);
                    actionBox.setVisible(false);
                    actionBox.setEnabled(false);
                    actPackageText.setVisible(false);
                } else { // 配置方案更改
                    changeConfig(fname);
                }
            }
        }
    }

    class InputKeyListener implements KeyListener {
        public void keyTyped(KeyEvent e) { // 按下

        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) { // 放开
            JTextField textFeild = (JTextField) e.getComponent();
            String name = textFeild.getName();
            changeTips(name, textFeild.getText());
        }
    }

    class BoxActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JCheckBox box = (JCheckBox) e.getSource();
            String name = box.getText();
            System.out.println(name);
            if (name.equals("生成Dao接口/类")) {
                daoPackageText.setEnabled(box.isSelected());
                daoPackageText.setText("");
            } else if (name.equals("生成Mapper.xml")) {
                mapPackageText.setEnabled(box.isSelected());
                mapPackageText.setText("");
            } else if (name.equals("生成Service接口/类")) {
                serPackageText.setEnabled(box.isSelected());
                serPackageText.setText("");
            } else if (name.equals("生成Hbm.xml")) {
                mapPackageText.setEnabled(box.isSelected());
                mapPackageText.setText("");
            } else if (name.equals("生成Controller类")) {
                actPackageText.setEnabled(box.isSelected());
                actPackageText.setText("");
            } else if (name.equals("生成Action类")) {
                actPackageText.setEnabled(box.isSelected());
                actPackageText.setText("");
            }
        }
    }

    class BtnActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String comm = e.getActionCommand();
            System.out.println(comm);
            if (comm.equals("查看表数据")) {
                MainWindow.mainWindow.showTableDataPanel(chooseTable);
            } else if (comm.equals("生成全部表")) {
                startCreate(true);
            } else if (comm.equals("生成选择的表")) {
                System.out.println("in....");
                startCreate(false);
            } else if (comm.equals("保存")) {
                saveConfig(1);
            } else if (comm.equals("另存为")) {
                saveConfig(2);
            } else if (comm.equals("选择位置")) {
                String dir = saveDirText.getText();
                if (CommonUtils.isBlank(dir)) {
                    jfc.setCurrentDirectory(new File("d://"));// 文件选择器的初始目录定为d盘
                } else {
                    jfc.setCurrentDirectory(new File(dir));
                }
                jfc.setFileSelectionMode(1);// 设定只能选择到文件夹
                int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
                if (state == 1) {
                    return;
                } else {
                    File f = jfc.getSelectedFile();// f为选择到的目录
                    String path = f.getAbsolutePath();
                    saveDirText.setText(path.replaceAll("\\\\", "/") + "/");
                }
            }
        }
    }

    class User {
        private String name;

        public User(String n) {
            name = n;
        }

        public String toString() {
            return name;
        }
    }

}
