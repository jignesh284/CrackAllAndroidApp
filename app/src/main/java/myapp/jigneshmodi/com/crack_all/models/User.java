package myapp.jigneshmodi.com.crack_all.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jigneshmodi on 03/11/17.
 */

public class User implements Serializable {
    private String name;
    private String email;
    private String photoUrl;
    private String uid;
    private ArrayList<Integer> userSubjects;
    private ArrayList<Integer> userExams;
    public User(){

    }

    public User(String user, String email, String photoUrl, String uid){

        this.email =email;
        this.photoUrl = photoUrl;
        this.name = user;
        this.uid = uid;

    }

    public User(String user, String email, String photoUrl, String uid, ArrayList<Integer> userExams,ArrayList<Integer> userSubjects ){

        this.email =email;
        this.photoUrl = photoUrl;
        this.name = user;
        this.uid = uid;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserSubjects( ArrayList<Integer>  userSubjects ){
        this.userSubjects = userSubjects;
    }

    public ArrayList<Integer> getUserSubjects(){
        return this.userSubjects;
    }
    public void setUserExams( ArrayList<Integer>  userExams ){
        this.userExams = userExams;
    }
    public ArrayList<Integer> getUserExams(){
        return this.userExams;
    }
}
