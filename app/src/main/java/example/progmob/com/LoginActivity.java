package example.progmob.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import example.progmob.com.app.AppController;
import example.progmob.com.util.Server;

public class LoginActivity extends AppCompatActivity {

    EditText password, etUsername, etpassword;
    CheckBox showPassword;
    Button btMasuk, btDaftar;
    ProgressDialog pDialog;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "login.php";

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final  String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";
    public final static String TAG_ROLE = "role";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    Boolean session = false;
    String id, username, role;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.daftarBt: openSignUp();
                    break;
                case R.id.masukBt: openSignIn();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btDaftar = findViewById(R.id.daftarBt);
        btDaftar.setOnClickListener(clickListener);
        btMasuk = findViewById(R.id.masukBt);
        btMasuk.setOnClickListener(clickListener);

        etUsername = findViewById(R.id.usernameEt);
        etpassword = findViewById(R.id.passEt);


        password = findViewById(R.id.passEt);
        showPassword = findViewById(R.id.showPassCb);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        id = sharedPreferences.getString(TAG_ID, null);
        username = sharedPreferences.getString(TAG_USERNAME, null);
        role =  sharedPreferences.getString(TAG_ROLE, null);

        if(session){
            if(role != null){
                int roleInt=Integer.parseInt(role);
                if( roleInt == 1){
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra(TAG_ID, id);
                    intent.putExtra(TAG_USERNAME, username);
                    intent.putExtra(TAG_ROLE, role);
//                Toast.makeText(this, role, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(this, HomeActivity2.class);
                    intent.putExtra(TAG_ID, id);
                    intent.putExtra(TAG_USERNAME, username);
                    intent.putExtra(TAG_ROLE, role);
                    finish();
                    startActivity(intent);

                }


//                Intent intent = new Intent(this, HomeActivity2.class);
//                intent.putExtra(TAG_ID, id);
//                intent.putExtra(TAG_USERNAME, username);
//                finish();
//                startActivity(intent);

            }

        }

    }


    public void openSignUp(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openSignIn(){

        String username = etUsername.getText().toString();
        String password = etpassword.getText().toString();

        if (username.trim().length() > 0 && password.trim().length() > 0){
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()){
                checkLogin(username, password);

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getApplicationContext(),"Kolom tidak Boleh Kosong", Toast.LENGTH_LONG).show();

        }
    }

    private void checkLogin(final String username, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        String role = jObj.getString(TAG_ROLE);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_ROLE, role);
                        editor.commit();

                        int roleInt=Integer.parseInt(role);

                        if(roleInt == 1){

                            // Memanggil main activity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra(TAG_ID, id);
                            intent.putExtra(TAG_USERNAME, username);
                            intent.putExtra(TAG_ROLE, role);
                            finish();
                            startActivity(intent);
                        }else{
                            // Memanggil main activity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity2.class);
                            intent.putExtra(TAG_ID, id);
                            intent.putExtra(TAG_USERNAME, username);
                            intent.putExtra(TAG_ROLE, role);
                            finish();
                            startActivity(intent);

                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
