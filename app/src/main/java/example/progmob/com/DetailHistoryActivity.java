package example.progmob.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.progmob.com.adapter.AdapterCart;
import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;
import example.progmob.com.util.Server;

public class DetailHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    TextView coba;
    String id_transaksi;
    String role;
    ListView list;
    SwipeRefreshLayout swipe;
    AdapterCart adapter;
    List<Data> itemList = new ArrayList<Data>();
    AlertDialog.Builder dialog;
    View dialogView;
    LayoutInflater inflater;
    TextView txt_id, txt_nama,txt_keterangan, txt_harga, txt_status;
    int success;
    String id, nama, keterangan, harga, status, id_user, jumlah, total, tgl_ambil_pesanan, totalCO, username;
    TextView grandTotal;
    String grandTotalString;
    Button checkOutBt;
    EditText jumlahEditT;
    int grandTotalInt = 0 ;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;
    TextView tvDateResult, tvTotal;
    Button btDatePicker;



    private static final String TAG = CartFragment.class.getSimpleName();
    private String url_select = Server.URL + "selectDetailHistoryUser.php";

    public static final String TAG_ID     = "id";
    public static final String TAG_ID_USER     = "id_user";
    public static final String TAG_NAMA     = "nama_kue";
    public static final String TAG_KETERANGAN  = "keterangan";
    private static final String TAG_HARGA = "harga";
    private static final String TAG_STATUS = "status";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_ROLE = "role";
    public static final String TAG_JUMLAH = "jumlah";
    public static final String TAG_TOTAL = "total";
    public static final String TAG_GAMBAR   = "gambar";
    public final static String TAG_USERNAME = "username";
    public static final String my_shared_preferences = "my_shared_preferences";

    String tag_json_obj = "json_obj_req";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);

        id_transaksi = getIntent().getStringExtra("id_transaksi");

        list = findViewById(R.id.list);
        swipe = findViewById(R.id.swipe_refresh_layout);

        grandTotal = findViewById(R.id.grandTotal);

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new AdapterCart(this, itemList);
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



    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        callVolley();

    }

    private void callVolley(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        //membuat request JSON
        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                //parsing JSON

                try {
                    JSONArray j = new JSONArray(response);
                    grandTotalInt = 0;

                    for (int i = 0; i<response.length(); i++) {

                        JSONObject obj = j.getJSONObject(i);

                        Data item = new Data();
                        item.setId(obj.getString(TAG_ID));
                        item.setNama(obj.getString(TAG_NAMA));
                        item.setHarga(obj.getString(TAG_HARGA));
                        item.setJumlah(obj.getString(TAG_JUMLAH));
                        item.setTotal(obj.getString(TAG_TOTAL));

                        if (obj.getString(TAG_GAMBAR) != "") {
                            item.setGambar(obj.getString(TAG_GAMBAR));
                        }

                        //menambah item ke array
                        itemList.add(item);

                        grandTotalInt += Integer.parseInt((obj.getString(TAG_TOTAL)));
                        grandTotalString = Integer.toString(grandTotalInt);
                        grandTotal.setText("Grand total: Rp." + grandTotalString);

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
                params.put("id_transaksi", id_transaksi);

                return params;
            }

        };
        // menambah request ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

}
