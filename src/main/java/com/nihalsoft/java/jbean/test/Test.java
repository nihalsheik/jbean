package com.nihalsoft.java.jbean.test;

import com.nihalsoft.java.jbean.JBean;

public class Test {

    public static void main(String[] args) throws Exception {
        
        System.out.println("==================================");
        
        JBean.build().includeSuperClassForInject(true).run(Test.class);
        
        EmpDao td = JBean.get("empdao");
        td.test();

        TestDao td2 = JBean.get("testdao");
        td2.test();
        
    }

}
