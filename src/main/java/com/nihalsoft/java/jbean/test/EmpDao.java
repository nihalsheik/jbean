package com.nihalsoft.java.jbean.test;

import com.nihalsoft.java.jbean.annotation.Bean;

@Bean(name = "empdao")
public class EmpDao extends BaseDao {

    public void test() {
        System.out.println("Emplayee dao");
        td.test2();
    }
}
