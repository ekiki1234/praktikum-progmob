package example.progmob.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import example.progmob.com.app.AppController;
import example.progmob.com.util.Server;

public class AccFragment extends Fragment {

    Button keluarBt, editBt;
    SharedPreferences sharedPreferences;
    TextView txt_id;
    String id, nama, username, password, hp, alamat, kelamin, role;
    int success;

    private String url_token = Server.URL + "deleteToken.php";

    private static final String TAG = AccFragment.class.getSimpleName();

    public static final String TAG_ID = "id";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_HP = "no_hp";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_KELAMIN = "kelamin";
    public static final String TAG_ROLE = "role";
    private static final  String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.keluarBt: openSignOut();
                break;
                case R.id.editBt: openEdit();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_acc, container, false);
        keluarBt = rootView.findViewById(R.id.keluarBt);
        keluarBt.setOnClickListener(clickListener);
        editBt = rootView.findViewById(R.id.editBt);
        editBt.setOnClickListener(clickListener);


        sharedPreferences = getActivity().getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        id = getActivity().getIntent().getStringExtra(TAG_ID);
        role = getActivity().getIntent().getStringExtra(TAG_ROLE);

        txt_id = rootView.findViewById(R.id.txt_id);
        txt_id.setText("ID : " + id);






        return rootView;
    }

    private void openSignOut(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LoginActivity.session_status, false);
        editor.putString(TAG_ID, null);
        editor.putString(TAG_USERNAME, null);
        editor.commit();

        deleteToken();

        Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    private void openEdit(){
        Intent intent = new Intent(getActivity().getApplicationContext(), EditUserActivity.class);
        intent.putExtra(TAG_ID, id);
        intent.putExtra(TAG_ROLE, role);

        getActivity().finish();
        startActivity(intent);
    }

    private void deleteToken(){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_token, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Token Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        String role = jObj.getString(TAG_ROLE);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
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
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("token", FirebaseInstanceId.getInstance().getToken());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
