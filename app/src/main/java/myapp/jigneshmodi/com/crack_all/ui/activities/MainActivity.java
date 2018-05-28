    package myapp.jigneshmodi.com.crack_all.ui.activities;

    import android.content.Intent;
    import android.media.Image;
    import android.os.Bundle;
    import android.support.annotation.NonNull;
    import android.support.design.widget.NavigationView;
    import android.support.v4.view.GravityCompat;
    import android.support.v4.app.Fragment;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v7.widget.SearchView;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.bumptech.glide.Glide;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    import myapp.jigneshmodi.com.crack_all.R;
    import myapp.jigneshmodi.com.crack_all.Utils.BaseActivity;
    import myapp.jigneshmodi.com.crack_all.Utils.FirebaseUtils;
    import myapp.jigneshmodi.com.crack_all.models.Post;
    import myapp.jigneshmodi.com.crack_all.models.User;
    import myapp.jigneshmodi.com.crack_all.ui.fragments.HomeFragment;

    public class MainActivity extends BaseActivity
            implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener {

        private FirebaseAuth.AuthStateListener mAuthStateListener;
        private ImageView mDisplayImageView;
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private ValueEventListener mUserValueEventListener;
        private DatabaseReference mUserRef;
        private View navHeaderView ;


        @Override
        public void onCreate(Bundle savedInstanceState) {
        Log.d("Tag", "MainActivity.onCreate()");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.d("Tag", "MainActivity.onAuthStateChanged()");

                    if(firebaseAuth.getCurrentUser() == null) {
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    }
                }

            };


            init();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            navHeaderView = navigationView.getHeaderView(0);
            initNavHeader(navHeaderView);
            requestPermissions();
        }

        private void init() {
            Log.d("Tag", "MainActivity.init()");
            if(mFireBaseUser!= null){
                mUserRef = FirebaseUtils.getUserRef(mFireBaseUser.getEmail().replace(".",","));
            }
        }
        private void initNavHeader(View view) {
            Log.d("Tag", "MainActivity.initNavHeader()");
            mDisplayImageView = (ImageView) view.findViewById(R.id.imageViewDispaly);
            mNameTextView =(TextView) view.findViewById(R.id.textViewName);
            mEmailTextView =(TextView)view.findViewById(R.id.textViewEmail);

            mUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Tag", "MainActivity.ValueEventListener.onDataChange() "+dataSnapshot.getValue());

                    if( null !=  dataSnapshot.getValue()  ){
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(MainActivity.this)
                                .load(user.getPhotoUrl())
                                .into(mDisplayImageView);

                        mNameTextView.setText(user.getName());
                        mEmailTextView.setText(user.getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Tag", "MainActivity.ValueEventListener.onCancelled()");
                }
            };

        }

        @Override
        public void onBackPressed() {
            Log.d("Tag", "MainActivity.onBackPressed()");
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            Log.d("Tag", "MainActivity.onCreateOptionsMenu()");
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.d("Tag", "MainActivity.onOptionsItemSelected()");
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            MenuItem menuItem =( MenuItem ) findViewById(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
             searchView.setOnQueryTextListener(this);

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Log.d("Tag", "MainActivity.onNavigationItemSelected()");
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_camera) {
                // Handle the camera action
            } else if (id == R.id.nav_gallery) {

            } else if (id == R.id.nav_slideshow) {

            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_signOut) {
                Log.d("Tag", "MainActivity.onnavSignoutClicked()");
                signOut();

            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        @Override
        protected void onStart() {
            Log.d("Tag", "MainActivity.onStart()");
            super.onStart();

             if( mAuth != null){
                mFireBaseUser = mAuth.getCurrentUser();
             }

            mAuth.addAuthStateListener(mAuthStateListener);
            if(mUserRef!= null){
                mUserRef.addValueEventListener(mUserValueEventListener);
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            if(mAuthStateListener != null) {
                mAuth.removeAuthStateListener(mAuthStateListener);
            }
            if(mUserRef!=null) {
                mUserRef.removeEventListener(mUserValueEventListener);
            }
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //          newText = newText.toLowerCase();
//            ArrayList<Post> newList = new ArrayList<>();
//             for(Post post : arrayList){
//                 String postText = post.getPostText().toLowerCase();
//                 if(postText.contains(newText))
//                     newList.add(post);
//             }
//             adapter.setFilter(newList);
            return true;
        }




    }
