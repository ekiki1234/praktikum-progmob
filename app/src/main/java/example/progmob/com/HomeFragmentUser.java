package example.progmob.com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import example.progmob.com.adapter.AdapterUser;
import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;
import example.progmob.com.util.Server;


public class HomeFragmentUser extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static FutureTask modelArrayList;
    String role;
    ListView list;
    SwipeRefreshLayout swipe;
    AdapterUser adapter;
    List<Data> itemList = new ArrayList<Data>();
    AlertDialog.Builder dialog;
    View dialogView;
    LayoutInflater inflater;
    TextView txt_id, txt_nama,txt_keterangan, txt_harga, txt_gambar;
    int success;
    String id, nama, keterangan, harga, jumlah, id_user, total, counter, gambar;
    Button checkOutBt;
    EditText jumlahEt;
    //    private String[] stringArray = new String[list.getCount()];

    private static final String TAG = HomeFragmentUser.class.getSimpleName();
    private String url_select = Server.URL + "selectKueUser.php";
    private String url_edit = Server.URL + "editKue.php";
    private String url_cart = Server.URL + "insert2Cart.php";
    private String url_delete = Server.URL + "deleteKue.php";

    public static final String TAG_ID     = "id";
    public static final String TAG_NAMA     = "nama";
    public static final String TAG_KETERANGAN  = "keterangan";
    private static final String TAG_HARGA = "harga";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_ROLE = "role";
    public static final String TAG_GAMBAR   = "gambar";
    public static final String my_shared_preferences = "my_shared_preferences";

    String tag_json_obj = "json_obj_req";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_user, container, false);

        list = rootView.findViewById(R.id.list);
        swipe = rootView.findViewById(R.id.swipe_refresh_layout);

        role = getActivity().getIntent().getStringExtra(TAG_ROLE);

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new AdapterUser(getActivity(), itemList);
        list.setAdapter(adapter);



        // menamilkan widget refresh
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                itemList.clear();
                adapter.notifyDataSetChanged();
                callVolley();
            }
        });

        // listview ditekan lama akan menampilkan dua pilihan edit atau delete data
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String idx = itemList.get(position).getId();

                final CharSequence[] dialogitem = {"Add"};
                dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                add(idx);
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
        itemList.clear();
        adapter.notifyDataSetChanged();
        callVolley();

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

                        if (obj.getString(TAG_GAMBAR) != "") {
                            item.setGambar(obj.getString(TAG_GAMBAR));
                        }

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

    private void add(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("get add data", jObj.toString());
                        String idx = jObj.getString(TAG_ID);
                        String namax = jObj.getString(TAG_NAMA);
                        String keteranganx = jObj.getString(TAG_KETERANGAN);
                        String hargax = jObj.getString(TAG_HARGA);
                        String gambarx = jObj.getString(TAG_GAMBAR);

                        DialogForm(idx, namax, keteranganx, hargax, gambarx, "TAMBAH");

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
    }

    private void DialogForm(String idx, String namax, String Keteranganx, String hargax, String gambarx, String button){

        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_add_item_cart, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.logo);
        dialog.setTitle("Kue");

        txt_id      = dialogView.findViewById(R.id.idTv);
        txt_nama    = dialogView.findViewById(R.id.namaTv);
        txt_keterangan  = dialogView.findViewById(R.id.keteranganTv);
        txt_harga   = dialogView.findViewById(R.id.hargaTv);
        txt_gambar = dialogView.findViewById(R.id.gambarTv);
        jumlahEt = dialogView.findViewById(R.id.jumlahEt);
        final EditText txt_jumlah = (EditText) dialogView.findViewById(R.id.jumlahEt);
        txt_jumlah.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});

        if (!idx.isEmpty()){
            txt_id.setText(idx);
            txt_nama.setText(namax);
            txt_keterangan.setText(Keteranganx);
            txt_harga.setText(hargax);
            txt_gambar.setText(gambarx);
            jumlahEt.setText("1");


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
                gambar = txt_gambar.getText().toString();
                jumlah = jumlahEt.getText().toString();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
                id_user = sharedPreferences.getString(TAG_ID, "0");
                int jumlahInt=Integer.parseInt(jumlah);
                int hargaInt =Integer.parseInt(harga);
                int totalInt = jumlahInt*hargaInt;
                total = Integer.toString(totalInt);

                simpan_update();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                kosong();
            }
        });

        dialog.show();


    }

    //fungsi untuk menyimpan update
    private void simpan_update(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_cart, new Response.Listener<String>() {

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
                        kosong();

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

                    params.put("id_kue", id);
                    params.put("id_user", id_user);
                    params.put("nama_kue", nama);
                    params.put("keterangan", keterangan);
                    params.put("harga", harga);
                    params.put("jumlah", jumlah);
                    params.put("total", total);
                    params.put("gambar", gambar);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
