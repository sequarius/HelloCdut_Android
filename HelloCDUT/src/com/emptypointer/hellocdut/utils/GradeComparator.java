package com.emptypointer.hellocdut.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import android.util.Log;

import com.emptypointer.hellocdut.domain.GradeInfo;

/**
 * 用于成绩排序的工具类
 *
 * @author Sequarius
 */
public class GradeComparator {
    public static final String TAG = "GradeComparator";

    /**
     * 根據時間排序
     *
     * @author Sequarius
     */
    public class ComparateByTime implements Comparator<GradeInfo> {

        @Override
        public int compare(GradeInfo lhs, GradeInfo rhs) {
            // TODO Auto-generated method stub

            SimpleDateFormat formatTime = new SimpleDateFormat(
                    "yyyy/M/d HH:mm:ss");
            // SimpleDateFormat formatTerm = new SimpleDateFormat("yyyyMM");
            Long leftTime = Long.MAX_VALUE;
            try {
                Date date = formatTime.parse(lhs.getUpdateTime());
                leftTime = date.getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                // try {
                // Date date = formatTerm.parse(lhs.getSemester());
                // leftTime = date.getTime();
                // } catch (ParseException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // e.printStackTrace();
            }

            Long rightTime = Long.MAX_VALUE;
            try {
                Date date = formatTime.parse(rhs.getUpdateTime());
                rightTime = date.getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                // try {
                // Date date = formatTerm.parse(rhs.getSemester());
                // rightTime = date.getTime();
                // } catch (ParseException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // e.printStackTrace();
            }

            return leftTime - rightTime > 0 ? 1 : -1;
        }

    }

    /**
     * 按成绩排序
     *
     * @author Sequarius
     */
    public class ComparateByGrade implements Comparator<GradeInfo> {

        @Override
        public int compare(GradeInfo lhs, GradeInfo rhs) {
            // TODO Auto-generated method stub
            String strLeftscore = lhs.getScore();
            double leftScore;
            try {
                leftScore = Double.parseDouble(strLeftscore);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                switch (strLeftscore) {
                    case "优":
                        leftScore = 98;
                        break;
                    case "优+":
                        leftScore = 93;
                        break;
                    case "良":
                        leftScore = 83;
                        break;
                    case "良+":
                        leftScore = 88;
                        break;
                    case "中":
                        leftScore = 73;
                        break;
                    case "中+":
                        leftScore = 78;
                        break;
                    case "及格":
                        leftScore = 63;
                        break;
                    case "不及格":
                        leftScore = 59;
                        break;

                    default:
                        leftScore = 60;
                        break;
                }
            }

            String strRightscore = rhs.getScore();
            double rightScore;
            try {
                rightScore = Double.parseDouble(strRightscore);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                switch (strRightscore) {
                    case "优":
                        rightScore = 98;
                        break;
                    case "优+":
                        rightScore = 93;
                        break;
                    case "良":
                        rightScore = 83;
                        break;
                    case "良+":
                        rightScore = 88;
                        break;
                    case "中":
                        rightScore = 73;
                        break;
                    case "中+":
                        rightScore = 78;
                        break;
                    case "及格":
                        rightScore = 63;
                        break;
                    case "不及格":
                        rightScore = 59;
                        break;

                    default:
                        rightScore = 60;
                        break;
                }
            }
            return leftScore - rightScore > 0 ? 1 : -1;
        }

    }

    /**
     * 按学分排序
     *
     * @author Sequarius
     */
    public class ComparateByCredit implements Comparator<GradeInfo> {

        @Override
        public int compare(GradeInfo lhs, GradeInfo rhs) {
            // TODO Auto-generated method stub

            return lhs.getCredits() - rhs.getCredits() > 0 ? 1 : -1;
        }

    }

}
