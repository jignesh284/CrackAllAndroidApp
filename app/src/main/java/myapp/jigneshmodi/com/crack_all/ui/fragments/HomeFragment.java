package myapp.jigneshmodi.com.crack_all.ui.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import myapp.jigneshmodi.com.crack_all.R;
import myapp.jigneshmodi.com.crack_all.Utils.Constants;
import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
import myapp.jigneshmodi.com.crack_all.ui.activities.PostActivity;
import myapp.jigneshmodi.com.crack_all.ui.activities.PostImageDisplayActivity;
import myapp.jigneshmodi.com.crack_all.ui.dialogs.PostCreateDialog;
import myapp.jigneshmodi.com.crack_all.models.Post;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private View mRootView;
    private FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;
    private RecyclerView mPostRecycleView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Tag","HomeFragment.onCreateView()");
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCreateDialog dialog = new PostCreateDialog();
                dialog.show( getFragmentManager() , null);
            }
        });
        init();
                
        return mRootView;
    }

    private void init() {
        Log.d("Tag","HomeFragment.init()");

        mPostRecycleView = (RecyclerView) mRootView.findViewById(R.id.recycleviewPost);
        mPostRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();
        mPostRecycleView.setAdapter(mPostAdapter);
    }


    private void setupAdapter() {
        Log.d("Tag","HomeFragment.setupAdapter()");

        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.row_post,
                PostHolder.class,
                FirebaseUtils.getPostRef().orderByChild(Constants.TIME_CREATED)
        ) {
            @Override
            protected void populateViewHolder(PostHolder viewHolder, final Post model, int position) {
                Log.d("Tag", "Name =" + model.getUser().getName() +"Text = "+ model.getPostText());
                viewHolder.setNumComments(String.valueOf(model.getNumComments()));
                viewHolder.setNumLikes(String.valueOf(model.getNumLikes()));
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setPostText(model.getPostText());


                    Log.d("Tag","Glider1");
                    Glide.with(getActivity())
                            .load(model.getUser().getPhotoUrl())
                            .into(viewHolder.postOwnerDisplayImageView);


                if(model.getPostImageUrl() != null ) {
                    Log.d("Tag","Glider2");

                    viewHolder.postDisplayImageView.setVisibility(View.VISIBLE);
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference(model.getPostImageUrl());

                    Glide.with(getActivity())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.postDisplayImageView);

                    viewHolder.postDisplayImageView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PostImageDisplayActivity.class);
                            intent.putExtra(Constants.POST_IMAGES, model.getPostImageUrl());
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.d("Tag","Glider2+else");

                    viewHolder.postDisplayImageView.setImageBitmap(null);
                    viewHolder.postDisplayImageView.setVisibility(View.GONE);

                }

                viewHolder.postLikeLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        onLikeClick(model.getPostId());
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);
                    }
                });


            }
        };
    }

    //******--- WARNING : Code Messed Up
    private void onLikeClick(final String postId){
        Log.d("Tag","HomeFragment.onLikeClick()");

        FirebaseUtils.getPostLikedRef(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Tag","HomeFragment.onDataChange()");
                        if(dataSnapshot.getValue() != null) {
                            //User Liked
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                                        @Override
                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                            Log.d("Tag","HomeFragment Transaction.Result doTransaction()");

                                                            long num = (long) mutableData.getValue();
                                                            mutableData.setValue(num - 1);
                                                            return Transaction.success(mutableData);
                                                        }

                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                            Log.d("Tag","HomeFragment Transaction.Result onComplete()");

                                                            FirebaseUtils.getPostLikedRef(postId)
                                                                    .setValue(null);
                                                        }
                                                    }
                                    );
                        } else {
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            Log.d("Tag","HomeFragment -else- Transaction.Result doTransaction()");

                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num+1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            Log.d("Tag","HomeFragment -else- Transaction.Result onComplete()");

                                            FirebaseUtils.getPostLikedRef(postId);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        TextView postTextTextView;
        TextView postOwnerUsernameTextView;
        LinearLayout postLikeLayout;
        TextView postNumLikesTextView;
        LinearLayout postCommentLayout;
        ImageView postDisplayImageView;
        TextView postNumCommentsTextView;
        TextView postTimeCreatedTextView;
        ImageView postOwnerDisplayImageView;

        public PostHolder(View itemView){
            super(itemView);
            postOwnerDisplayImageView= (ImageView) itemView.findViewById(R.id.iv_PostOwnerDisplay);
            postDisplayImageView= (ImageView) itemView.findViewById(R.id.iv_PostDisplay);
            postOwnerUsernameTextView= (TextView) itemView.findViewById(R.id.tv_PostUsername);
            postTextTextView= (TextView) itemView.findViewById(R.id.tv_PostText);
            postNumCommentsTextView= (TextView) itemView.findViewById(R.id.tv_Comments);
            postTimeCreatedTextView= (TextView) itemView.findViewById(R.id.tv_Time);
            postNumLikesTextView= (TextView) itemView.findViewById(R.id.tv_Likes);
            postLikeLayout = (LinearLayout) itemView.findViewById(R.id.likeLayout);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.commentLayout);

        }

        public void setUsername(String username){
            Log.d("Tag","HomeFragment.PostHolder.setUsername()");
            postOwnerUsernameTextView.setText(username);
        }
        public void setTime(CharSequence time){
            Log.d("Tag","HomeFragment.PostHolder.setTime()");

            postTimeCreatedTextView.setText(time);
        }
        public void setNumLikes(String numLikes){
            Log.d("Tag","HomeFragment.PostHolder.setNumLikes()");

            postNumLikesTextView.setText(numLikes);
        }
        public void setNumComments(String numCommenst){
            Log.d("Tag","HomeFragment.PostHolder.setNumComments()");

            postNumCommentsTextView.setText(numCommenst);
        }
        public void setPostText(String postText){
            Log.d("Tag","HomeFragment.PostHolder.setPostText()");

            postTextTextView.setText(postText);
        }
//        public void setFilter(ArrayList<Post> newList) {
//            arrayList = new ArrayList<>();
//            arrayList.addAll(newList);
//            notifyDataSetChanged();
//        }

    }

}
