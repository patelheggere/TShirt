package tshirt.rajeev.com.tshirt;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tshirt.rajeev.com.tshirt.Helper.utility.alertD;
import static tshirt.rajeev.com.tshirt.Helper.utility.validateMobile;
import static tshirt.rajeev.com.tshirt.Helper.utility.validateName;

public class RegisterActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int CAMERA_REQUEST = 2;
    ProgressDialog progressDialog;

    private EditText signupInputName, signupInputEmail, signupInputPassword, etBoothNo;
    private Button btnSignUp;
    private Button btnUploadId, btnCancel;
    private RadioGroup genderRadioGroup;
    private Spinner spinner, spinnerBenifit;
    private SpinnerAdapter spinnerAdapter;
    private String Place = "";
    private String Benefit = "";
    private String idUrl = "";
    private Uri filePath;
    private Uri downloadUrl;
    private StorageReference storageRef, imageRef;
    private FirebaseStorage storage;
    private ImageView ivVoter;
    private String BaseURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        storage = FirebaseStorage.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        BaseURL = sharedPreferences.getString("BaseUrl", "null");
        //creates a storage reference
        storageRef = storage.getReference();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setTitle("Add Beneficiary");
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        signupInputName = (EditText) findViewById(R.id.signup_input_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        spinner = (Spinner) findViewById(R.id.spPlace);
        spinnerBenifit = (Spinner) findViewById(R.id.spBenefit);
        etBoothNo = (EditText) findViewById(R.id.etboothno);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnUploadId = (Button) findViewById(R.id.btn_upload_id);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ivVoter = (ImageView) findViewById(R.id.ivVoterImage);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUploadId.setText("Upload Voter ID");
                ivVoter.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
            }
        });

        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signupInputEmail.getText().length() != 10) {
                    signupInputEmail.setError("Enter 10 digit mobile number");
                    signupInputEmail.setFocusable(true);
                } else if (signupInputName.getText().toString().length() <= 3) {
                    signupInputName.setError("Please Enter valid name");
                    signupInputName.setFocusable(true);
                } else {
                    submitForm();
                }
            }
        });
        btnUploadId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStoragePermissionGranted2();
                isStoragePermissionGranted();
                if (btnUploadId.getText().toString().equalsIgnoreCase("Upload Voter ID")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    //showFileChooser();
                }
                else if (signupInputEmail.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
        List<String> places = new ArrayList<String>();
        places.add("Select place");
        places.add("Kudachi");
        places.add("Gundawad");
        places.add("Shiragur");
        places.add("Khemalapur");
        places.add("Siddapur");
        places.add("Paramanadawadi");
        places.add("Yalparatti");
        places.add("Yabaratti");
        places.add("Koligudda");
        places.add("Harugeri");
        places.add("Badabyakud");
        places.add("Alakhanur");
        places.add("Suttatti");
        places.add("Nilaji");
        places.add("Morab");
        places.add("Bekkeri");
        places.add("Nidagudni");
        places.add("Alagawadi");
        places.add("Bastawad");
        places.add("Khanadal");
        places.add("Itanal");
        places.add("Savasuddi");
        places.add("Devapurahatti");
        places.add("Katakabhavi");
        places.add("Hidakal");
        places.add("Mugalkhod");
        places.add("Palabhavi");
        places.add("Sultanpur");
        places.add("Kappalaguddi");
        places.add("Handigunda");
        places.add("Marakudi");

        List<String> benifitList = new ArrayList<>();
        benifitList.add("Select Benifit");
        benifitList.add("T-Shirt");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, places);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter = null;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (initializedAdapter != adapterView.getAdapter()) {
                    initializedAdapter = adapterView.getAdapter();
                    return;
                }
                Place = adapterView.getItemAtPosition(i).toString().toUpperCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayAdapter<String> mBenifitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, benifitList);
        mBenifitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBenifit.setAdapter(mBenifitAdapter);
        spinnerBenifit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter = null;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (initializedAdapter != adapterView.getAdapter()) {
                    initializedAdapter = adapterView.getAdapter();
                    return;
                }
                Benefit = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        signupInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!validateMobile(signupInputEmail.getText().toString()))
                    signupInputEmail.setError("Enter Correct 10 digit Mobile number");

            }
        });
        signupInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //if(validateName(signupInputName.getText().toString()))
                //  signupInputName.setError("Enter Valid Name");
            }
        });

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
    public  boolean isStoragePermissionGranted2() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageRef.child("votercards/" + signupInputEmail.getText().toString() + ".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            idUrl = downloadUrl.toString();
                            btnUploadId.setText("Upload Voter ID");
                            btnCancel.setVisibility(View.GONE);
                            ivVoter.setVisibility(View.GONE);

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            Log.d("hjs", "onFailure: " + exception.getLocalizedMessage());
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    private void submitForm() {
        /*if(idUrl.equalsIgnoreCase(""))
        {
            Toast.makeText(this,"Upload Voter ID ",Toast.LENGTH_SHORT).show();
        }
        else {*/
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        String gender;
        if (selectedId == R.id.female_radio_btn)
            gender = "Female";
        else
            gender = "Male";
        if (Place.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Select Village Name", Toast.LENGTH_LONG).show();
        } else if (Benefit.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Select Benefit", Toast.LENGTH_LONG).show();
        } else {
            registerUser(signupInputName.getText().toString().toUpperCase(),
                    signupInputEmail.getText().toString(),
                    "ntg",
                    gender,
                    etBoothNo.getText().toString(), Place);
        }
        //}
    }

    private void registerUser(final String name, final String email, final String password,
                              final String gender, final String dob, final String place) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        Log.d(TAG, "registerUser: " + name + " " + idUrl);

        progressDialog.setMessage("Adding you ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                BaseURL + "rajeevdistapi/register.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                       String msg = "Hi " + user + ", You are successfully Added!";
                       alertD(RegisterActivity.this, msg);
                        signupInputName.setText("");
                        signupInputEmail.setText("");
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        alertD(RegisterActivity.this, errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                alertD(RegisterActivity.this, error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                SharedPreferences settings = getSharedPreferences("prefs", 0);
                String mobile = settings.getString("mobile", "null");
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", place);
                params.put("gender", mobile);
                params.put("age", Benefit);
                params.put("url", idUrl);
                params.put("booth",etBoothNo.getText().toString());
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        //uploadbtn.setVisibility(View.VISIBLE);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+data.toString());

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            filePath = data.getData();
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            filePath = getImageUri(getApplicationContext(), photo);
            Log.d(TAG, "onActivityResult: "+filePath.toString());
            ivVoter.setImageBitmap(photo);
            ivVoter.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnUploadId.setText("Upload");
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivVoter.setImageBitmap(bitmap);
                ivVoter.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnUploadId.setText("Upload");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Place = adapterView.getItemAtPosition(i).toString();
        System.out.println("Place:" + Place);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}