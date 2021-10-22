package com.nihalsoft.java.jbean.common;

import java.io.File;
import java.nio.file.Paths;

import com.nihalsoft.java.jbean.annotation.Bean;

public class BeanUtil {

    public static File getEnvFile() {

        System.out.println("-----------> application.properties doesn't exist, Searching....");

        String[] paths1 = new String[] { "", "src/main/resources", "resources", "src" };
        String[] paths2 = new String[] { "", "conf", "config" };

        File file = null;
        for (String path1 : paths1) {
            for (String path2 : paths2) {
                file = Paths.get(path1, path2, "application.properties").toFile();
                System.out.println("-----------> Searching file in " + file.getAbsolutePath());
                if (file.exists()) {
                    System.out.println("-----------> application.properties found in " + file.getAbsolutePath());
                    break;
                }
            }
            if (file.exists()) {
                break;
            }
        }

        if(!file.exists()) {
            file = null;
        }
        return file;
    }

    public static String getBeanName(Bean bean, Class<?> clazz) {
        return bean.name().isEmpty() ? clazz.getName() : bean.name();
    }
}
