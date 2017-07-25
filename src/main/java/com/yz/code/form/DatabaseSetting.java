package com.yz.code.form;

import com.yz.code.util.IniReader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSetting extends BasePanel {

    private int width = 900;
    private int height = 600;
    private JComboBox<Object> connNameCombo = null;
    private MySqlSetPanel mySqlSetPanel = null;
    private OracleSetPanel oracleSetPanel = null;
    private SqlServerSetPanel sqlServerSetPanel = null;
    private JTabbedPane tabPane = null;
    private boolean comboInited = false;

    public DatabaseSetting(int width, int height) {
        this.width = width;
        this.height = height;
        initComponent();
    }

    public void showPanel() {
        this.setVisible(true);
        initConnNameList(MySqlSetPanel.sections, null);
    }

    public String getConnectionName() {
        String result = "未命名";
        Object item = connNameCombo.getSelectedItem();
        if (item != null) {
            result = item.toString().equals("") ? result : item.toString();
        }
        return result;
    }

    public void setConnectionName(String name) {
        try {
            connNameCombo.addItem(name);
            connNameCombo.setSelectedItem(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initComponent() {
        this.setSize(new Dimension(width, height - 30));
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JButton close = new JButton("X");
        close.setBounds(width - 45, 2, 30, 20);
        this.add(close);
        close.addActionListener(new ComActionListener(this));

        JLabel connNameLabel = new JLabel("连接名称：");
        connNameLabel.setBounds(20, 6, 150, 25);

        connNameCombo = new JComboBox<Object>(new Object[]{});
        connNameCombo.setBounds(160, 6, 500, 25);
        connNameCombo.setEditable(true);
        // -------------

        tabPane = new JTabbedPane();

        mySqlSetPanel = new MySqlSetPanel(width, height - 35 - 40);
        oracleSetPanel = new OracleSetPanel(width, height - 35 - 40);
        sqlServerSetPanel = new SqlServerSetPanel(width, height - 35 - 40);

        tabPane.addTab("MySQL", null, mySqlSetPanel, "MySQL 数据库配置信息");
        tabPane.addTab("Oracle", null, oracleSetPanel, "Oracle 数据库配置信息");
        tabPane.addTab("SQLServer", null, sqlServerSetPanel, "SQLServer 数据库配置信息");

        tabPane.setBounds(10, 35, width, height - 35);

        tabPane.addChangeListener(new TabChangeListener(this));

        this.add(connNameLabel);
        this.add(connNameCombo);
        this.add(tabPane);

        connNameCombo.addItemListener(new ComboItemListener(this));

    }

    public void initConnNameList(String section, String secname) {
        try {
            comboInited = false;
            connNameCombo.removeAllItems();
            IniReader reader = IniReader.getIniReader();
            HashMap<String, HashMap<String, String>> map = reader.getConfig(section);
            if (map != null && map.entrySet().size() > 0) {
                String name = "", isSelected = null;
                int index = -1, i = 0;
                HashMap<String, String> cfg = null;
                for (Map.Entry<String, HashMap<String, String>> ent : map.entrySet()) {
                    name = ent.getKey();
                    if (name != null) {
                        cfg = ent.getValue();
                        if (secname != null && secname.equals(name)) {
                            index = i;
                            setConfigValue(cfg);
                        } else if (secname == null) {
                            isSelected = cfg.get("isSelected");
                            if (isSelected != null && isSelected.equals("true")) {
                                index = i;
                                setConfigValue(cfg);
                            }
                        }
                        connNameCombo.addItem(name);
                        i++;
                    }
                }
                if (index == -1) {
                    setConfigValue(null);
                }
                if (map.entrySet().size() > 0 && index > -1) {
                    connNameCombo.setSelectedIndex(index);
                    comboInited = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConfigValue(HashMap<String, String> cfg) {
        int index = tabPane.getSelectedIndex();
        if (index == 0) { // mysql
            mySqlSetPanel.setConfigValue(cfg);
        } else if (index == 1) { // Oracle
            oracleSetPanel.setConfigValue(cfg);
        } else if (index == 2) { // SQLServer
            sqlServerSetPanel.setConfigValue(cfg);
        }
    }

    private class TabChangeListener implements ChangeListener {
        private DatabaseSetting dbset = null;

        public TabChangeListener(DatabaseSetting dbset) {
            this.dbset = dbset;
        }

        public void stateChanged(ChangeEvent e) {
            JTabbedPane tab = (JTabbedPane) e.getSource();
            int index = tab.getSelectedIndex();
            if (index == 0) { // mysql
                dbset.initConnNameList(MySqlSetPanel.sections, null);
            } else if (index == 1) { // Oracle
                dbset.initConnNameList(OracleSetPanel.sections, null);
            } else if (index == 2) { // SQLServer
                dbset.initConnNameList(SqlServerSetPanel.sections, null);
            }
        }
    }

    private class ComActionListener implements ActionListener {
        private DatabaseSetting dbset = null;

        public ComActionListener(DatabaseSetting dbset) {
            this.dbset = dbset;
        }

        public void actionPerformed(ActionEvent e) {
            dbset.setVisible(false);
        }
    }

    private class ComboItemListener implements ItemListener {
        private DatabaseSetting dbset = null;

        public ComboItemListener(DatabaseSetting dbset) {
            this.dbset = dbset;
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && comboInited) {
                String secname = e.getItem().toString();
                System.out.println(secname);
                int index = tabPane.getSelectedIndex();
                if (index == 0) { // mysql
                    dbset.initConnNameList(MySqlSetPanel.sections, secname);
                } else if (index == 1) { // Oracle
                    dbset.initConnNameList(OracleSetPanel.sections, secname);
                } else if (index == 2) { // SQLServer
                    dbset.initConnNameList(SqlServerSetPanel.sections, secname);
                }

            }
        }
    }

}
