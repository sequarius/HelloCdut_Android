package com.emptypointer.hellocdut.utils;

import java.util.regex.Pattern;

public class StringChecker {
    /**
     * 核对手机号
     *
     * @param num
     * @return
     */
    public static boolean isPhoneNum(String num) {
        String strRegex = "[1][34578]\\d{9}";
        return num.matches(strRegex);
    }

    /**
     * 核对手机号
     *
     * @param num
     * @return
     */
    public static boolean isChaptcha(String num) {
        String strRegex = "\\d{4}";
        return num.matches(strRegex);
    }

    /**
     * 核对手机短号
     *
     * @param num
     * @return
     */
    public static boolean isShortNum(String num) {
        String strRegex = "\\d{6}";
        return num.matches(strRegex);
    }

    public static boolean isLegalUserName(String userName) {
        String strRegex = "^[0-9a-zA-Z]{6,16}$";
        return userName.matches(strRegex);
    }

    public static boolean isLegalPassword(String password) {
        String strRegex = "^.{6,16}$";
        return password.matches(strRegex);
    }

    public static boolean isLegalStudentID(String stuID) {
        String strRegex = "\\d{12}";
        return stuID.matches(strRegex);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 核对邮箱
     *
     * @param mail
     * @return
     */
    public static boolean isMail(String mail) {
        String regex = "[a-zA-Z_0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,3}){1,3}";
        return mail.matches(regex);
    }

}
