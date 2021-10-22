package com.nihalsoft.java.jbean;

public class JBeanBuilder {

    private String packageToScan = "";
    private String propertySource = "";
    private boolean includeSuperClassForInject = false;
    private boolean verbose = true;

    public JBeanBuilder() {
    }

    public JBeanBuilder(Class<?> clazz) {
        this.packageToScan = clazz.getPackage().getName();
    }

    public String getPackageToScan() {
        return packageToScan;
    }

    public JBeanBuilder packageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
        return this;
    }

    public String getPropertySource() {
        return propertySource;
    }

    public JBeanBuilder propertySource(String propertySource) {
        this.propertySource = propertySource;
        return this;
    }

    public boolean isIncludeSuperClassForInject() {
        return includeSuperClassForInject;
    }

    public JBeanBuilder includeSuperClassForInject(boolean includeSuperClassForInject) {
        this.includeSuperClassForInject = includeSuperClassForInject;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public JBeanBuilder withNoVerbose() {
        this.verbose = false;
        return this;
    }

    public void run(Class<?> clazz) throws Exception {
        if (packageToScan.isEmpty()) {
            packageToScan = clazz.getPackage().getName();
        }
        JBean.run(clazz);
    }
}
