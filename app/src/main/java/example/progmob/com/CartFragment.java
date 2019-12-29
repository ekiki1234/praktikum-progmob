package example.progmob.com;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import example.progmob.com.adapter.AdapterCart;
import example.progmob.com.app.AppController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



import example.progmob.com.data.Data;
import example.progmob.com.util.Server;



public class CartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

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
    private String url_select = Server.URL + "selectCartUser.php";
    private String url_edit = Server.URL + "editKueCart.php";
    private String url_update = Server.URL + "updateCart.php";
    private String url_delete = Server.URL + "deleteCart.php";
    private String url_moveCartData = Server.URL + "moveCartData.php";
    private String url_checkOut = Server.URL + "checkOut.php";
    private String url_deleteCartData = Server.URL + "deleteCartData.php";
    private String url_updateCartData = Server.URL + "updateCartData.php";
    private String url_notif= Server.URL + "notifPesananUser.php";

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        list = rootView.findViewById(R.id.list);
        swipe = rootView.findViewById(R.id.swipe_refresh_layout);

        grandTotal = rootView.findViewById(R.id.grandTotal);

        checkOutBt = rootView.findViewById(R.id.checkOutBt);
        checkOutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFormCheckOut("Checkout");


            }
        });


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        id_user = sharedPreferences.getString(TAG_ID, "0");
//        Toast.makeText(getActivity(), id_user, Toast.LENGTH_LONG).show();
        username = sharedPreferences.getString(TAG_USERNAME, "0");

        role = getActivity().getIntent().getStringExtra(TAG_ROLE);

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new AdapterCart(getActivity(), itemList);
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
                params.put("id_user", id_user);

                return params;
            }

        };
        // menambah request ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

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
                        String jumlahx = jObj.getString(TAG_JUMLAH);

                        DialogForm(idx, namax, keteranganx, hargax, jumlahx, "UPDATE");

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

    private void DialogFormCheckOut(String button){
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_checkout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.logo);
        dialog.setTitle("Form Edit Kue");

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvTotal = (TextView) dialogView.findViewById(R.id.tv_grandTotal);
        tvTotal.setText("Harga Total :" + grandTotalString);

        tvDateResult = (TextView) dialogView.findViewById(R.id.tv_dateresult);
        btDatePicker = (Button) dialogView.findViewById(R.id.bt_datepicker);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });


        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                tgl_ambil_pesanan = tvDateResult.getText().toString();
                totalCO = tvTotal.getText().toString();

//                moveData();
                tambahTransaksi();
//                updateCartData();
//                deleteCartData();
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

    private void tambahTransaksi(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_checkOut, new Response.Listener<String>() {

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
                        moveData();


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

                params.put("id_user", id_user);
                params.put("tgl_pemesanan", tgl_ambil_pesanan);
                params.put("total", grandTotalString);
                params.put("username", username);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);


    }

    private void moveData(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_moveCartData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Update", jObj.toString());

                        callVolley();
                        updateCartData();

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
                params.put("id_user", id_user);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteCartData(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_deleteCartData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Update", jObj.toString());

                        callVolley();
                        notif();


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
                params.put("id_user", id_user);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }


    private void updateCartData(){

        StringRequest strReq = new StringRequest(Request.Method.POST, url_updateCartData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Update", jObj.toString());

                        callVolley();
                        deleteCartData();

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
                params.put("id_user", id_user);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);


    }

    private void notif(){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_notif, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Token Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Token Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

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

    private void DialogForm(String idx, String namax, String Keteranganx, String hargax, String jumlahx, String button){

        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_edit_item_cart, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.logo);
        dialog.setTitle("Form Edit Kue");

        txt_id      = dialogView.findViewById(R.id.idTv);
        txt_nama    = dialogView.findViewById(R.id.namaTv);
        txt_keterangan  = dialogView.findViewById(R.id.keteranganTv);
        txt_harga   = dialogView.findViewById(R.id.hargaTv);
        jumlahEditT    = dialogView.findViewById(R.id.jumlahEt);

        if (!idx.isEmpty()){
            txt_id.setText(idx);
            txt_nama.setText(namax);
            txt_keterangan.setText(Keteranganx);
            txt_harga.setText(hargax);
            jumlahEditT.setText(jumlahx);

        } else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                id      = txt_id.getText().toString();
                jumlah    = jumlahEditT.getText().toString();
                harga   = txt_harga.getText().toString();
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
                params.put("jumlah", jumlah);
                params.put("total", total);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                tvDateResult.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

}
