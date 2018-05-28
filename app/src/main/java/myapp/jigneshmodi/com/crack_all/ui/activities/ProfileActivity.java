    package myapp.jigneshmodi.com.crack_all.ui.activities;

    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.net.Uri;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.Layout;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import com.bumptech.glide.Glide;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    import myapp.jigneshmodi.com.crack_all.R;
    import myapp.jigneshmodi.com.crack_all.Utils.BaseActivity;
    import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
    import myapp.jigneshmodi.com.crack_all.models.User;

    public class ProfileActivity extends BaseActivity implements View.OnClickListener {
        private Button buttonSave;
        private LinearLayout layoutSkip;
        private LinearLayout layoutSelectSubject;
        private LinearLayout layoutSelectExam;
        private TextView selectedSubjects;
        private TextView selectedExams;
        private String[] subjectList;
        private String[] examList;
        private boolean[] checkedSubjectList;
        private boolean[] checkedExamList;
        ArrayList<Integer> mUserSubjects = new ArrayList<>();
        ArrayList<Integer> mUserExams = new ArrayList<>();
        private ImageView mProfilePicture;
        private String mUserEmail;
        private DatabaseReference mUserRef;
        private ValueEventListener mUserValueEventListener;
        private User user;
        private int RC_PHOTO_PICKER = 1;
        private Uri mSelectedUri;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            init();
            initListeners();
            subjectList = getResources().getStringArray(R.array.selectSubjectList);
            examList = getResources().getStringArray(R.array.selectExamList);
            checkedExamList = new boolean[examList.length];
            checkedSubjectList = new boolean[subjectList.length];
//            if( null != user.getUserExams() ) {
//                mUserExams = user.getUserExams();
//                for(int i:mUserExams ){
//                    checkedExamList[i] =  true;
//                }
//                selectedExams.setText("Your Exams : " + arrayToText(mUserExams, examList));
//            }

//            if( null != user.getUserExams() ) {
//                mUserSubjects = user.getUserExams();
//                for(int i:mUserSubjects ){
//                    checkedSubjectList[i] =  true;
//                }
//                selectedSubjects.setText("Your Subject : " + arrayToText(mUserSubjects, subjectList));
//
//            }


        }

        private void init(){
            if( null != mAuth) {
                if( mAuth != null){
                    mFireBaseUser = mAuth.getCurrentUser();
                }
            }
            Log.d("Tag", "ProfileActivity.init()");
            buttonSave = (Button) findViewById(R.id.buttonSave);
            layoutSkip = (LinearLayout) findViewById(R.id.layoutSkip);
            layoutSelectSubject = (LinearLayout) findViewById(R.id.layoutSelectSubject);
            layoutSelectExam = (LinearLayout) findViewById(R.id.layoutSelectExam);
            selectedExams =(TextView) findViewById(R.id.tv_selectedExams);
            selectedSubjects =(TextView) findViewById(R.id.tv_selectedSubjects);
            mProfilePicture = (ImageView) findViewById(R.id.iv_profilePicture);

            mUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Tag", "MainActivity.ValueEventListener.onDataChange() "+dataSnapshot.getValue());

                    if( null !=  dataSnapshot.getValue()  ){
                        user = dataSnapshot.getValue(User.class);
                        Glide.with(ProfileActivity.this)
                                .load(user.getPhotoUrl())
                                .into(mProfilePicture);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Tag", "ProfileActivity.ValueEventListener.onCancelled()");
                }
            };

        }

        private void initListeners(){
            Log.d("Tag", "ProfileActivity.initListeners()");
            mProfilePicture.setOnClickListener(this);
            buttonSave.setOnClickListener(this);
            layoutSkip.setOnClickListener(this);
            layoutSelectExam.setOnClickListener(this);
            layoutSelectSubject.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.layoutSkip:
                    skipButtonClicked();
                    break;
                case R.id.layoutSelectExam:
                    selectExamClicked();
                    break;
                case R.id.layoutSelectSubject:
                    selectSubjectClicked();
                    break;
                case R.id.iv_profilePicture:
                    uploadProfileClicked();
                    break;
                case R.id.buttonSave:
                    saveButtonClicked();
                    break;
                default:
            }
        }

        private void skipButtonClicked() {
            Log.d("Tag", "ProfileActivity.skipButtonClicked()");
            finish();
        }



        private void selectExamClicked() {
            Log.d("Tag", "ProfileActivity.selectExamClicked()");
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
            mBuilder.setTitle("Choose your Exams");
            mBuilder.setMultiChoiceItems(examList, checkedExamList, new DialogInterface.OnMultiChoiceClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                    Integer integerPostion  = new Integer(position);
                    if(isChecked){
                        if(!mUserExams.contains(integerPostion)) {
                            mUserExams.add(integerPostion);
                        } else {
                            mUserExams.remove(integerPostion);
                        }
                    } else {
                        if(mUserExams.contains(integerPostion)) mUserExams.remove(integerPostion);
                    }
                }
            });
            mBuilder.setCancelable(true);
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    selectedExams.setText("Your Exams : " + arrayToText(mUserExams, examList));
                }
            });
            mBuilder.show();

        }

        public void selectSubjectClicked() {
            Log.d("Tag", "ProfileActivity.selectSubjectClicked()");
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
            mBuilder.setTitle("Choose your Subjects");
            mBuilder.setMultiChoiceItems(subjectList, checkedSubjectList, new DialogInterface.OnMultiChoiceClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                    Integer integerPostion  = new Integer(position);
                    if(isChecked){
                        if(!mUserSubjects.contains(integerPostion)) {
                            mUserSubjects.add(integerPostion);
                        } else {
                            mUserSubjects.remove(integerPostion);
                        }
                    } else {
                        if(mUserSubjects.contains(integerPostion)) mUserSubjects.remove(integerPostion);
                    }
                }
            });

            mBuilder.setCancelable(true);
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedSubjects.setText("Your Subject : " + arrayToText(mUserSubjects, subjectList));
                }
            });
            mBuilder.show();
        }

        private void uploadProfileClicked() {
            Log.d("Tag","PostCreateDialog.uploadProfileClicked()");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "complete action using"), RC_PHOTO_PICKER );

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == RC_PHOTO_PICKER){
                if(resultCode == RESULT_OK ){
                    mSelectedUri = data.getData();
                    mProfilePicture.setImageURI(mSelectedUri);
                }
            }
        }

        private void saveButtonClicked() {
            Log.d("Tag", "ProfileActivity.saveButtonClicked()");
            // lines to save code
                user.setUserExams(mUserExams);
                user.setUserSubjects(mUserSubjects);
            mUserRef.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Log.d("Tag", "ProfileActivity.onComplete() User Data Saved!");
                            finish();
                        }
                    });

        }

        @Override
        protected void onStart() {
            Log.d("Tag", "ProfileActivity.onStart()");
            super.onStart();

            if( mAuth != null){
                mFireBaseUser = mAuth.getCurrentUser();
            }
            mUserEmail = mFireBaseUser.getEmail().replace('.',',');
            mUserRef = FirebaseUtils.getUserRef(mUserEmail);
            mUserRef.addValueEventListener(mUserValueEventListener);

        }

        @Override
        protected void onStop() {
            super.onStop();

            if(mUserRef!=null) {
                mUserRef.removeEventListener(mUserValueEventListener);
            }
        }

        private String arrayToText(ArrayList<Integer>  checkedList, String[] completeList  ) {
            String item = "";
            for( int i = 0; i < checkedList.size(); i++ ){
                item = item + completeList[checkedList.get(i)];
                if( i != checkedList.size() - 1 ) {
                    item = item + ", ";
                }
            }
            return item;
        };

    }
