package example.progmob.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class RegisterActivity extends AppCompatActivity {

  EditText etPassword, etConfirmPass, etNama, etHp, etAlamat, etUsername;
  CheckBox showPassword;
  ProgressDialog pDialog;
  Button btDaftar;
  RadioGroup rgKelamin;
  RadioButton rbKelamin;

  int success;
  ConnectivityManager conMgr;

  private String url = Server.URL + "register.php";

  private static final String TAG = RegisterActivity.class.getSimpleName();

  private static final String TAG_SUCCESS = "success";
  private static final String TAG_MESSAGE = "message";

  String tag_json_obj = "json_obj_req";

  private View.OnClickListener ClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      switch(v.getId()){
        case R.id.daftarBt: openDaftar();
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    etPassword = findViewById(R.id.passEt);
    etConfirmPass = findViewById(R.id.passConfirmEt);
    showPassword = findViewById(R.id.showPassCb);

    showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
          etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        }else{
          etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
          etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

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

    btDaftar = findViewById(R.id.daftarBt);
    btDaftar.setOnClickListener(ClickListener);
    etNama = findViewById(R.id.namaEt);
    etUsername = findViewById(R.id.usernameEt);
    etPassword = findViewById(R.id.passEt);
    etConfirmPass = findViewById(R.id.passConfirmEt);
    etHp = findViewById(R.id.hpEt);
    etAlamat = findViewById(R.id.alamatEt);
    rgKelamin = findViewById(R.id.radioGrupKelamin);

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
      checkRegister(nama, username, password, confirmPassword, hp, alamat, kelamin);
    } else {
      Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
    finish();
    startActivity(intent);
  }

  private void checkRegister(final String nama, final String username, final String password, final String confirmPassword, final String hp, final String alamat, final String kelamin) {
    pDialog = new ProgressDialog(this);
    pDialog.setCancelable(false);
    pDialog.setMessage("Register ...");
    showDialog();

    StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

      @Override
      public void onResponse(String response) {
        Log.e(TAG, "Register Response: " + response.toString());
        hideDialog();

        try {
          JSONObject jObj = new JSONObject(response);
          success = jObj.getInt(TAG_SUCCESS);

          // Check for error node in json
          if (success == 1) {

            Log.e("Successfully Register!", jObj.toString());

            Toast.makeText(getApplicationContext(),
                    jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

            etNama.setText("");
            etUsername.setText("");
            etPassword.setText("");
            etConfirmPass.setText("");
            etHp.setText("");
            etAlamat.setText("");

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
}
