package com.lyx.test;

import com.lyx.base.DefaultNameHandler;
import com.lyx.base.SqlContext;
import com.lyx.base.SqlUtils;
import com.lyx.model.User;
import org.junit.Test;

/**
 * Created by liyanxin on 2014/12/6.
 */
public class TestSqlUtils {
    /**
     *
     */
    @Test
    public void tets766789() {
        String test1 = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
        System.out.println(test1.split(",").length);

        String tet232 = "[lyx, 12, 青年大街, Sat Dec 06 02:56:45 CST 2014, Sat Dec 06 02:56:45 CST 2014, false, 1234234, FEMALE, 233454.23, 12344543, 1234, 233435.23, false, 23.2, 234, 1, M, c]";
        System.out.println(tet232.split(",").length);
    }


    /**
     * 测试 buildInsertSql && buildUpdateSql
     */
    @Test
    public void tetu8() {
        User user = new User();
        SqlContext insertSql = SqlUtils.buildInsertSql(user, new DefaultNameHandler());
        SqlContext updateSql = SqlUtils.buildUpdateSql(user, new DefaultNameHandler());

        System.out.println(insertSql.getSql());
        System.out.println(insertSql.getPrimaryKey());

        System.out.println("===============================");

        System.out.println(updateSql.getSql());
        System.out.println(updateSql.getPrimaryKey());
    }


}
