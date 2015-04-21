package com.emptypointer.hellocdut.domain;

public class GradeInfo {
    private String name;
    private String score;
    private String updateTime;
    private String semester;
    private String CourseID;
    private double credits;
    private String teacherName;

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getSemester() {
        return semester;
    }

    public String getCourseID() {
        return CourseID;
    }

    public double getCredits() {
        return credits;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public GradeInfo(String name, String score, String updateTime,
                     String semester, String courseID, double credits, String teacherName) {
        super();
        this.name = name;
        this.score = score;
        this.updateTime = updateTime;
        this.semester = semester;
        CourseID = courseID;
        this.credits = credits;
        this.teacherName = teacherName;
    }


}
