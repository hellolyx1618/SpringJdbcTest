package com.lyx.test;

import org.junit.Test;

import javax.persistence.EnumType;
import java.util.Date;

/**
 * Created by liyanxin on 2014/12/24.
 */
public class TestInstance {

    @Test
    public void test8678() {
        Date date = new Date();
        System.out.println(Date.class.isInstance(date)); //true
    }

    @Test
    public void test87ui() {
        Date date = new Date();
        System.out.println(date.getClass().hashCode());
        System.out.println(Date.class.hashCode());
    }

    @Test
    public void tes7ui() {
        Date date = new Date();
        System.out.println(date.getClass().hashCode());
        System.out.println(Date.class.hashCode());
    }

    @Test
    public void tes797ui() {
        System.out.println(EnumType.class.isAssignableFrom(Enum.class));
    }
}
