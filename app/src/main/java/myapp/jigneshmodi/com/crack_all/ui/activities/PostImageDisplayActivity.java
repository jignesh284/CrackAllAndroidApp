package myapp.jigneshmodi.com.crack_all.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import myapp.jigneshmodi.com.crack_all.R;
import myapp.jigneshmodi.com.crack_all.Utils.Constants;


public class PostImageDisplayActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivRotateClockwise;
    private ImageView ivRotateAntiClockwise;
    private ImageView ivBack;
    private ImageView ivPostDisplayImage;
    private String photoUrl;
    private StorageReference storageReference;
    private Bitmap photoBitmap;
    private float rotationAngle = 0f;

    private Matrix matrix = new Matrix();
    private float minScale =1f;
    private float maxScale =5f;
    private float imageHeight;
    private float imageWidth;
    private float imageViewHeight;
    private float imageViewWidth;
    private final static int NONE = 0;
    private final static int PAN = 1;
    private final static int ZOOM = 2;
    private int mEventState;
    private float mStartX = 0;
    private float mStartY = 0;
    private float mTransaletX = 0;
    private float mTransaletY = 0;
    private float mPreviousTransaletX = 0;
    private float mPreviousTransaletY = 0;




    private Float scale = 0.5f;
    private ScaleGestureDetector SGD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Tag","PostImageDisplayActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image_display);
        init();
        initListener();
        Intent intent = getIntent();
        Bundle extras = (Bundle) intent.getExtras();
        photoUrl = extras.getString(Constants.POST_IMAGES);
        storageReference = FirebaseStorage.getInstance().getReference(photoUrl);


         Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
               .into(ivPostDisplayImage);

        SGD = new ScaleGestureDetector(PostImageDisplayActivity.this, new ScaleListener());

        matrix.setScale(0.7f, 0.5f);
        ivPostDisplayImage.setImageMatrix(matrix);

    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("Tag", "onScale()");
            scale = scale * detector.getScaleFactor();
            scale = Math.max(minScale, Math.min(scale, maxScale));
            matrix.setScale(0.7f*scale,0.5f*scale);
            matrix.setTranslate(mTransaletX/scale, mTransaletY/scale);
            ivPostDisplayImage.setImageMatrix(matrix);
            return true;
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Tag", "onTouchEvent()");
        switch(event.getAction() & MotionEvent.ACTION_MASK ) {
            case MotionEvent.ACTION_DOWN:
                mEventState = PAN;
                mStartX = event.getX() - mPreviousTransaletX;
                mStartY = event.getY() - mPreviousTransaletY ;

                break;
            case MotionEvent.ACTION_UP:
                mEventState = NONE;
                mPreviousTransaletX = mTransaletX;
                mPreviousTransaletY = mTransaletY;
                break;
            case MotionEvent.ACTION_MOVE:
                mTransaletX = event.getX() - mStartX;
                mTransaletY = event.getY() - mStartY;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mEventState = ZOOM;
                break;

        }
        SGD.onTouchEvent(event);

        if((PAN == mEventState )){
            imageWidth = ivPostDisplayImage.getWidth();
            imageHeight= ivPostDisplayImage.getHeight();

            if((mTransaletX * -1.0) < 0 ){
                mTransaletX = 0;
            } else if((mTransaletX * -1.0) > imageWidth) {
                mTransaletX = imageWidth * -1;
            }
            if((mTransaletY * -1.0) < 0 ){
                mTransaletY = 0;
            } else if((mTransaletY * -1.0) > imageHeight) {
                mTransaletY = imageHeight* -1;
            }

            matrix.setTranslate(mTransaletX/scale, mTransaletY/scale);
            ivPostDisplayImage.setImageMatrix(matrix);

        }
        return true;
    }

    private void init(){
        Log.d("Tag","PostImageDisplayActivity.init()");
        ivRotateClockwise = (ImageView)findViewById(R.id.iv_rotate_clockwise);
        ivRotateAntiClockwise = (ImageView)findViewById(R.id.iv_rotate_anti_clockwise);
        ivBack = (ImageView)findViewById(R.id.iv_back);
        ivPostDisplayImage = (ImageView)findViewById(R.id.iv_PostDisplay);
        ivPostDisplayImage.setDrawingCacheEnabled(true);
        ivPostDisplayImage.buildDrawingCache();
    }

    private void initListener(){
        ivBack.setOnClickListener(this);
        ivRotateAntiClockwise.setOnClickListener(this);
        ivRotateClockwise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_rotate_anti_clockwise:
                rotateAntiClockwise();
                break;
            case R.id.iv_rotate_clockwise:
                rotateClockwise();
                break;
        }
    }


    private void rotateAntiClockwise() {
        Log.d("Tag","PostImageDisplayActivity.rotateAntiClockwise()");
        Bitmap bitmap=ivPostDisplayImage.getDrawingCache();
        Matrix matrix = new Matrix();
        rotationAngle =rotationAngle - 90f;
        matrix.setRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getHeight(),matrix,true);
        ivPostDisplayImage.setImageBitmap(rotatedBitmap);
    }

    private void rotateClockwise() {
        Log.d("Tag","PostImageDisplayActivity.rotateClockwise()");
        Bitmap bitmap=ivPostDisplayImage.getDrawingCache();
        Matrix matrix = new Matrix();
        rotationAngle =rotationAngle + 90f;
        matrix.setRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getHeight(),matrix,true);
        ivPostDisplayImage.setImageBitmap(rotatedBitmap);
    }




}

