package myapp.jigneshmodi.com.crack_all.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by jigneshmodi on 02/11/17.
 */

public class FirebaseUtils {
    // Creating class to craet code bit cleaner and more well managed.

    public static DatabaseReference getUserRef( String email){
        //Get Users Reference from database from its email.
        Log.i("Tag","FirebaseUtils.getUserRef()");
        return FirebaseDatabase.getInstance().getReference(Constants.USERS_KEY).child(email);
    }

    public static DatabaseReference getPostRef(){
        //Get Post Reference from database.
        Log.i("Tag","FirebaseUtils.getPostRef()");
        return FirebaseDatabase.getInstance().getReference(Constants.POST_KEY);
    }

    public static DatabaseReference getPostLikedRef( ){
        //Get Post Liked  Reference from database.
        Log.i("Tag","FirebaseUtils.getPostLikedRef()");
        return FirebaseDatabase.getInstance().getReference(Constants.POST_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postId ){
        //Get Post Liked  Reference from database from postID.
        Log.i("Tag","FirebaseUtils.getPostLikedRef()");
        return getPostRef().child(getCurrentUser().getEmail().replace(".", ",")).child(postId);
    }

    public static FirebaseUser getCurrentUser() {
        //To get the current user details from firebase authentication
        Log.i("Tag","FirebaseUtils.getCurrentUser()");

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid(){
        //To get the current user Id from firebase database
        Log.i("Tag","FirebaseUtils.getUid()");

        String path = FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/")+1 );
    }

    public static StorageReference getImageSRef(){
        //To get the Image storage reference
        Log.i("Tag","FirebaseUtils.getImageSRef()");

        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);

    }

    public static DatabaseReference getMyPostRef(){
        //To get My Post ref
        Log.i("Tag","FirebaseUtils.getMyPostRef()");

        return FirebaseDatabase.getInstance().getReference(Constants.MY_POST).child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getCommentRef( String postId) {
        //To get comment  ref from the post Id
        Log.i("Tag","FirebaseUtils.getCommentRef()");

        return FirebaseDatabase.getInstance().getReference(Constants.COMMENT_KEY).child(postId);
    }

    public static DatabaseReference getMyRecordRef() {
        //To get MyRecord  ref
        Log.i("Tag","FirebaseUtils.getMyRecordRef()");

        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD).child(getCurrentUser().getEmail().replace(".",","));
    }

    public static  void addToMyRecord( String node, final String id) {
        //To add to my record
        Log.i("Tag","FirebaseUtils.addToMyRecord()");

        getMyRecordRef().child(node).runTransaction( new Transaction.Handler(){
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ArrayList<String> myRecordCollection;
                if(mutableData.getValue() == null){
                    myRecordCollection = new ArrayList<String>(1);
                    myRecordCollection.add(id);
                } else {
                    myRecordCollection = (ArrayList<String>) mutableData.getValue();
                    myRecordCollection.add(id);

                }
                mutableData.setValue(myRecordCollection);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    //To hide keyboard from view.


}
