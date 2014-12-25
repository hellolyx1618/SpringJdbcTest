/*
 * Copyright (c) 2014.
 */

package com.lyx.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lyx.model.Gender;
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
 * test insert base dao
 * Created by liyanxin on 2014/12/13.
 */
@Transactional(rollbackFor = Exception.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",
        defaultRollback = false)
public class TestInsert {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return this.userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void test978234() {
        User user = new User();
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(new Boolean(true));
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
        user.setStringFlag("sddddddddddd");

        //全量插入
        System.out.println(this.userDao.insert(user));
    }


    /**
     * 测试插入时间日期和枚举类型
     */
    @Test
    public void test9234() {
        User user = new User();
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(new Boolean(true));
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
        user.setStringFlag("sddddddddddd");
        user.setTimeFlag(new Date());
        user.setDateFlag(new Date());
        user.setUerGender(Gender.FMAIL);
        user.setRoleGender(Gender.MAIL);

        //全量插入
        System.out.println(this.userDao.insert(user));
    }

    /**
     * userDao.insert(sqlContext)
     */
    @Test
    public void test978() {
        User user = new User();
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(new Boolean(true));
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
        user.setStringFlag("sddddddddddd");

        SqlContext sqlContext = user.buildInsertSQL("intFlag",
                "bigDecimalFlag", "floatFlag", "online", "doubleFlag",
                "lastLoginDate", "integerObjFlag", "boolObjFlag", "shortObjFlag");

        System.out.println(this.userDao.insert(sqlContext));
    }

    /**
     * userDao.insert(sqlInsert, user)
     */
    @Test
    public void test22323() {
        User user = new User();
        user.setBigDecimalFlag(new BigDecimal(2.3));
        user.setBoolObjFlag(new Boolean(true));
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
        user.setStringFlag("sddddddddddd");

        String sqlInsert = "insert into tb_hello_user (int_flag,bigDecimalFlag,_float_flag,on_line,"
                + "double_fg,last_login_date,integer_obj_flag,bool_obj_flag,short_obj_flag,char_flag) values"
                + "(:intFlag,:bigDecimalFlag,:floatFlag,"
                + ":online,:doubleFlag,:lastLoginDate,:integerObjFlag,:boolObjFlag,:shortObjFlag,:charFlag)";

        System.out.println(this.userDao.insert(sqlInsert, user));
    }

    /**
     * userDao.insert(sqlInsert, params)
     */
    @Test
    public void test2233423() {
        User user = new User();
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
        user.setStringFlag("sddddddddddd");
        String sqlInsert = "insert into tb_hello_user (int_flag,bigDecimalFlag,_float_flag,on_line,"
                + "double_fg,last_login_date,integer_obj_flag,bool_obj_flag,short_obj_flag) values"
                + "(:intFlag,:bigDecimalFlag,:floatFlag,"
                + ":online,:doubleFlag,:lastLoginDate,:integerObjFlag,:boolObjFlag,:shortObjFlag)";
        Map<String, Object> params = user.convertToMap(false, true, "intFlag",
                "bigDecimalFlag", "floatFlag", "online", "doubleFlag",
                "lastLoginDate", "integerObjFlag", "boolObjFlag", "shortObjFlag");
        System.out.println(this.userDao.insert(sqlInsert, params));
    }

    /**
     * userDao.insert(sqlInsert, params, primaryKey)
     */
    @Test
    public void test8767() {

        User user = new User();
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
        user.setStringFlag("sddddddddddd");
        String sqlInsert = "insert into tb_hello_user (int_flag,bigDecimalFlag,_float_flag,on_line,"
                + "double_fg,last_login_date,integer_obj_flag,bool_obj_flag,short_obj_flag) values"
                + "(?,?,?,?,?,?,?,?,?)";

        List<Object> params = user.convertToList(true, "intFlag",
                "bigDecimalFlag", "floatFlag", "online", "doubleFlag",
                "lastLoginDate", "integerObjFlag", "boolObjFlag", "shortObjFlag");

        String primaryKey = "id";
        System.out.println(this.userDao.insert(sqlInsert, params, primaryKey));
    }

    /**
     * userDao.insert(sqlInsert, params, primaryKey)
     */
    @Test
    public void test2223() {
        User user = new User();
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
        user.setStringFlag("sddddddddddd");
        String sqlInsert = "insert into tb_hello_user (int_flag,bigDecimalFlag,_float_flag,on_line,"
                + "double_fg,last_login_date,integer_obj_flag,bool_obj_flag,short_obj_flag) values"
                + "(:intFlag,:bigDecimalFlag,:floatFlag,"
                + ":online,:doubleFlag,:lastLoginDate,:integerObjFlag,:boolObjFlag,:shortObjFlag)";
        Map<String, Object> params = user.convertToMap(false, true, "intFlag",
                "bigDecimalFlag", "floatFlag", "online", "doubleFlag",
                "lastLoginDate", "integerObjFlag", "boolObjFlag", "shortObjFlag");

        String primaryKey = "id";
        System.out.println(this.userDao.insert(sqlInsert, params, primaryKey));
    }

}
