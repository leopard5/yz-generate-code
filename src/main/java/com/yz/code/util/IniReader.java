package com.yz.code.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IniReader {
    private static Logger log = Logger.getLogger(IniReader.class);

    private static IniReader reader = null;
    public static HashMap<String, HashMap<String, HashMap<String, String>>> sections = new HashMap<String, HashMap<String, HashMap<String, String>>>();
    private String currentSecion, currTag;
    private HashMap<String, HashMap<String, String>> current;
    private HashMap<String, String> cfgMap;
    private static String fileName = "/cfg.ini";
    public static String file = "/cfg.ini";
    public static String baseDir = "/";

    private IniReader() {
    }

    private IniReader(String filename) {
        file = filename;
        init();
    }

    public static IniReader getIniReader() {
        if (reader == null) {
            baseDir = Utililies.getBaseDir();
            reader = new IniReader(baseDir + fileName);
        }
        return reader;
    }

    private void init() {
        BufferedReader reader = null;
        FileReader fr = null;
        try {
            FileUtils.createDir(baseDir);
            FileUtils.createFile(file);
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            read(reader);


            if (!sections.containsKey("use_db_type")) {
                HashMap<String, HashMap<String, String>> tagMap = new HashMap<String, HashMap<String, String>>();
                HashMap<String, String> cfgMap = new HashMap<String, String>();
                cfgMap.put("type", "mysql_conn_info");
                tagMap.put("db", cfgMap);
                sections.put("use_db_type", tagMap);
                this.save();
            }
        } catch (Exception e) {
            log.error("初始化配置失败", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        line = line.trim();
        if (line.matches("\\[.*\\]")) {
            currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
            current = new HashMap<String, HashMap<String, String>>();
            sections.put(currentSecion, current);
            cfgMap = new HashMap<String, String>();
        } else if (line.matches("<.*>")) {
            currTag = line.replaceFirst("<(.*)>", "$1");
            cfgMap = new HashMap<String, String>();
            current.put(currTag, cfgMap);
        } else if (line.matches(".*=.*")) {
            if (cfgMap != null) {
                int i = line.indexOf('=');
                String name = line.substring(0, i);
                String value = line.substring(i + 1);
                cfgMap.put(name, value);
            }
        }
    }

    public HashMap<String, HashMap<String, String>> getConfig(String section) {
        HashMap<String, HashMap<String, String>> p = sections.get(section);
        if (p == null) {
            return null;
        }
        return p;
    }

    public HashMap<String, String> getConfig(String section, String tag) {
        HashMap<String, HashMap<String, String>> p = sections.get(section);
        if (p == null) {
            return null;
        }
        HashMap<String, String> valMap = p.get(tag);
        if (valMap == null) {
            return null;
        }
        return valMap;
    }

    public String getValue(String section, String tag, String name) {
        HashMap<String, String> valMap = getConfig(section, tag);
        if (valMap == null) {
            return null;
        }
        return valMap.get(name);
    }

    public IniReader putValue(String section, String tag, String name, String value) {
        HashMap<String, HashMap<String, String>> map = sections.get(section);
        HashMap<String, String> cfg = null;
        if (map == null) {
            map = new HashMap<String, HashMap<String, String>>();
            sections.put(section, map);
            cfg = new HashMap<String, String>();
            map.put(tag, cfg);
        } else {
            cfg = map.get(tag);
            if (cfg == null) {
                cfg = new HashMap<String, String>();
                map.put(tag, cfg);
            }
        }
        cfg.put(name, value);
        return this;
    }

    /**
     * 获取正激活使用的配置
     *
     * @param section
     * @return
     */
    public HashMap<String, String> getActiveCfg(String section) {
        HashMap<String, HashMap<String, String>> map = sections.get(section);
        HashMap<String, String> result = null, temp = null;
        if (map != null) {
            String isSelected = "";
            for (Map.Entry<String, HashMap<String, String>> ent : map.entrySet()) {
                temp = ent.getValue();
                isSelected = temp.get("isSelected");
                if (isSelected != null && isSelected.equals("true")) {
                    result = temp;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 替换激活配置
     *
     * @param section
     * @return
     */
    public HashMap<String, String> replaceActiveCfg(String section, String tag) {
        HashMap<String, HashMap<String, String>> map = sections.get(section);
        HashMap<String, String> result = null;
        if (map != null) {
            result = map.get(tag);
            String key = "";
            for (Map.Entry<String, HashMap<String, String>> ent : map.entrySet()) {
                key = ent.getKey();
                if (key.equals(tag)) {
                    this.putValue(section, key, "isSelected", "true");
                } else {
                    this.putValue(section, key, "isSelected", "false");
                }
            }
        }
        return result;
    }

    public IniReader save() {
        OutputStream fos = null;
        try {
            StringBuffer sb = new StringBuffer();
            HashMap<String, HashMap<String, String>> currMap = null;
            HashMap<String, String> cfgMap = null;
            for (Map.Entry<String, HashMap<String, HashMap<String, String>>> entry : sections.entrySet()) {
                sb.append("[").append(entry.getKey()).append("]\r\n");
                currMap = entry.getValue();

                if (currMap != null && currMap.entrySet().size() > 0) {
                    for (Map.Entry<String, HashMap<String, String>> ent : currMap.entrySet()) {
                        sb.append("<").append(ent.getKey()).append(">\r\n");
                        cfgMap = ent.getValue();

                        for (Map.Entry<String, String> e : cfgMap.entrySet()) {
                            sb.append(e.getKey() + "=").append(e.getValue() + "\r\n");
                        }
                    }
                }
                sb.append("\r\n");
            }
            fos = new FileOutputStream(file);// 加载读取文件流
            fos.write(sb.toString().getBytes());
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    public static void main(String[] args) {
        IniReader reader = IniReader.getIniReader();
        System.out.println(IniReader.sections);
    }

}
