package com.lyx.test;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.lyx.base.SqlContext;
import com.lyx.dao.UserDao;
import com.lyx.model.User;

/**
 * Created by liyanxin on 2014/12/13.
 */

@Transactional(rollbackFor = Exception.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",
        defaultRollback = false)
public class TestUpdate {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return this.userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void test232sdd3423() {
        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("update user 10");

        System.out.println(this.userDao.update(user));
    }

    @Test
    public void test87656() {

        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("================================");

        SqlContext sqlContext = user
            .buildUpdateSQL(
                "stringFlag,online,shortFlag,longFlag,lastLoginDate,charFlag",
                "id");

        System.out.println(this.userDao.update(sqlContext));
    }

    @Test
    public void test876() {

        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("===????????????????????===");

        String sql = "update tb_hello_user set string_flag=?,char_flag=?,short_flag=?,"
            + "on_line=?,long_flag=?,last_login_date=? where id=?";
        System.out.println(this.userDao.update(sql, user.convertToList(true,
            "stringFlag", "charFlag", "shortFlag", "online", "longFlag",
            "lastLoginDate", "id")));
    }

    @Test
    public void test234() {

        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("==>>>>>>>>>>>>>>>>>===");

        System.out.println(this.userDao.update0(user
            .buildUpdateSQLWithParamType("stringFlag,charFlag,"
                + "shortFlag,online,longFlag,lastLoginDate", "id")));

    }

    @Test
    public void test7656() {

        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("===????????????????????===");

        String sql = "update tb_hello_user set string_flag=?,char_flag=?,short_flag=?,"
            + "on_line=?,long_flag=?,last_login_date=? where id=?";
        System.out.println(this.userDao.update0(sql, user.convertToList(true,
            "stringFlag", "charFlag", "shortFlag", "online", "longFlag",
            "lastLoginDate", "id")));

    }

    @Test
    public void test7580() {
        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("===iiiiiiiiiii========================");

        SqlContext sqlContext = user
            .buildUpdateSQL(
                "stringFlag,online,shortFlag,longFlag,lastLoginDate,charFlag",
                "id");

        System.out.println(this.userDao.update(sqlContext.getSql().toString(),
            sqlContext.getParamHolder()));
    }

    @Test
    public void test76() {
        User user = new User();
        user.setId(10);
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(true);
        user.setByteFlag((byte) 3);
        user.setCharFlag('c');
        user.setCharacterObjFlag('c');
        user.setDoubleFlag(1.2d);
        user.setDoubleObjFlag(2.3d);
        user.setFloatFlag(2.3f);
        user.setFloatObjFlag(4.5f);
        user.setIntFlag(2);
        user.setIntegerObjFlag(64);
        user.setLastLoginDate(new Date());
        user.setLongFlag(7890L);
        user.setLongObjFlag(123L);
        user.setOnline(true);
        user.setShortFlag((short) 2);
        user.setShortObjFlag((short) 23);
        user.setStringFlag("===????????????????????===");

        String sql = "update tb_hello_user set string_flag=:stringFlag,char_flag=:charFlag,short_flag=:shortFlag,"
            + "on_line=:online,long_flag=:longFlag,last_login_date=:lastLoginDate where id=:id";
        System.out.println(this.userDao.update(sql, user));
    }

}
