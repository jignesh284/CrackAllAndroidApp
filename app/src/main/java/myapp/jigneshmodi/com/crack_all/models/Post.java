package myapp.jigneshmodi.com.crack_all.models;

import java.io.Serializable;

/**
 * Created by jigneshmodi on 03/11/17.
 */

public class Post implements Serializable {
    private  User user;
    private String postText;
    private String postImageUrl;
    private String postId;
    private long numLikes;
    private long numComments;
    private long timeCreated;

    public Post(User user, String postText, String postImageUrl, String postId, long numLikes, long numComments, long timeCreated) {
        this.user = user;
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = Long.MAX_VALUE - timeCreated;
    }

    public Post() {


    }

    public User getUser() { return user;}

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getTimeCreated() {
        return Long.MAX_VALUE + timeCreated;
    }

    public void setTimeCreated(long timeCreated) { this.timeCreated = Long.MAX_VALUE - timeCreated;}
}
