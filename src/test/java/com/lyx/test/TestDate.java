package com.lyx.test;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liyanxin on 2014/12/24.
 */
public class TestDate {

    @Test
    public void test97789() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date ud = new Date();
        java.sql.Date sd = new java.sql.Date(ud.getTime());

        System.out.println(df.format(ud));
        System.out.println(df.format(sd));
    }

    @Test
    public void test970987789() {
        System.out.println(Date.class.getName());
    }
}
