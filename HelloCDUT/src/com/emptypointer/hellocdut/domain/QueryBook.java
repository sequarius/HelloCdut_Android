package com.emptypointer.hellocdut.domain;

public class QueryBook {

    private String name;
    private String bookIndex;
    private String publish;
    private String pubTime;
    private String introduction;
    private String writer;
    private String bookCount;
    private String location;
    private String imageURL;
    private String hrefIndex;
    private String institution;
    private String bookDatabase;

    public QueryBook(String name, String bookIndex, String publish,
                     String pubTime, String introduction, String writer,
                     String bookCount, String location, String imageURL,
                     String hrefIndex, String institution, String bookDatabase) {
        super();
        this.name = name;
        this.bookIndex = bookIndex;
        this.publish = publish;
        this.pubTime = pubTime;
        this.introduction = introduction;
        this.writer = writer;
        this.bookCount = bookCount;
        this.location = location;
        this.imageURL = imageURL;
        this.hrefIndex = hrefIndex;
        this.institution = institution;
        this.bookDatabase = bookDatabase;
    }

    public String getName() {
        return name;
    }

    public String getBookIndex() {
        return bookIndex;
    }

    public String getPublish() {
        return publish;
    }

    public String getPubTime() {
        return pubTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getWriter() {
        return writer;
    }

    public String getBookCount() {
        return bookCount;
    }

    public String getLocation() {
        return location;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getHrefIndex() {
        return hrefIndex;
    }

    public String getInstitution() {
        return institution;
    }

    public String getBookDatabase() {
        return bookDatabase;
    }


}
