package com.example.friends;

/**
 * Created by taixiang on 2015/12/30.
 */
public class CommentItem {
    private String name;
    private String toName;
    private String comment;

    public CommentItem() {
    }

    public CommentItem(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public CommentItem(String name, String toName, String comment) {
        this.name = name;
        this.toName = toName;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CommentItem{" +
                "name='" + name + '\'' +
                ", toName='" + toName + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
