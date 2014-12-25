/*
 * Copyright (c) 2014.
 */

package com.lyx.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class TestDelete {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return this.userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void test8976() {
        System.out.println(this.userDao.deleteById(12));
    }

    @Test
    public void test7yt5() {
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

        System.out.println(user.getClass().hashCode());
        user.setStringFlag("================");
        System.out.println(user.getClass().hashCode());
    }

    @Test
    public void test8765() {

        String sql = "delete from tb_hello_user where id in (:ids)";
        Map<String, Object> param = new HashMap<>();
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        param.put("ids", ids);
        System.out.println(this.userDao.deleteEntities(sql, param));
    }

    @Test
    public void test() {
        this.userDao.deleteAll();
    }
}
