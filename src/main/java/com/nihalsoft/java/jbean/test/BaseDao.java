package com.nihalsoft.java.jbean.test;

import com.nihalsoft.java.jbean.annotation.Inject;

public class BaseDao {

    protected TestDao td;

    @Inject
    public void setTestDao(@Inject("testdao") TestDao td) {
        this.td = td;
    }
}
