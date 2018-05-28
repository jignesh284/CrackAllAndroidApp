package myapp.jigneshmodi.com.crack_all.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import myapp.jigneshmodi.com.crack_all.Manifest;
import myapp.jigneshmodi.com.crack_all.R;

/**
 * Created by jigneshmodi on 02/11/17.
 */

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    protected FirebaseUser mFireBaseUser;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth mAuth;
    protected ProgressDialog mProgressDialog;
    protected GoogleSignInOptions mGso;
    protected Activity thisActivity;
    public int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public int MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 2;
    public int MY_PERMISSIONS_REQUEST_MEDIA_CONTOL = 3;
    public int MY_PERMISSIONS_REQUEST_INTERNET = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        Log.d("Tag","BaseActivity.onCreate()");

        thisActivity = this;
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if( mAuth != null ){
            mFireBaseUser = mAuth.getCurrentUser();
        }
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .enableAutoManage(this,this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                                .build();

    }

    protected void showProgressDialog() {
        Log.d("Tag","BaseActivity.showProgressDialog()");

        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        Log.d("Tag","BaseActivity.dismissProgressDialog()");

        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    protected void signOut() {
        Log.d("Tag","BaseActivity.signOut()");

        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {


            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Tag","BaseActivity.onConnectionFailed()");
        Toast.makeText(getApplicationContext(), "Internet Disconnected" , Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d("Tag","BaseActivity.onDestroy()");
        super.onDestroy();

        dismissProgressDialog();
    }

    public String[] mPermissions = new String[] {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.MEDIA_CONTENT_CONTROL
            };

            public void requestPermissions() {
                Log.d("Tag", "BaseActivity.requestPermissions");

                    if (ContextCompat.checkSelfPermission(thisActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            Toast.makeText(BaseActivity.this, R.string.permission_enable_msg ,Toast.LENGTH_LONG).show();

                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(thisActivity,
                                    mPermissions,
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        }
                    }

            }

        public void hideKeyboardFrom(View view) {
        Log.d("tag", "BaseActivity.hideKeyboardFrom()");
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

    }
}
