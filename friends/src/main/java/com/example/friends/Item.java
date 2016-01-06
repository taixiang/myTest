package com.example.friends;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taixiang on 2015/12/16.
 */
public class Item {
    private int portraitId; // 头像
    private String nickName; // 昵称
    private String content; // 说说
    private String createdAt; // 发布时间
    private List<CommentItem> comments = new ArrayList<>();
    private List<UserImage> images = new ArrayList<>();

    public int getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(int portraitId) {
        this.portraitId = portraitId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public List<UserImage> getImages() {
        return images;
    }

    public void setImages(List<UserImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Item{" +
                "portraitId=" + portraitId +
                ", nickName='" + nickName + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", comments=" + comments +
                ", images=" + images +
                '}';
    }
}
