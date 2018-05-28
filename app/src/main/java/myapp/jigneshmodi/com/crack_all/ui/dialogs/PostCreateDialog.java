package myapp.jigneshmodi.com.crack_all.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import myapp.jigneshmodi.com.crack_all.R;
import myapp.jigneshmodi.com.crack_all.Utils.Constants;
import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
import myapp.jigneshmodi.com.crack_all.models.Post;
import myapp.jigneshmodi.com.crack_all.models.User;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jigneshmodi on 05/11/17.
 */

public class PostCreateDialog extends DialogFragment implements View.OnClickListener {
    private static final int RC_PHOTO_PICKER = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private ImageView mPostDisplay;
    private View mRootView;
    private int CAMERA_INTENT_REQUEST_CODE=14;
    private String mImageFileLocation="" ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("Tag","PostCreateDialog.onCreateDialog()");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mPost = new Post();
        mProgressDialog = new ProgressDialog(getContext());
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.create_post_dialog, null);
        mPostDisplay = (ImageView) mRootView.findViewById(R.id.postDailogDisplay);
        mRootView.findViewById(R.id.postDialogSelectImageView).setOnClickListener(this);
        mRootView.findViewById(R.id.postDialogSendImageView).setOnClickListener(this);
        mPostDisplay.setOnClickListener(this);
        builder.setView(mRootView);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        Log.d("Tag","PostCreateDialog.onClick()");
        switch(v.getId()) {
            case R.id.postDialogSendImageView:
                sendPost();
                break;
            case R.id.postDialogSelectImageView:
                selectImage();
                break;
            case R.id.postDailogDisplay:
                takePhoto(mPostDisplay);
                break;

        }
    }
    private void takePhoto(View view) {
        Log.d("Tag","PostCreateDialog.takePhoto()" );
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
        String authorities = getActivity().getPackageName() + ".fileprovider";
        Uri imageUri = FileProvider.getUriForFile(getContext(),authorities, photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(callCameraApplicationIntent, CAMERA_INTENT_REQUEST_CODE);
    }

    private void sendPost() {
        Log.d("Tag","PostCreateDialog.sendPost()");
        mProgressDialog.setMessage("Sending Post...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        final String postId = FirebaseUtils.getUid();
                        TextView postDialogTextView = (TextView) mRootView.findViewById(R.id.postDialogEditText);
                        String text = postDialogTextView.getText().toString();

                        mPost.setUser(user);
                        mPost.setPostText(text);
                        mPost.setPostId(postId);
                        mPost.setNumLikes(0);
                        mPost.setNumComments(0);
                        mPost.setTimeCreated(System.currentTimeMillis());

                        if(mSelectedUri != null ){
                            FirebaseUtils.getImageSRef()
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(getActivity(),
                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = Constants.POST_IMAGES+"/"+mSelectedUri.getLastPathSegment();
                                                    mPost.setPostImageUrl(url);
                                                    addToMyPostList(postId);

                                                }
                                            });
                        } else {
                            addToMyPostList(postId);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();

                    }
                });
    }

    private void addToMyPostList(String postId) {
        Log.d("Tag","PostCreateDialog.addToMyPostList()");
        FirebaseUtils.getPostRef().child(postId).setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressDialog.dismiss();
                dismiss();
            }
        });
        FirebaseUtils.addToMyRecord(Constants.POST_KEY, postId);
    }

    private void selectImage() {
        Log.d("Tag","PostCreateDialog.selectImage()");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "complete action using"), RC_PHOTO_PICKER );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Tag","PostCreateDialog.onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER){
            if(resultCode == RESULT_OK ){
                mSelectedUri = data.getData();
                mPostDisplay.setImageURI(mSelectedUri);
            }
        }
        if(requestCode == CAMERA_INTENT_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d("Tag","PostCreateDialog.CameraActivityResult" );
            mSelectedUri = Uri.fromFile(new File(mImageFileLocation));
           // Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            //mPostDisplay.setImageBitmap(photoCapturedBitmap);
            rotateImage(setReducedImageSize());

        }
    }

    private File createImageFile() throws IOException {
        Log.d("Tag","PostCreateDialog.createImageFile()" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString();
        String imageFileName = "IMAGE_"+ timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }
    private Bitmap setReducedImageSize() {
        Log.d("Tag","PostCreateDialog.setReducedImageSize()" );

        int targetImageViewWidth = mPostDisplay.getWidth();
        int targetImageViewHeight = mPostDisplay.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;
        int scaleFactor=  Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        //Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        //mPostDisplay.setImageBitmap(photoReducedSizeBitmap);
        return BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(mImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
                default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(),bitmap.getHeight(), matrix, true );
        mPostDisplay.setImageBitmap(rotatedBitmap);
    }

}
