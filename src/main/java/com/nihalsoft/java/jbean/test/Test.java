package com.nihalsoft.java.jbean.test;

import com.nihalsoft.java.jbean.JBean;

public class Test {

    public static void main(String[] args) throws Exception {
        
        System.out.println("==================================");
        
        JBean.builder().includeSuperClassForInject(true).registerWith(EmpDao.class).build(Test.class);
        
        EmpDao td = JBean.get(EmpDao.class);
        td.test();

        
    }

}
