package example.progmob.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import example.progmob.com.R;
import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;

public class AdapterHistory extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Data> items;
    ImageLoader imageLoader;

    public AdapterHistory(Activity activity, List<Data> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_history, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView id_transaksi = (TextView) convertView.findViewById(R.id.id_transkasi);
        TextView nama = (TextView) convertView.findViewById(R.id.nama);
        TextView tgl_transaksi = (TextView) convertView.findViewById(R.id.tgl_transaksi);
        TextView tgl_ambil = (TextView) convertView.findViewById(R.id.tgl_ambil);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView total = (TextView) convertView.findViewById(R.id.total);


        Data data = items.get(position);

        id.setText(data.getId());
//        id_transaksi.setText(data.getIdTransaksi());
        nama.setText(data.getNama());
        tgl_transaksi.setText("Tanggal Transaksi: " + data.getTglTransaksi());
        tgl_ambil.setText("Tanggal_Ambil:   " + data.getTglAmbil());

        int statusInt = Integer.parseInt(data.getStatus());
        if (statusInt == 0){
            status.setText("Belum dikonfirmasi");
        }else if(statusInt == 1){
            status.setText("Sudah Dikonfirmasi");
        }else if(statusInt == 2){
            status.setText("Sudah Selesai");
        }

        total.setText("Total: " + data.getTotal());

        return convertView;
    }



}
