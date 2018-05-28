package myapp.jigneshmodi.com.crack_all;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jigneshmodi on 03/11/17.
 */

public class CrackallApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
