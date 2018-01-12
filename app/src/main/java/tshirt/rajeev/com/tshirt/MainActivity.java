package tshirt.rajeev.com.tshirt;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static tshirt.rajeev.com.tshirt.Helper.utility.validateMobile;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //private static final String URL_FOR_LOGIN = AppConstants.BASE_URL+"login2.php";
    ProgressDialog progressDialog;
    private EditText loginInputEmail, loginInputPassword;
    private Button btnlogin;
    private Button btnLinkSignup;
    private boolean isAdmin = false;
    String url = "";
    String imageDownloadUrl = "";
    String BaseUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("prefs",0);
        BaseUrl = sharedPreferences.getString("BaseUrl", "null");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setTitle("Login Page");
        loginInputEmail = (EditText) findViewById(R.id.login_input_email);
        loginInputPassword = (EditText) findViewById(R.id.login_input_password);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnLinkSignup = (Button) findViewById(R.id.btn_link_signup);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginInputEmail.getText().toString().equalsIgnoreCase("") || loginInputEmail.getText().toString().length()!=10)
                {
                    loginInputEmail.setError("Please Enter 10 digit mobile number");
                    loginInputEmail.setFocusable(true);
                }
                else {
                    loginUser(loginInputEmail.getText().toString(),
                            loginInputPassword.getText().toString());
                }
            }
        });
        loginInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!validateMobile(loginInputEmail.getText().toString()))
                    loginInputEmail.setError("Enter Valid 10 digit number");
            }
        });

        btnLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdmin = true;
                /*Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);*/

            }
        });
    }

    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        if(isAdmin)
        {
            url = BaseUrl+AppConstants.ADMIN_LOGIN;
        }
        else {
            url = BaseUrl+AppConstants.DIST_LOGIN;
        }
        Log.d(TAG, "loginUser: "+url);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    Log.d(TAG, "onResponse: "+response.toString());
                    JSONObject jObj = new JSONObject(response.toString());
                    Log.d(TAG, "onResponse: "+jObj.toString());
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        String phone = jObj.getJSONObject("user").getString("phone");
                        imageDownloadUrl = jObj.getJSONObject("user").getString("url");
                        SharedPreferences settings=getSharedPreferences("prefs",0);
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("mobile",phone);
                        editor.putString("url",imageDownloadUrl);
                        editor.commit();
                        if(user.equalsIgnoreCase("admin"))
                        {
                            // Launch User activity
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    RegisterDistributor.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                            finish();
                        }
                        else {

                            // Launch User activity
                            Intent intent = new Intent(MainActivity.this, DistributorDetailActivity.class);
                            intent.putExtra("username", user);
                            intent.putExtra("phone",phone);
                            intent.putExtra("link",imageDownloadUrl);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d(TAG, "onResponse: "+jObj.toString());

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
