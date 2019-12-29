package example.progmob.com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import example.progmob.com.Helper.DbHelper;
import example.progmob.com.Model.Kue;
import example.progmob.com.adapter.Adapter;
import example.progmob.com.app.AppController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import example.progmob.com.data.Data;
import example.progmob.com.util.Server;



public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    String role;
    ListView list;
    SwipeRefreshLayout swipe;
    Adapter adapter;
    List<Data> itemList = new ArrayList<Data>();
    AlertDialog.Builder dialog;
    View dialogView;
    LayoutInflater inflater;
    EditText txt_id, txt_nama,txt_keterangan, txt_harga, txt_status;
    int success;
    String id, nama, keterangan, harga, status;
    Button plus, minus;
    private Context context;
    List<Kue> daftarKue = new ArrayList<Kue>();;
    ConnectivityManager conMgr;
    DbHelper SQLite = new DbHelper(getActivity());

    private static final String TAG = HomeFragment.class.getSimpleName();
    private String url_select = Server.URL + "selectKue.php";
    private String url_edit = Server.URL + "editKue.php";
    private String url_update = Server.URL + "updateKue.php";
    private String url_delete = Server.URL + "deleteKue.php";

    public static final String TAG_ID     = "id";
    public static final String TAG_NAMA     = "nama";
    public static final String TAG_KETERANGAN  = "keterangan";
    private static final String TAG_HARGA = "harga";
    private static final String TAG_STATUS = "status";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_ROLE = "role";
    public static final String TAG_GAMBAR   = "gambar";

    String tag_json_obj = "json_obj_req";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        SQLite = new DbHelper(getActivity().getApplicationContext());

        list = rootView.findViewById(R.id.list);
        swipe = rootView.findViewById(R.id.swipe_refresh_layout);

        role = getActivity().getIntent().getStringExtra(TAG_ROLE);


        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new Adapter(getActivity(), itemList);
        list.setAdapter(adapter);

        // menamilkan widget refresh
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {

                conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        swipe.setRefreshing(true);
                        itemList.clear();
                        adapter.notifyDataSetChanged();
                        SQLite.delete();
                        callVolley();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        getAllData();
                    }
                }

            }
        });

        // listview ditekan lama akan menampilkan dua pilihan edit atau delete data
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String idx = itemList.get(position).getId();

                final CharSequence[] dialogitem = {"Edit", "Delete"};
                dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                edit(idx);
                                break;
                            case 1:
                                delete(idx);
                                break;
                        }
                    }
                }).show();

                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {

        conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {

                swipe.setRefreshing(true);
                itemList.clear();
                adapter.notifyDataSetChanged();
                SQLite.delete();
                callVolley();

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
        }

    }

    // untuk menampilkan semua data pada listview
    private void callVolley(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        //membuat request JSON
        JsonArrayRequest jArr = new JsonArrayRequest(url_select, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                //parsing JSON
                for (int i = 0; i<response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Data item = new Data();

                        item.setId(obj.getString(TAG_ID));
                        item.setNama(obj.getString(TAG_NAMA));
                        item.setKeterangan(obj.getString(TAG_KETERANGAN));
                        item.setHarga(obj.getString(TAG_HARGA));
                        item.setStatus(obj.getString(TAG_STATUS));

                        if (obj.getString(TAG_GAMBAR) != "") {
                            item.setGambar(obj.getString(TAG_GAMBAR));
                        }

                        String idx = obj.getString(TAG_ID);
                        String namax = obj.getString(TAG_NAMA);
                        String keteranganx = obj.getString(TAG_KETERANGAN);
                        String hargax = obj.getString(TAG_HARGA);
                        String gambarx = obj.getString(TAG_GAMBAR);
                        String statusx = obj.getString(TAG_STATUS);

                        SQLite.insert(namax, keteranganx, hargax, gambarx, statusx );

                        //menambah item ke array
                        itemList.add(item);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                // notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });
        // menambah request ke request queue
        AppController.getInstance().addToRequestQueue(jArr);

    }

    private void getAllData() {
        ArrayList<HashMap<String, String>> row = SQLite.getAllData();

        for (int i = 0; i < row.size(); i++) {
            String id = row.get(i).get(TAG_ID);
            String nama = row.get(i).get(TAG_NAMA);
            String keterangan = row.get(i).get(TAG_KETERANGAN);
            String harga = row.get(i).get(TAG_HARGA);
            String gambar = row.get(i).get(TAG_GAMBAR);
            String status = row.get(i).get(TAG_STATUS);

            Data data = new Data();

            data.setId(id);
            data.setNama(nama);
            data.setKeterangan(keterangan);
            data.setHarga(harga);
            data.setGambar(gambar);
            data.setStatus(status);

            itemList.add(data);
        }

        adapter.notifyDataSetChanged();
    }

    private void edit(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String idx = jObj.getString(TAG_ID);
                        String namax = jObj.getString(TAG_NAMA);
                        String keteranganx = jObj.getString(TAG_KETERANGAN);
                        String hargax = jObj.getString(TAG_HARGA);



                        DialogForm(idx, namax, keteranganx, hargax, "UPDATE");

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void delete(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("delete", jObj.toString());

                        callVolley();

                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void kosong(){
        txt_id.setText(null);
        txt_nama.setText(null);
        txt_keterangan.setText(null);
        txt_harga.setText(null);
        txt_status.setText(null);
    }

    private void DialogForm(String idx, String namax, String Keteranganx, String hargax, String button){

        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_edit_kue, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.logo);
        dialog.setTitle("Form Edit Kue");

        txt_id      = dialogView.findViewById(R.id.txt_id);
        txt_nama    = dialogView.findViewById(R.id.txt_nama);
        txt_keterangan  = dialogView.findViewById(R.id.txt_keterangan);
        txt_harga   = dialogView.findViewById(R.id.txt_harga);


        if (!idx.isEmpty()){
            txt_id.setText(idx);
            txt_nama.setText(namax);
            txt_keterangan.setText(Keteranganx);
            txt_harga.setText(hargax);

        } else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                id      = txt_id.getText().toString();
                nama    = txt_nama.getText().toString();
                keterangan  = txt_keterangan.getText().toString();
                harga   = txt_harga.getText().toString();

                simpan_update();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    //fungsi untuk menyimpan update
    private void simpan_update(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_update, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("update", jObj.toString());

                        callVolley();


                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", id);
                params.put("nama_kue", nama);
                params.put("keterangan", keterangan);
                params.put("harga", harga);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
