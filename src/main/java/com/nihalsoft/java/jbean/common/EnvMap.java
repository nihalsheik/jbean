package com.nihalsoft.java.jbean.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.nihalsoft.java.jbean.annotation.Bean;

@Bean
public class EnvMap extends DataMap {

    public void load(String source) {

        try {
            File file;

            if (!source.isEmpty()) {
                file = new File(source);
            } else {
                file = BeanUtil.getEnvFile();
            }

            if (file != null && file.exists()) {
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                p.forEach((k, v) -> this.put(k.toString(), v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
