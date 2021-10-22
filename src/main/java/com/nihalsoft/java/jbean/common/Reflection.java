package com.nihalsoft.java.jbean.common;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflection {

    public Reflection() {
    }

    public List<Field> getFieldsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        return _fields(clazz.getFields(), annotation);
    }

    public List<Field> getDeclaredFieldsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        return _fields(clazz.getDeclaredFields(), annotation);
    }

    public List<Field> _fields(Field[] fields, Class<? extends Annotation> annotation) {
        List<Field> flist = new ArrayList<Field>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(annotation)) {
                flist.add(f);
            }
        }
        return flist;
    }

    public List<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        return _methods(clazz.getMethods(), annotation);
    }

    public List<Method> getDeclaredMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        return _methods(clazz.getDeclaredMethods(), annotation);
    }

    public List<Method> _methods(Method[] methods, Class<? extends Annotation> annotation) {
        List<Method> mList = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                mList.add(method);
            }
        }
        return mList;
    }

    public List<Class<?>> getClassesImplementedWith(String packageName, Class<?> clazz) throws Exception {
        return this.findAllClasses2(packageName, c -> {
            Class<?>[] interfaces = c.getInterfaces();
            boolean res = false;
            for (Class<?> iface : interfaces) {
                res = iface.getName().equals(clazz.getName());
            }
            return res;
        });
    }

    public List<Class<?>> getClassesAnnotatedWith(String packageName, Class<? extends Annotation> clazz)
            throws Exception {
        return this.findAllClasses2(packageName, c -> {
            return c.isAnnotationPresent(clazz);
        });
    }

    private List<Class<?>> findAllClasses2(String packageName, Predicate<Class<?>> filter) throws Exception {
        List<Class<?>> k = new ArrayList<Class<?>>();

        this.findAllClasses(packageName, className -> {
            try {
                Class<?> c = Class.forName(className);
                if (filter.test(c)) {
                    k.add(c);
                }
            } catch (Exception e) {
            }
            return false;
        });

        return k;
    }

    public List<String> findAllClasses(String packageName, Predicate<String> filter) throws Exception {

        List<String> list = new ArrayList<String>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // File(ClassFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();

        if (filter == null) {
            filter = (file) -> {
                return true;
            };
        }

        String pkgs = packageName.replace(".", "/");
        URL packageURL = classLoader.getResource(pkgs);

        if (packageURL.getProtocol().equals("jar")) {
            list = findClassesFromJar(packageURL, pkgs, filter);
        } else {
            list = findClassesFromFolder(packageName, new File(packageURL.getPath()), filter);
        }

        return list;
    }

    private List<String> findClassesFromJar(URL packageURL, String packageName, Predicate<String> filter)
            throws Exception {

        List<String> list = new ArrayList<String>();

        String entryName;

        String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
        jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));

        System.out.println("JAR > " + jarFileName);

        JarFile jf = new JarFile(jarFileName);
        Enumeration<JarEntry> jarEntries = jf.entries();

        while (jarEntries.hasMoreElements()) {
            entryName = jarEntries.nextElement().getName();
            if (entryName.startsWith(packageName) && entryName.endsWith(".class")) {
                entryName = entryName.replaceAll("/", "\\.");
                String myClass = entryName.substring(0, entryName.lastIndexOf('.'));
                if (filter.test(myClass)) {
                    list.add(myClass);
                }
            }

        }

        jf.close();
        return list;
    }

    private List<String> findClassesFromFolder(String pkg, File folder, Predicate<String> filter) {
        List<String> list = new ArrayList<String>();
        File[] files = folder.listFiles();
        String fn;
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(findClassesFromFolder(pkg + "." + f.getName(), f, filter));
            } else if (f.getName().endsWith(".class")) {
                fn = pkg + "." + f.getName().substring(0, f.getName().lastIndexOf('.'));
                if (filter.test(fn)) {
                    list.add(fn);
                }
            }
        }
        return list;
    }

}