    package myapp.jigneshmodi.com.crack_all.ui.activities;

    import android.app.Activity;
    import android.content.Intent;
    import android.support.annotation.NonNull;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;

    import com.google.android.gms.auth.api.Auth;
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
    import com.google.android.gms.auth.api.signin.GoogleSignInResult;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;

    import myapp.jigneshmodi.com.crack_all.R;
    import myapp.jigneshmodi.com.crack_all.Utils.BaseActivity;
    import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
    import myapp.jigneshmodi.com.crack_all.models.User;

    public class RegisterActivity extends BaseActivity implements View.OnClickListener {
        private Button buttonSignIn;
        private static final int RC_SIGN_IN = 9001;
        private FirebaseUser mFirebaseUser;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d("Tag","RegisterActivity.onCreate()");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            initViews();
            initListeners();
            initObjects();
        }

        private void initViews() {
            Log.d("Tag", "RegisterActivity.initViews()");

            buttonSignIn=(Button) findViewById(R.id.buttonSignIn);
        }

        private void initListeners() {
            Log.d("Tag", "RegisterActivity.initListeners()");
            buttonSignIn.setOnClickListener(this);
        }

        private void initObjects() {
            Log.d("Tag", "RegisterActivity.initObjects()");
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.buttonSignIn:
                    showProgressDialog();
                    signIn();
                    break;
            }

        }

        private void signIn(){
            Log.d("Tag", "RegisterActivity.signIn()");

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d("Tag", "RegisterActivity.onActivityResult()");


            super.onActivityResult(requestCode, resultCode, data);
            if( resultCode == Activity.RESULT_OK){
                if( requestCode == RC_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if( result.isSuccess() ){
                        GoogleSignInAccount account = result.getSignInAccount();
                        Log.d("Tag", "RegisterActivity. acccount === " + account);
                        firebaseAuthWithGoogle(account);
                    } else {  dismissProgressDialog(); }

                } else {   dismissProgressDialog(); }

            } else {  dismissProgressDialog(); }
        }

        private void firebaseAuthWithGoogle(final GoogleSignInAccount account){
            Log.d("Tag", "RegisterActivity.firebaseAuthWithGoogle()");

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if( task.isSuccessful()){
                                //Call the model
                                User user = new User();
                                String photoUrl = null;
                                if(account.getPhotoUrl() != null ) {
                                    user.setPhotoUrl(account.getPhotoUrl().toString());
                                }

                                user.setEmail(account.getEmail().toString());
                                user.setName(account.getDisplayName());
                                user.setUid(mAuth.getCurrentUser().getUid());

                                FirebaseUtils.getUserRef(account.getEmail().replace(".",","))
                                        .setValue(user, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                mFirebaseUser = mAuth.getCurrentUser();
                                                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }

                        }
                    });
        }
    }
