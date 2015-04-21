package com.emptypointer.hellocdut.service;

/**
 * EmptyPoniter的Ecption类
 *
 * @author Sequarius
 */
public class EPRulesException extends RuntimeException {
    private static final long serialVersionUID = -3195583172799802661L;

    public EPRulesException() {

    }

    public EPRulesException(String message, Throwable cause) {
        super(message, cause);
    }

    public EPRulesException(String message) {
        super(message);
    }

    public EPRulesException(Throwable cause) {
        super(cause);
    }
}
