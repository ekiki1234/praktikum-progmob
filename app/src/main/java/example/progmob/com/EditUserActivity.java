package example.progmob.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;
import example.progmob.com.util.Server;

public class EditUserActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView txt_id, txt_role, namaEt, usernameEt, passEt;
    String id, role;
    ProgressDialog pDialog;
    CheckBox showPassword;
    EditText etNama, etUsername, etPassword, etConfirmPass, etHp, etAlamat, etKelamin, etRole , showEtPass, showEtCpass;
    Adapter adapter;
    Button btDaftar, btBack;
    RadioGroup rgKelamin;
    RadioButton rbKelamin;
    int success;
    ConnectivityManager conMgr;
//    List<Data> itemList = new ArrayList<Data>();

    private static final String TAG = EditUserActivity.class.getSimpleName();

    private static String url_select     = Server.URL + "editUser.php";
    private static String url_update     = Server.URL + "updateUser.php";

    public static final String TAG_ID = "id";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_HP = "no_hp";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_KELAMIN = "kelamin";
    public static final String TAG_ROLE = "role";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";


    private View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.daftarBt: openDaftar();
                    break;
                case R.id.backBt: openBack();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

//        adapter = new Adapter(EditUserActivity.this, itemList);
//        list.setAdapter(adapter);
        btDaftar = findViewById(R.id.daftarBt);
        btDaftar.setOnClickListener(ClickListener);
        btBack = findViewById(R.id.backBt);
        btBack.setOnClickListener(ClickListener);

        etNama = findViewById(R.id.namaEt);
        etUsername = findViewById(R.id.usernameEt);
        etUsername.setEnabled(false);

        etPassword = findViewById(R.id.passEt);
        etConfirmPass = findViewById(R.id.passConfirmEt);
        etHp = findViewById(R.id.hpEt);
        etAlamat = findViewById(R.id.alamatEt);
        rgKelamin = findViewById(R.id.radioGrupKelamin);

        sharedPreferences = this.getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra(TAG_ID);
        txt_id = findViewById(R.id.txt_id);
        txt_id.setText("ID : " + id);

        role = getIntent().getStringExtra(TAG_ROLE);
        txt_role = findViewById(R.id.txt_role);
        txt_role.setText("Role: " + role);

        showEtPass = findViewById(R.id.passEt);
        showEtCpass = findViewById(R.id.passConfirmEt);
        showPassword = findViewById(R.id.showPassCb);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showEtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showEtCpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }else{
                    showEtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showEtCpass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

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

        editUser();

    }

    private void kosong(){
        txt_id.setText(null);
        etNama.setText(null);
        etUsername.setText(null);
        etPassword.setText(null);
        etPassword.setText(null);
        etHp.setText(null);
        etAlamat.setText(null);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void openDaftar(){

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPass.getText().toString();
        String nama = etNama.getText().toString();
        String alamat = etAlamat.getText().toString();
        String hp = etHp.getText().toString();

        int selectedId = rgKelamin.getCheckedRadioButtonId();
        rbKelamin = findViewById(selectedId);
        String kelamin = rbKelamin.getText().toString();

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            checkUpdate(nama, username, password, confirmPassword, hp, alamat, kelamin);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBack(){

        int roleInt=Integer.parseInt(role);

        if(roleInt == 1){
            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            startActivity(intent);

        }else{
            Intent intent = new Intent(this, HomeActivity2.class);
            finish();
            startActivity(intent);
        }

    }


    private void checkUpdate(final String nama, final String username, final String password, final String confirmPassword, final String hp, final String alamat, final String kelamin) {

        id = getIntent().getStringExtra(TAG_ID);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Update ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_update, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Update Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Updated!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


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
                params.put("id", id);
                params.put("nama", nama);
                params.put("username", username);
                params.put("password", password);
                params.put("confirm_password", confirmPassword);
                params.put("hp", hp);
                params.put("alamat", alamat);
                params.put("kelamin", kelamin);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    private void editUser(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String namax = jObj.getString(TAG_NAMA);
                        String usernamex = jObj.getString(TAG_USERNAME);
                        String passwordx = jObj.getString(TAG_PASSWORD);
                        String hpx = jObj.getString(TAG_HP);
                        String alamatx = jObj.getString(TAG_ALAMAT);
                        String kelaminx =jObj.getString(TAG_KELAMIN);



//                        DialogForm(idx, namax, alamatx, "UPDATE");

                        etNama = findViewById(R.id.namaEt);
                        etUsername = findViewById(R.id.usernameEt);
                        etPassword = findViewById(R.id.passEt);
                        etHp = findViewById(R.id.hpEt);
                        etAlamat = findViewById(R.id.alamatEt);
                        RadioButton rbLaki = findViewById(R.id.radioMale);
                        RadioButton rbWanita = findViewById(R.id.radioFemale);


                        if (!id.isEmpty()){
                            etNama.setText(namax);
                            etUsername.setText(usernamex);
                            etPassword.setText(passwordx);
                            etHp.setText(hpx);
                            etAlamat.setText(alamatx);
                            if(kelaminx.equals("Laki-laki")){
                                rbLaki.setChecked(true);
                            }else{
                                rbWanita.setChecked(true);
                            }

                        }else{
                            kosong();
                        }


//                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(EditUserActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(EditUserActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })  {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }


}
