package com.emptypointer.hellocdut.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfo {
    private static UserInfo instance;
    public static final int VISIBILE_ALL = 0;
    public static final int VISIBILE_FRIEND = 1;
    public static final int VISIBILE_NONE = 2;
    public static final String INITED_STRING = "";
    private SharedPreferences mSharedPreferences;
    private String metto = null;
    private String imageURL = null;
    private int loveStatus = Integer.MIN_VALUE;
    private int sexOrientation = Integer.MIN_VALUE;
    private int gender = Integer.MIN_VALUE;
    private String realName = null;
    private String birthDate = null;
    private String studentID = null;
    private String instituteName = null;
    private String majorName = null;
    private String classID = null;
    private String entryYear = null;
    private String mail = null;

    private int permissionLoveStatus = Integer.MIN_VALUE;
    private int permissionSexOrientation = Integer.MIN_VALUE;
    private int permissionRealName = Integer.MIN_VALUE;
    private int permissionBirthDate = Integer.MIN_VALUE;
    private int permissionInstitute = Integer.MIN_VALUE;
    private int permissionStudentID = Integer.MIN_VALUE;
    private int permissionMajor = Integer.MIN_VALUE;
    private int permissionClass = Integer.MIN_VALUE;
    private int permissionEntryYear = Integer.MIN_VALUE;
    private int permissionSchedule = Integer.MIN_VALUE;
    private int permissionGroupSchedule = Integer.MIN_VALUE;

    private static final String PRE_METTO = "metto";
    private static final String PRE_IMAGEURL = "imageurl";
    private static final String PRE_LOVESTATUS = "lovestatus";
    private static final String PRE_SEXORIENTATION = "sexorientation";
    private static final String PRE_REALNAME = "realname";
    private static final String PRE_GENDER = "gender";
    private static final String PRE_BIRTHDATE = "birthdate";
    private static final String PRE_STUDENTID = "studentid";
    private static final String PRE_INSTITUTENAME = "institutename";
    private static final String PRE_MAJORNAME = "majorname";
    private static final String PRE_CLASSID = "classid";
    private static final String PRE_ENTRYYEAR = "entryyear";
    private static final String PRE_MAIL = "mail";

    /*
     * 权限SharedPreferences
     */
    private static final String PRE_PERMISSIONLOVESTATUS = "permissionLoveStatus";
    private static final String PRE_PERMISSIONSEXORIENTATION = "permissionsexorientation";
    private static final String PRE_PERMISSIONREALNAME = "permissionrealname";
    private static final String PRE_PERMISSIONBIRTHDATE = "permissionbirthdate";
    private static final String PRE_PERMISSIONINSTITUTE = "permissioninstitute";
    private static final String PRE_PERMISSIONSTUDENTID = "permissionstudentid";
    private static final String PRE_PERMISSIONMAJOR = "permissionmajor";
    private static final String PRE_PERMISSIONCLASS = "permissionclass";
    private static final String PRE_PERMISSIONENTRYYEAR = "permissionentryyear";
    private static final String PRE_PERMISSIONSCHEDULE = "permissionschedule";
    private static final String PRE_PERMISSIONGROUPSCHEDULE = "permissiongroupschedule";

    /**
     * 获取恋爱状态 权限
     *
     * @return
     */
    public int getPermissionLoveStatus() {
        if (permissionLoveStatus == Integer.MIN_VALUE) {
            permissionLoveStatus = mSharedPreferences.getInt(
                    PRE_PERMISSIONLOVESTATUS, Integer.MIN_VALUE);
        }
        return permissionLoveStatus;
    }

    /**
     * 设置恋爱状态权限
     *
     * @param permissionLoveStatus
     */
    public void setPermissionLoveStatus(int permissionLoveStatus) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONLOVESTATUS, permissionLoveStatus)
                .commit()) {
            this.permissionLoveStatus = permissionLoveStatus;
        }
    }

    /**
     * 获取性取向权限
     *
     * @return
     */
    public int getPermissionSexOrientation() {
        if (permissionSexOrientation == Integer.MIN_VALUE) {
            permissionSexOrientation = mSharedPreferences.getInt(
                    PRE_PERMISSIONSEXORIENTATION, Integer.MIN_VALUE);
        }
        return permissionSexOrientation;
    }

    /**
     * 设置性取向权限
     *
     * @param permissionSexOrientation
     */

    public void setPermissionSexOrientation(int permissionSexOrientation) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONSEXORIENTATION,
                permissionSexOrientation).commit()) {
            this.permissionSexOrientation = permissionSexOrientation;
        }
    }

    /**
     * 获取实名权限
     *
     * @return
     */
    public int getPermissionRealName() {
        if (permissionRealName == Integer.MIN_VALUE) {
            permissionRealName = mSharedPreferences.getInt(
                    PRE_PERMISSIONREALNAME, Integer.MIN_VALUE);
        }
        return permissionRealName;
    }

    /**
     * 设置实名权限
     *
     * @param permissionRealName
     */
    public void setPermissionRealName(int permissionRealName) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONREALNAME, permissionRealName).commit()) {
            this.permissionRealName = permissionRealName;
        }
    }

    /**
     * 获取生日权限
     *
     * @return
     */
    public int getPermissionBirthDate() {
        if (permissionBirthDate == Integer.MIN_VALUE) {
            permissionBirthDate = mSharedPreferences.getInt(
                    PRE_PERMISSIONBIRTHDATE, Integer.MIN_VALUE);
        }
        return permissionBirthDate;
    }

    /**
     * 设置生日权限
     *
     * @param permissionBirthDate
     */
    public void setPermissionBirthDate(int permissionBirthDate) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONBIRTHDATE, permissionBirthDate)
                .commit()) {
            this.permissionBirthDate = permissionBirthDate;
        }
    }

    /**
     * 获取学院权限
     *
     * @return
     */
    public int getPermissionInstitute() {
        if (permissionInstitute == Integer.MIN_VALUE) {
            permissionInstitute = mSharedPreferences.getInt(
                    PRE_PERMISSIONINSTITUTE, Integer.MIN_VALUE);
        }
        return permissionInstitute;
    }

    /**
     * 设置学院状态
     *
     * @param permissionInstitute
     */
    public void setPermissionInstitute(int permissionInstitute) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONINSTITUTE, permissionInstitute)
                .commit()) {
            this.permissionInstitute = permissionInstitute;
        }
    }

    /**
     * 学号权限
     *
     * @return
     */
    public int getPermissionStudentID() {
        if (permissionStudentID == Integer.MIN_VALUE) {
            permissionStudentID = mSharedPreferences.getInt(
                    PRE_PERMISSIONSTUDENTID, Integer.MIN_VALUE);
        }
        return permissionStudentID;
    }

    /**
     * 设置学号权限
     *
     * @param permissionStudentID
     */
    public void setPermissionStudentID(int permissionStudentID) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONSTUDENTID, permissionStudentID)
                .commit()) {
            this.permissionStudentID = permissionStudentID;
        }
    }

    /**
     * 专业权限
     *
     * @return
     */
    public int getPermissionMajor() {
        if (permissionMajor == Integer.MIN_VALUE) {
            permissionMajor = mSharedPreferences.getInt(PRE_PERMISSIONMAJOR,
                    Integer.MIN_VALUE);
        }
        return permissionMajor;
    }

    /**
     * 设置专业权限
     *
     * @param permissionMajor
     */
    public void setPermissionMajor(int permissionMajor) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONMAJOR, permissionMajor).commit()) {
            this.permissionMajor = permissionMajor;
        }
    }

    /**
     * 班号权限
     *
     * @return
     */
    public int getPermissionClass() {
        if (permissionClass == Integer.MIN_VALUE) {
            permissionClass = mSharedPreferences.getInt(PRE_PERMISSIONCLASS,
                    Integer.MIN_VALUE);
        }
        return permissionClass;
    }

    /**
     * 设置班号权限
     *
     * @param permissionClass
     */
    public void setPermissionClass(int permissionClass) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONCLASS, permissionClass).commit()) {
            this.permissionClass = permissionClass;
        }
    }

    /**
     * 入学年份权限
     *
     * @return
     */
    public int getPermissionEntryYear() {
        if (permissionEntryYear == Integer.MIN_VALUE) {
            permissionEntryYear = mSharedPreferences.getInt(
                    PRE_PERMISSIONENTRYYEAR, Integer.MIN_VALUE);
        }
        return permissionEntryYear;
    }

    /**
     * 设置入学年份权限
     *
     * @param permissionEntryYear
     */
    public void setPermissionEntryYear(int permissionEntryYear) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONENTRYYEAR, permissionEntryYear)
                .commit()) {
            this.permissionEntryYear = permissionEntryYear;
        }
    }

    /**
     * 课表权限
     *
     * @return
     */
    public int getPermissionSchedule() {
        if (permissionSchedule == Integer.MIN_VALUE) {
            permissionSchedule = mSharedPreferences.getInt(
                    PRE_PERMISSIONSCHEDULE, Integer.MIN_VALUE);
        }
        return permissionSchedule;
    }

    /**
     * 设置课表权限
     *
     * @param permissionSchedule
     */
    public void setPermissionSchedule(int permissionSchedule) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONSCHEDULE, permissionSchedule).commit()) {
            this.permissionSchedule = permissionSchedule;
        }
    }

    /**
     * 群课表权限
     *
     * @return
     */
    public int getPermissionGroupSchedule() {
        if (permissionGroupSchedule == Integer.MIN_VALUE) {
            permissionGroupSchedule = mSharedPreferences.getInt(
                    PRE_PERMISSIONGROUPSCHEDULE, Integer.MIN_VALUE);
        }
        return permissionGroupSchedule;
    }

    /**
     * 设置群课表权限
     *
     * @param permissionGroupSchedule
     */
    public void setPermissionGroupSchedule(int permissionGroupSchedule) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_PERMISSIONGROUPSCHEDULE, permissionGroupSchedule)
                .commit()) {
            this.permissionGroupSchedule = permissionGroupSchedule;
        }
    }

    private UserInfo(Context context) {
        mSharedPreferences = context.getSharedPreferences("user_info",
                Context.MODE_PRIVATE);
    }

    public static UserInfo getInstance(Context context) {
        if (instance == null) {
            instance = new UserInfo(context);
        }
        return instance;
    }

    /**
     * 获取座右铭
     *
     * @return
     */
    public String getMetto() {
        if (metto == null) {
            metto = mSharedPreferences.getString(PRE_METTO, INITED_STRING);
        }
        return metto;
    }

    /**
     * 设置座右铭
     *
     * @param metto
     */
    public void setMetto(String metto) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_METTO, metto).commit()) {
            this.metto = metto;
        }
    }

    /**
     * 获取用户头像
     *
     * @return
     */
    public String getImageURL() {
        if (imageURL == null) {
            imageURL = mSharedPreferences
                    .getString(PRE_IMAGEURL, INITED_STRING);
        }
        return imageURL;
    }

    /**
     * 设置用户头像
     *
     * @param imageURL
     */
    public void setImageURL(String imageURL) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_IMAGEURL, imageURL).commit()) {
            this.imageURL = imageURL;
        }
    }

    /**
     * 恋爱状态
     *
     * @return
     */
    public int getLoveStatus() {
        if (loveStatus == Integer.MIN_VALUE) {
            loveStatus = mSharedPreferences.getInt(PRE_LOVESTATUS,
                    Integer.MIN_VALUE);
        }
        return loveStatus;
    }

    /**
     * 设置恋爱状态
     *
     * @param loveStatus
     */
    public void setLoveStatus(int loveStatus) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_LOVESTATUS, loveStatus).commit()) {
            this.loveStatus = loveStatus;
        }
    }

    /**
     * 性取向
     *
     * @return
     */
    public int getSexOrientation() {
        if (sexOrientation == Integer.MIN_VALUE) {
            sexOrientation = mSharedPreferences.getInt(PRE_SEXORIENTATION,
                    Integer.MIN_VALUE);
        }

        return sexOrientation;
    }

    /**
     * 设置性取向
     *
     * @param sexOrientation
     */
    public void setSexOrientation(int sexOrientation) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_SEXORIENTATION, sexOrientation).commit()) {
            this.sexOrientation = sexOrientation;
        }
    }

    /**
     * 获取实名
     *
     * @return
     */
    public String getRealName() {
        if (realName == null) {
            realName = mSharedPreferences
                    .getString(PRE_REALNAME, INITED_STRING);
        }
        return realName;
    }

    /**
     * 设置实名
     *
     * @param realName
     */
    public void setRealName(String realName) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_REALNAME, realName).commit()) {
            this.realName = realName;
        }
    }

    /**
     * 获取性别
     *
     * @return
     */
    public int getGender() {
        if (gender == Integer.MIN_VALUE) {
            gender = mSharedPreferences.getInt(PRE_GENDER, Integer.MIN_VALUE);
        }
        return gender;
    }

    /**
     * 设置性别
     *
     * @param gender
     */
    public void setGender(int gender) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putInt(PRE_GENDER, gender).commit()) {
            this.gender = gender;
        }
    }

    /**
     * 获取生日
     *
     * @return
     */
    public String getBirthDate() {
        if (birthDate == null) {
            birthDate = mSharedPreferences.getString(PRE_BIRTHDATE,
                    INITED_STRING);
        }
        return birthDate;
    }

    /**
     * 设置生日
     *
     * @param birthDate
     */
    public void setBirthDate(String birthDate) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_BIRTHDATE, birthDate).commit()) {
            this.birthDate = birthDate;
        }
    }

    /**
     * 获取学号
     *
     * @return
     */
    public String getStudentID() {
        if (studentID == null) {
            studentID = mSharedPreferences.getString(PRE_STUDENTID,
                    INITED_STRING);
        }
        return studentID;
    }

    /**
     * 设置学号
     *
     * @param studentID
     */
    public void setStudentID(String studentID) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_STUDENTID, studentID).commit()) {
            this.studentID = studentID;
        }
    }

    /**
     * 获取学院名称
     *
     * @return
     */
    public String getInstituteName() {
        if (instituteName == null) {
            instituteName = mSharedPreferences.getString(PRE_INSTITUTENAME,
                    INITED_STRING);
        }
        return instituteName;
    }

    /**
     * 设置学院名称
     *
     * @param instituteName
     */
    public void setInstituteName(String instituteName) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_INSTITUTENAME, instituteName).commit()) {
            this.instituteName = instituteName;
        }
    }

    /**
     * 获取专业名称
     *
     * @return
     */
    public String getMajorName() {
        if (majorName == null) {
            majorName = mSharedPreferences.getString(PRE_MAJORNAME,
                    INITED_STRING);
        }
        return majorName;
    }

    /**
     * 设置专业名称
     *
     * @param majorName
     */
    public void setMajorName(String majorName) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_MAJORNAME, majorName).commit()) {
            this.majorName = majorName;
        }
    }

    /**
     * 获取班号
     *
     * @return
     */
    public String getClassID() {
        if (classID == null) {
            classID = mSharedPreferences.getString(PRE_CLASSID, INITED_STRING);
        }
        return classID;
    }

    /**
     * 设置班号
     *
     * @param classID
     */
    public void setClassID(String classID) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_CLASSID, classID).commit()) {
            this.classID = classID;
        }
    }

    /**
     * 获取入学年份
     *
     * @return
     */
    public String getEntryYear() {
        if (entryYear == null) {
            entryYear = mSharedPreferences.getString(PRE_ENTRYYEAR,
                    INITED_STRING);
        }
        return entryYear;
    }

    /**
     * 设置入学年份
     *
     * @param entryYear
     */
    public void setEntryYear(String entryYear) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_ENTRYYEAR, entryYear).commit()) {
            this.entryYear = entryYear;
        }
    }

    /**
     * 获取email
     *
     * @return
     */
    public String getMail() {
        if (mail == null) {
            mail = mSharedPreferences.getString(PRE_MAIL, INITED_STRING);
        }
        return mail;
    }

    /**
     * 得到email
     *
     * @param mail
     */
    public void setMail(String mail) {
        Editor editor = mSharedPreferences.edit();
        if (editor.putString(PRE_MAIL, mail).commit()) {
            this.mail = mail;
        }
    }

}
