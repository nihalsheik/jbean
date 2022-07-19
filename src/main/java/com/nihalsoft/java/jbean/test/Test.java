package com.nihalsoft.java.jbean.test;

import com.nihalsoft.java.jbean.JBean;

public class Test {

    public static void main(String[] args) throws Exception {
        
        System.out.println("==================================");
        
        JBean.build().includeSuperClassForInject(true).run(Test.class);
        
        EmpDao td = JBean.get(EmpDao.class);
        td.test();

        
    }

}
