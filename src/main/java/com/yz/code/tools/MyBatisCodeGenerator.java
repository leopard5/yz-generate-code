package com.yz.code.tools;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.painter.border.StandardBorderPainter;
import org.pushingpixels.substance.api.skin.*;

import javax.swing.*;

/**
 * 本类不用修改，运行就行
 * 具体配置，请修改 src/test/resources/MBG_configuration.xml
 */
public class MyBatisCodeGenerator {


    public static void main(String[] args) throws Exception {

        JFrame frame = new JFrame();
//        setaTema(frame);

        frame.setBounds(100, 100, 200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);

        for (int i = 0; i < 10; i++) {
            JButton but = new JButton(i + "");
            but.setBounds((i % 3) * 70, 30 * (i / 3), 60, 30);
            frame.add(but);
        }


//        System.out.println("+++++++++generate begin++++++++++");
//        List<String> warnings = new ArrayList<String>();
//        boolean overwrite = true;
//        String url = MyBatisCodeGenerator.class.getResource("/MBG_configuration.xml").getFile();
//        File configFile = new File(url);
//        ConfigurationParser cp = new ConfigurationParser(warnings);
//        Configuration config = cp.parseConfiguration(configFile);
//        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
//        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
//        myBatisGenerator.generate(null);
//        System.out.println("+++++++++generate writer end+++++++++++");
    }
}
