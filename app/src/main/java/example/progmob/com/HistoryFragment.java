package example.progmob.com;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import example.progmob.com.adapter.AdapterHistory;
import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;
import example.progmob.com.util.Server;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static FutureTask modelArrayList;
    String role;
    ListView list;
    SwipeRefreshLayout swipe;
    AdapterHistory adapter;
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

    private static final String TAG = HistoryFragment.class.getSimpleName();
    private String url_select_user = Server.URL + "selectHistoryUser.php";
    private String url_select = Server.URL + "selectHistory.php";
    private String url_edit = Server.URL + "editKue.php";
    private String url_cart = Server.URL + "insert2Cart.php";
    private String url_delete = Server.URL + "deleteKue.php";

    public static final String TAG_ID     = "id";
    public static final String TAG_ID_USER     = "id_user";
    public static final String TAG_NAMA     = "nama_user";
    public static final String TAG_TGL_TRANSAKSI  = "tgl_transaksi";
    private static final String TAG_TGL_AMBIL = "tgl_pemesanan";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_ROLE = "role";
    public static final String TAG_GAMBAR   = "gambar";
    public static final String TAG_STATUS = "status";
    public static final String TAG_TOTAL = "total";
    public static final String my_shared_preferences = "my_shared_preferences";

    String tag_json_obj = "json_obj_req";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        list = rootView.findViewById(R.id.list);
        swipe = rootView.findViewById(R.id.swipe_refresh_layout);

        role = getActivity().getIntent().getStringExtra(TAG_ROLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        id_user = sharedPreferences.getString(TAG_ID, "0");

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new AdapterHistory(getActivity(), itemList);
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

                final CharSequence[] dialogitem = {"Detail Pesnanan"};
                dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                detail(idx);
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
        String url;
        int roleInt=Integer.parseInt(role);
        if(roleInt == 1){
            url = url_select;
        }else{
            url= url_select_user;
        }

        //membuat request JSON
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                //parsing JSON

                try {
                    JSONArray j = new JSONArray(response);
                    for (int i = 0; i<response.length(); i++) {

                        JSONObject obj = j.getJSONObject(i);

                        Data item = new Data();
                        item.setId(obj.getString(TAG_ID));
                        item.setIdUser(obj.getString(TAG_ID_USER));
                        item.setTglTransaksi(obj.getString(TAG_TGL_TRANSAKSI));
                        item.setTglAmbil(obj.getString(TAG_TGL_AMBIL));
                        item.setNama(obj.getString(TAG_NAMA));
                        item.setTotal(obj.getString(TAG_TOTAL));
                        item.setStatus(obj.getString(TAG_STATUS));

                        //menambah item ke array
                        itemList.add(item);

                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }


                // notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);

                return params;
            }

        };
        // menambah request ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private void detail(final String idx){
        int roleInt=Integer.parseInt(role);
        if(roleInt == 1){
            Intent intent = new Intent(getActivity(), AdminOrderActivity.class);
            intent.putExtra("id_transaksi", idx);

            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), DetailHistoryActivity.class);
            intent.putExtra("id_transaksi", idx);

            startActivity(intent);

        }

    }
}
