package com.nihalsoft.java.jbean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.nihalsoft.java.jbean.annotation.Bean;
import com.nihalsoft.java.jbean.annotation.BeanConfiguration;
import com.nihalsoft.java.jbean.annotation.Inject;
import com.nihalsoft.java.jbean.common.BeanUtil;
import com.nihalsoft.java.jbean.common.EnvMap;
import com.nihalsoft.java.jbean.common.Reflection;

public class JBean {

    private static Map<String, BeanInfo> beanList = new HashMap<String, BeanInfo>();

    private static Reflection reflections;
    private static JBeanBuilder jb;

    private static final Logger log = Logger.getLogger("JBean");

    private static boolean _verbose;

    public static JBeanBuilder build() {
        jb = new JBeanBuilder();
        return jb;
    }

    public static void run(Class<?> clazz) throws Exception {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");

        if (jb == null) {
            jb = new JBeanBuilder(clazz);
        }

        String pkg = jb.getPackageToScan();
        _verbose = jb.isVerbose();

        try {

            _log("Start scanning packages :" + pkg);

            if (pkg == null || pkg.length() == 0) {
                System.out.println("No packages to scan");
                return;
            }

            reflections = new Reflection();

            _register(EnvMap.class);
            JBean.get(EnvMap.class).load(jb.getPropertySource());

            _scanResource();
            _scanResources();
            _inject();

            AtomicInteger i = new AtomicInteger(0);
            beanList.forEach(
                    (k, v) -> _log("Bean : -----> " + i.incrementAndGet() + ",  " + k + " Scope : " + v.scope()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T get(String name) {
        return _getBean(name);
    }

    public static <T> T get(Class<T> clazz) {
        Bean bean = clazz.getAnnotation(Bean.class);
        if (bean == null) {
            return null;
        }
        return _getBean(BeanUtil.getBeanName(bean, clazz));
    }

    @SuppressWarnings("unchecked")
    public static <T> T _getBean(String name) {
        try {

            if (!beanList.containsKey(name)) {
                return null;
            }

            BeanInfo bi = beanList.get(name);

            if (bi.isSingleton()) {
                return (T) bi.object();

            } else if (bi.isPrototype()) {
                Class<T> c = (Class<T>) Class.forName(bi.object().toString());
                _log("Prototype Bean " + c.getName());
                Object ins = c.newInstance();
                JBean.inject(ins);
                return (T) ins;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void inject(Object object) {
        JBean._injectField(object, object.getClass());
        JBean._injectSetter(object, object.getClass());
    }

    private static void _scanResources() throws Exception {

        _log("");
        int i = 0;

        List<Class<?>> annotated = reflections.getClassesAnnotatedWith(jb.getPackageToScan(), BeanConfiguration.class);

        for (Class<?> clazz : annotated) {

            Object resourceList = clazz.newInstance();
            JBean._injectField(resourceList, resourceList.getClass());

            List<Method> methods = reflections.getDeclaredMethodsAnnotatedWith(clazz, Bean.class);

            i = 0;
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean != null && bean.scope() == BeanScope.SINGLETON) {
                    _register(bean, method.invoke(resourceList));
                    i++;
                }
            }

            _log("Resource list count for " + clazz.getName() + " is " + i);
        }

    }

    private static void _scanResource() throws Exception {
        _log("");
        _log("Scanning Resource");
        List<Class<?>> annotated = reflections.getClassesAnnotatedWith(jb.getPackageToScan(), Bean.class);
        annotated.forEach(JBean::_register);
        _log("Resource count " + annotated.size());
    }

    private static void _inject() {

        beanList.forEach((k, bi) -> {
            if (bi.isSingleton()) {
                JBean._injectField(bi.object(), bi.object().getClass());
                JBean._injectSetter(bi.object(), bi.object().getClass());
            }
        });

        beanList.forEach((k, bi) -> {
            try {
                Class<?> clazz = bi.object().getClass();

                List<Method> methods = reflections.getDeclaredMethodsAnnotatedWith(clazz, PostConstruct.class);
                for (Method method : methods) {
                    _log("=============================>" + clazz.getName() + " -->" + method.getName());
                    method.setAccessible(true);
                    method.invoke(bi.object());
                }
            } catch (Exception e) {
            }
        });
    }

    private static void _injectField(Object object, Class<?> clazz) {
        String className = clazz.getName();

        List<Field> fields = reflections.getDeclaredFieldsAnnotatedWith(clazz, Inject.class);

        _log("Injecting for " + className);

        for (Field field : fields) {
            Inject inj = field.getAnnotation(Inject.class);
            try {
                String name = inj.value().equals("") ? field.getType().getName() : inj.value();
                _log("    Injecting Field --> " + field.getName() + ", Source " + name);
                Object ins = JBean.get(name);
                if (ins != null) {
                    field.setAccessible(true);
                    field.set(object, ins);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!jb.isIncludeSuperClassForInject()) {
            return;
        }

        if (!clazz.getSuperclass().getSimpleName().equals("Object")) {
            _log("Super class " + clazz.getSuperclass().getSimpleName());
            _injectField(object, clazz.getSuperclass());
        }
    }

    // @TODO : Check
    private static void _injectSetter(Object object, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Inject inj = method.getAnnotation(Inject.class);
            if (inj == null) {
                continue;
            }
            try {
                _log("Injecting Setter - method " + method.getName());
                Object[] args = new Object[method.getParameterCount()];
                int i = 0;
                for (Parameter p : method.getParameters()) {
                    Inject pann = p.getAnnotation(Inject.class);
                    String name = "";
                    if (pann != null && !pann.value().isEmpty()) {
                        name = pann.value();
                    } else {
                        name = p.getType().getName();
                    }
                    _log("Parameter name -->" + name);
                    args[i++] = JBean.get(name);
                }
                _log("Injecting setter - invokind");
                method.invoke(object, args);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!jb.isIncludeSuperClassForInject()) {
            return;
        }

        if (!clazz.getSuperclass().getSimpleName().equals("Object")) {
            _log("Super class " + clazz.getSuperclass().getSimpleName());
            _injectSetter(object, clazz.getSuperclass());
        }
    }

    private static void _register(Class<?>... classes) {
        try {
            for (Class<?> clazz : classes) {
                Bean bean = clazz.getAnnotation(Bean.class);
                if (bean == null) {
                    return;
                }
                _register(bean, clazz.newInstance());
            }
        } catch (Exception ex) {
            _log(ex.getMessage());
        }
    }

    private static void _register(Bean bean, Object instance) {
        try {
            String name = BeanUtil.getBeanName(bean, instance.getClass());
            _log("Find bean ......" + name);
            BeanInfo b = null;
            if (bean.scope() == BeanScope.SINGLETON) {
                b = new BeanInfo(bean.scope(), instance);

            } else if (bean.scope() == BeanScope.PROTOTYPE) {
                b = new BeanInfo(bean.scope(), name);
            }
            beanList.put(name, b);

        } catch (

        Exception ex) {
            _log(ex.getMessage());
        }
    }

    private static void _log(String msg) {
        if (_verbose) {
            log.info("     --| " + msg);
        }
    }

}
