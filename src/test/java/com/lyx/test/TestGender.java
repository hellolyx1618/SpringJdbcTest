package com.lyx.test;

import com.lyx.model.Gender;
import org.junit.Test;

/**
 * Created by liyanxin on 2014/12/24.
 */
public class TestGender {

    @Test
    public void test8679() {
        System.out.println(Gender.FMAIL);
        System.out.println(Gender.FMAIL.name());
    }

    @Test
    public void test98767() {
        System.out.println(Gender.MAIL.ordinal());
        System.out.println(Gender.FMAIL.ordinal());
    }

    @Test
    public void test987() {
        System.out.println(Gender.values()[0]);
        System.out.println(Gender.values()[1]);
    }

    @Test
    public void tes987() {
        System.out.println(Gender.valueOf("MAIL").name());
        System.out.println(Gender.valueOf("MAIL").ordinal());
        System.out.println(Enum.valueOf(Gender.class, "MAIL").name());
        System.out.println(Enum.valueOf(Gender.class, "MAIL").ordinal());
    }

    @Test
    public void tes987987() {
        System.out.println(Gender.class.getEnumConstants()[1].getClass().isEnum());
    }

    @Test
    public void test86() {
        Class clz = Gender.class;
        System.out.println(Enum.valueOf(clz, "MAIL"));

        int i = 1;
        System.out.println(clz.getEnumConstants()[i]);
    }
}
