package com.lyx.base;

/**
 * Created by liyanxin on 2014/12/3.
 */
public class JdbcDataAccessException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 107839068284169233L;

    public JdbcDataAccessException() {
        super();
    }

    public JdbcDataAccessException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
