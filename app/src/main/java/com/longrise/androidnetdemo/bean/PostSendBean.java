package com.longrise.androidnetdemo.bean;

public class PostSendBean {

    /**
     * articleId : 234123
     * commentContent : 这是评论内容
     */

    private String articleId;
    private String commentContent;

    public PostSendBean(String articleId, String commentContent) {
        this.articleId = articleId;
        this.commentContent = commentContent;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    @Override
    public String toString() {
        return "PostSendBean{" +
                "articleId='" + articleId + '\'' +
                ", commentContent='" + commentContent + '\'' +
                '}';
    }
}
