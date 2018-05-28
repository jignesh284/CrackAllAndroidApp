package myapp.jigneshmodi.com.crack_all.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import myapp.jigneshmodi.com.crack_all.R;
import myapp.jigneshmodi.com.crack_all.Utils.BaseActivity;
import myapp.jigneshmodi.com.crack_all.Utils.Constants;
import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
import myapp.jigneshmodi.com.crack_all.models.Comment;
import myapp.jigneshmodi.com.crack_all.models.Post;
import myapp.jigneshmodi.com.crack_all.models.User;

public class PostActivity extends BaseActivity implements View.OnClickListener {

    private static final String BUNDLE_COMMENT = "comment";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Tag","PostActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if(savedInstanceState != null){
            mComment = (Comment) savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }
        Intent intent = getIntent();
         mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);
        init();
        initPost();
        initCommnetSestion();
    }
    private void init() {
        Log.d("Tag","PostActivity.init()");

        mCommentEditTextView = (EditText) findViewById(R.id.etComment);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    private void initPost() {
        Log.d("Tag","PostActivity.initPost()");

        ImageView postOwnerDisplayImageView = (ImageView) findViewById(R.id.iv_PostOwnerDisplay);
        TextView postOwnerUsernameTextView = (TextView) findViewById(R.id.tv_PostUsername);
        TextView postTimeCreateTextView = (TextView) findViewById(R.id.tv_Time);
        ImageView postDisplayImageView = (ImageView) findViewById(R.id.iv_PostDisplay);
        LinearLayout postLikeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        LinearLayout postCommentLayout = (LinearLayout) findViewById(R.id.commentLayout);
        TextView postNumLikeTextView = (TextView) findViewById(R.id.tv_Likes);
        TextView postNumCommentsTextView = (TextView) findViewById(R.id.tv_Comments);
        TextView postTextTextView = (TextView) findViewById(R.id.tv_PostText);

        postOwnerUsernameTextView.setText(mPost.getUser().getName());
        postTimeCreateTextView.setText(DateUtils.getRelativeTimeSpanString(mPost.getTimeCreated()));
        postTextTextView.setText(mPost.getPostText());
        postNumLikeTextView.setText(String.valueOf(mPost.getNumLikes()));
        postNumCommentsTextView.setText(String.valueOf(mPost.getNumComments()));

        Glide.with(PostActivity.this)
                .load(mPost.getUser().getPhotoUrl())
                .into(postOwnerDisplayImageView);
        if(mPost.getPostImageUrl() != null) {
            postDisplayImageView.setVisibility(View.VISIBLE);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(mPost.getPostImageUrl());

            Glide.with(PostActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(postDisplayImageView);
        } else {
            postDisplayImageView.setImageBitmap(null);
            postDisplayImageView.setVisibility(View.GONE);
        }

    }

    private void initCommnetSestion() {
        Log.d("Tag","PostActivity.initCommnetSestion()");

        RecyclerView commentRecyclerView = (RecyclerView)findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

        FirebaseRecyclerAdapter<Comment, CommentHolder>  commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.row_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentRef(mPost.getPostId())
        ) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                Log.d("Tag","PostActivity.populateViewHolder()" + " name= " );
                 viewHolder.setUsername( model.getUser().getName() );
                 viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                 viewHolder.setComment(model.getComment());

                Glide.with(PostActivity.this)
                        .load(model.getUser().getPhotoUrl())
                        .into(viewHolder.commentOwnerDisplay);

            }
        };
        commentRecyclerView.setAdapter(commentAdapter);

    }

    @Override
    public void onClick(View v) {
        Log.d("Tag","PostActivity.onClick()");

        switch (v.getId()){
            case R.id.iv_send:
                sendComment();
                break;
        }
    }

    private void sendComment() {
        Log.d("Tag","PostActivity.sendComment()");
        final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("sending Comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mComment = new Comment();
        final String uid = FirebaseUtils.getUid();
        String strCommnet = mCommentEditTextView.getText().toString();
        mComment.setComment(strCommnet);
        mComment.setCommentId(uid);
        mComment.setTimeCreated((System.currentTimeMillis()));

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mComment.setUser(user);
                        FirebaseUtils.getCommentRef(mPost.getPostId())
                                .child(uid)
                                .setValue(mComment);


                        FirebaseUtils.getPostRef().child(mPost.getPostId())
                                .child(Constants.NUM_COMMENTS_KEY)
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long num = (long) mutableData.getValue();
                                        mutableData.setValue(num+1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                         progressDialog.dismiss();
                                        FirebaseUtils.addToMyRecord(Constants.COMMENT_KEY, uid);
                                        mCommentEditTextView.setText(null);
                                        hideKeyboardFrom(mCommentEditTextView);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView){
            super(itemView);
            Log.d("Tag","PostActivity.CommentHolderClass.CommentHolder()");
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.ivCommentOwnerDisplay);
            usernameTextView =(TextView) itemView.findViewById(R.id.tv_CommentUsername);
            timeTextView =(TextView) itemView.findViewById(R.id.tv_Time);
            commentTextView =(TextView) itemView.findViewById(R.id.tv_Comment);
        }

        public void setUsername(String username) {
            Log.d("Tag","PostActivity.CommentHolderClass.setUsername()");
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            Log.d("Tag","PostActivity.CommentHolderClass.setTime()");
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            Log.d("Tag","PostActivity.CommentHolderClass.setComment()");
            commentTextView.setText(comment);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }
}
