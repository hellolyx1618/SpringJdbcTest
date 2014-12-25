/*
 * Copyright (c) 2014.
 */

package com.lyx.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TestQuery {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return this.userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void test2323423() {
        User user = new User();
        user.setDoubleFlag(0.0d);
        user.setDoubleObjFlag(2.3d);
        SqlContext sqlContext = user.buildSelectSQL(
            "intFlag,bigDecimalFlag,floatFlag,online,doubleFlag,"
                + "lastLoginDate,integerObjFlag,boolObjFlag,shortObjFlag",
            "doubleFlag,doubleObjFlag");

        System.out.println(sqlContext.getSql().toString());
        List<User> userList = this.userDao.queryEntities(sqlContext);
        System.out.println(userList);

    }

    @Test
    public void test2423() {
        System.out.println(this.userDao.queryAll());
    }

    @Test
    public void test869() {
        System.out.println(this.userDao.queryEntityById(1));
    }

    @Test
    public void test7t6() {

        User user = new User();
        user.setDoubleFlag(1.2);
        user.setId(2);
        String sql = user.buildSelectPartSQL("intFlag", "bigDecimalFlag",
            "floatFlag", "online", "doubleFlag", "lastLoginDate",
            "integerObjFlag", "boolObjFlag", "shortObjFlag", "charFlag");
        StringBuilder sqlToUse = new StringBuilder(sql);
        sqlToUse.append("where double_fg = :double_fg and id =:id");
        User u = this.userDao.queryEntity(sqlToUse.toString(),
            user.convertToMap(true, true, "doubleFlag", "id"));

        System.out.println(u);
    }

    @Test
    public void test9867() {
        User user = new User();
        user.setDoubleFlag(1.2);
        user.setId(2);
        String sql = user.buildSelectPartSQL("intFlag", "bigDecimalFlag",
            "floatFlag", "online", "doubleFlag", "lastLoginDate",
            "integerObjFlag", "boolObjFlag", "shortObjFlag", "charFlag");
        StringBuilder sqlToUse = new StringBuilder(sql);
        sqlToUse.append("where double_fg =? and id =?");
        User u = this.userDao.queryEntity(sqlToUse.toString(),
            user.convertToList(true, "doubleFlag", "id"));

        System.out.println(u);
    }

    @Test
    public void test96() {
        User user = new User();
        user.setBoolObjFlag(true);
        user.setCharFlag('c');
        SqlContext sqlContext = user
            .buildSelectSQL(
                "intFlag,bigDecimalFlag,floatFlag,online,doubleFlag"
                    + "lastLoginDate,integerObjFlag,boolObjFlag,shortObjFlag,charFlag",
                "boolObjFlag,charFlag");

        System.out.println(sqlContext.getSql().toString());
        System.out.println(sqlContext.getParamHolder().size());

        List<User> userList = this.userDao
            .queryEntities(user
                .buildSelectSQL(
                    "intFlag,bigDecimalFlag,floatFlag,online,doubleFlag"
                        + "lastLoginDate,integerObjFlag,boolObjFlag,shortObjFlag,charFlag",
                    "boolObjFlag,charFlag"));
        System.out.println(userList);
    }

    @Test
    public void test865678() {
        User user = new User();
        user.setBoolObjFlag(true);
        user.setCharFlag('c');
        user.setId(6);
        SqlContext sqlContext = user
            .buildSelectSQL(
                "intFlag,bigDecimalFlag,floatFlag,online,doubleFlag"
                    + "lastLoginDate,integerObjFlag,boolObjFlag,shortObjFlag,charFlag",
                "id");
        User u = this.userDao.queryEntity(sqlContext);
        System.out.println(u);
    }

    @Test
    public void testu7f() {
        User user = new User();
        user.setBoolObjFlag(true);
        String sql = "select int_flag,_float_flag,char_flag,on_line,bigDecimalFlag,bool_obj_flag,integer_obj_flag,short_obj_flag "
            + "from tb_hello_user where bool_obj_flag=?";

        List<User> userList = this.userDao.queryEntities(sql,
            user.convertToList(true, "boolObjFlag"));
        System.out.println(userList);
    }

    @Test
    public void testu7f23() {

        User user = new User();
        user.setBoolObjFlag(true);
        SqlContext sqlContext = user
            .buildSelectSQL(
                "intFlag,bigDecimalFlag,floatFlag,online,doubleFlag"
                    + "lastLoginDate,integerObjFlag,boolObjFlag,shortObjFlag,charFlag",
                "boolObjFlag");

        System.out.println(sqlContext.getSql().toString());
        System.out.println(sqlContext.getParamHolder().size());

        List<User> userList = this.userDao.queryEntities(sqlContext.getSql()
            .toString(), sqlContext.getParamHolder());

        System.out.println(userList);
    }

    @Test
    public void test() {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("flag", true);
        Long l = this.userDao.queryCount(
            "select count(*) from tb_hello_user where bool_obj_flag = :flag",
            queryParam);
        System.out.println(l.longValue());
    }
}
