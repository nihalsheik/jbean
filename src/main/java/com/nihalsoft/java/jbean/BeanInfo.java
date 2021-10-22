package com.nihalsoft.java.jbean;

public class BeanInfo {

    private BeanScope scope;
    private Object object;

    public BeanInfo(BeanScope scope, Object object) {
        this.scope = scope;
        this.object = object;
    }

    public BeanScope scope() {
        return scope;
    }

    public Object object() {
        return object;
    }

    public boolean isSingleton() {
        return scope == BeanScope.SINGLETON;
    }
    
    public boolean isPrototype() {
        return scope == BeanScope.PROTOTYPE;
    }
}
