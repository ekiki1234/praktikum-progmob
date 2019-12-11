package example.progmob.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static example.progmob.com.LoginActivity.TAG_ID;

public class AccFragment extends Fragment {

    Button keluarBt, editBt;
    SharedPreferences sharedPreferences;
    TextView txt_id;
    String id, nama, username, password, hp, alamat, kelamin, role;

    public static final String TAG_ID = "id";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_HP = "no_hp";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_KELAMIN = "kelamin";
    public static final String TAG_ROLE = "role";

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
}
