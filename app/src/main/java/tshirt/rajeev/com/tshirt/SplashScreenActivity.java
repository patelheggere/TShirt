package tshirt.rajeev.com.tshirt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tshirt.rajeev.com.tshirt.R;

public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    String Phone,Name, Place;
    private Button nextButton;
    private DatabaseReference mFirebaseRef;
    private String BaseUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        SharedPreferences settings=getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("BaseUrl","http://www.patelheggere.esy.es/");
        editor.commit();

        mFirebaseRef = firebaseDatabase.getReference().child("urladdress").child("baseurl");
        mFirebaseRef.keepSynced(true);
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tg", "onDataChange: "+dataSnapshot.getValue().toString());
                BaseUrl = dataSnapshot.getValue(String.class);
                Log.d("splas", "onDataChange: "+BaseUrl);
                SharedPreferences settings=getSharedPreferences("prefs",0);
                SharedPreferences.Editor editor=settings.edit();
                editor.putString("BaseUrl",BaseUrl);
                editor.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Tag", "onCancelled: "+databaseError.getMessage());
            }
        });
        /*nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });*/

       new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();

            }



        }, SPLASH_TIME_OUT);
    }


}
