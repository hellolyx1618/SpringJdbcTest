package com.lyx.base;

/**
 * Created by liyanxin on 2014/12/3.
 */
public class DefaultNameHandler implements NameHandler {

    private static final char DELIMITER_UNDERLINE = '_';

    private static final String TABLE_PREFIX = "tb_";

    private static final String PRIMARY_SUFFIX = "_id";

    /**
     * 根据实体名获取表名
     *
     * @param entityName
     * @return
     */
    @Override
    public String getTableName(String entityName) {
        //Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
        String underlineName = this.getUnderlineName(entityName);
        return TABLE_PREFIX + underlineName;
    }

    /**
     * 根据表名获取主键名
     *
     * @param entityName
     * @return
     */
    @Override
    public String getPrimaryName(String entityName) {
        String underlineName = this.getUnderlineName(entityName);
        return underlineName + PRIMARY_SUFFIX;
    }

    /**
     * 根据属性名获取列名
     *
     * @param fieldName
     * @return
     */
    @Override
    public String getColumnName(String fieldName) {
        return this.getUnderlineName(fieldName);
    }


    public String getUnderlineName(String name) {
        StringBuilder underLineName;
        if (null != name && !name.equals("")) {
            underLineName = new StringBuilder();
            char[] array = name.toCharArray();
            for (char c : array) {
                if (!Character.isLowerCase(c)) {
                    underLineName.append(DELIMITER_UNDERLINE);
                    underLineName.append(Character.toLowerCase(c));
                } else {
                    underLineName.append(c);
                }
            }
        } else {
            throw new JdbcDataAccessException("data access exception");
        }

        return underLineName.charAt(0) == DELIMITER_UNDERLINE ?
                underLineName.substring(1) : underLineName.toString();
    }

}
