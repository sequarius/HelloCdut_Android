package com.emptypointer.hellocdut.domain;

import com.easemob.chat.EMContact;

public class User extends EMContact {
    private int unreadMsgCount;
    private String header;
    private String motto;
    private String imageURL;
    private String nicKName;
    private String note;


    @Override
    public String getNick() {
        // TODO Auto-generated method stub
        return nicKName;
    }


    public User() {
        super();
    }

    public User(String userName) {
        super(userName);
        // TODO Auto-generated constructor stub
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String metto) {
        this.motto = metto;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNicKName() {
        return nicKName;
    }


    public void setNicKName(String nicKName) {
        this.nicKName = nicKName;
        super.setNick(nicKName);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getNicKName().equals(((User) o).getNicKName());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}