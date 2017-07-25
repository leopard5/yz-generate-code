package com.yz.code.form;

import com.yz.code.util.CommonUtils;
import com.yz.code.util.IniReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

@SuppressWarnings("serial")
public class MySqlSetPanel extends BasePanel {
    private String url = "jdbc:mysql://{0}:{1}/{2}";
    private JTextField hostText = null;
    private JTextField userNameText = null;
    private JPasswordField pwdText = null;
    private JTextField portText = null;
    private JTextField dbText = null;
    private JCheckBox useBox = null;
    private JComboBox<Object> codeCombo = null;

    public static String sections = "mysql_conn_info";

    public MySqlSetPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        initComponent();
    }

    public void initComponent() {
        int labelWidth = 150,
                labelTextHeight = 25,
                textWidth = 200;
        JLabel hostLabel = new JLabel("MySql主机地址");
        hostLabel.setBounds(10, 10, labelWidth, labelTextHeight);

        hostText = new JTextField("127.0.0.1");
        hostText.setBounds(labelWidth, 10, textWidth, labelTextHeight);

        //////////////////////
        JLabel userNameLabel = new JLabel("用户名");
        userNameLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 1), labelWidth, labelTextHeight);

        userNameText = new JTextField("root");
        userNameText.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 1), textWidth, labelTextHeight);

        //////////////////////
        JLabel pwdLabel = new JLabel("密码");
        pwdLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 2), labelWidth, labelTextHeight);

        pwdText = new JPasswordField();
        pwdText.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 2), textWidth, labelTextHeight);

        //////////////////////
        JLabel portLabel = new JLabel("端口号：");
        portLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 3), labelWidth, labelTextHeight);

        portText = new JTextField("3306");
        portText.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 3), textWidth, labelTextHeight);

        //////////////////////
        JLabel dbLabel = new JLabel("数据库名称");
        dbLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 4), labelWidth, labelTextHeight);

        dbText = new JTextField();
        dbText.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 4), textWidth, labelTextHeight);

        //////////////////////
        JLabel codeLabel = new JLabel("使用编码");
        codeLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 5), labelWidth, labelTextHeight);

        codeCombo = new JComboBox<Object>(new Object[]{"utf-8", "gbk"});
        codeCombo.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 5), textWidth, labelTextHeight);
        codeCombo.setSelectedIndex(0);

        //////////////////////
        JLabel useLabel = new JLabel("是否使用此连接");
        useLabel.setBounds(10, 10 + ((labelTextHeight + 10) * 6), labelWidth, labelTextHeight);

        useBox = new JCheckBox("启用");
        useBox.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 6), textWidth, labelTextHeight);

        JButton saveBtn = new JButton("保存");
        saveBtn.setBounds(labelWidth, 10 + ((labelTextHeight + 10) * 7), 110, labelTextHeight);
        JButton testBtn = new JButton("测试连接");
        testBtn.setBounds(labelWidth * 2, 10 + ((labelTextHeight + 10) * 7), 110, labelTextHeight);

        this.add(hostLabel);
        this.add(hostText);

        this.add(userNameLabel);
        this.add(userNameText);

        this.add(pwdLabel);
        this.add(pwdText);

        this.add(portLabel);
        this.add(portText);

        this.add(dbLabel);
        this.add(dbText);

        this.add(codeLabel);
        this.add(codeCombo);

        this.add(useLabel);
        this.add(useBox);

        this.add(saveBtn);
        this.add(testBtn);

        saveBtn.addActionListener(new ExcActionListener());
        testBtn.addActionListener(new ExcActionListener());

    }

    public boolean testConnection() {
        boolean bool = false;
        String ip = hostText.getText();
        String port = portText.getText();
        String dnname = dbText.getText();
        String username = userNameText.getText();
        String password = new String(pwdText.getPassword());
        try {
            url = CommonUtils.format(url, ip, port, dnname);
            // 加载MySql的驱动类
            Class.forName("com.mysql.jdbc.Driver");
            // 连接MySql数据库，用户名和密码都是root
            Connection conn = DriverManager.getConnection(url, username, password);
            if (conn != null) {
                bool = true;
                conn.close();
            }
        } catch (Exception e) {
            bool = false;
            System.out.println(ip + ":" + port + ", " + dnname + ", " + username + ", " + password);
        }
        return bool;
    }

    public void setConfigValue(HashMap<String, String> cfg) {
        hostText.setText(CommonUtils.excNullToString(cfg.get("host"), ""));
        portText.setText(CommonUtils.excNullToString(cfg.get("port"), "3306"));
        dbText.setText(CommonUtils.excNullToString(cfg.get("db_name"), ""));
        userNameText.setText(CommonUtils.excNullToString(cfg.get("userName"), ""));
        pwdText.setText(CommonUtils.excNullToString(cfg.get("password"), ""));
        useBox.setSelected((cfg.get("password") != null && cfg.get("isSelected").equals("true")));
        codeCombo.setSelectedItem(CommonUtils.excNullToString(cfg.get("encode"), "UTF-8"));
    }

    private class ExcActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = e.getActionCommand();
            if (name.equals("测试连接")) {
                Toolkit.getDefaultToolkit().beep();
                if (testConnection()) {
                    JOptionPane.showMessageDialog(null, "恭喜，连接成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "连接失败，请检查", "提示", JOptionPane.ERROR_MESSAGE);
                }
            } else if (name.equals("保存")) {
                Toolkit.getDefaultToolkit().beep();
                try {
                    String conn_name = MainWindow.mainWindow.dbsetPanel.getConnectionName();
                    String ip = hostText.getText();
                    String port = portText.getText();
                    String dnname = dbText.getText();
                    String username = userNameText.getText();
                    String password = new String(pwdText.getPassword());
                    String isSelected = useBox.isSelected() ? "true" : "false";
                    String encode = codeCombo.getSelectedItem().toString();

                    IniReader reader = IniReader.getIniReader();
                    if (isSelected.equals("true")) {
                        //HashMap<String, String> sedMap = reader.getActiveCfg(sections);
                        reader.replaceActiveCfg(sections, conn_name);
                        /*if(sedMap != null){
                            reader.putValue(sections, sedMap.get("conn_name"), "isSelected", "false");
                        }*/
                    }
                    reader.putValue(sections, conn_name, "conn_name", conn_name)
                            .putValue(sections, conn_name, "host", ip)
                            .putValue(sections, conn_name, "port", port)
                            .putValue(sections, conn_name, "userName", username)
                            .putValue(sections, conn_name, "password", password)
                            .putValue(sections, conn_name, "db_name", dnname)
                            .putValue(sections, conn_name, "isSelected", isSelected)
                            .putValue(sections, conn_name, "encode", encode)
                            .save();

                    JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "保存失败，请检查", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
