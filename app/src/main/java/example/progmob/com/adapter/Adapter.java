package example.progmob.com.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import example.progmob.com.R;
import example.progmob.com.app.AppController;
import example.progmob.com.data.Data;

import java.util.List;

public class Adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Data> items;

    public Adapter(Activity activity, List<Data> items) {
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
            convertView = inflater.inflate(R.layout.list_row_kue, null);

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView nama = (TextView) convertView.findViewById(R.id.nama);
        TextView keterangan = (TextView) convertView.findViewById(R.id.keterangan);
        TextView harga = (TextView) convertView.findViewById(R.id.harga);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        Data data = items.get(position);

        id.setText(data.getId());
        nama.setText(data.getNama());
        keterangan.setText(data.getKeterangan());
        harga.setText("Rp. " + data.getHarga());

        int statusInt = Integer.parseInt(data.getStatus());
        if (statusInt == 1){
            status.setText("Status: Aktif");

        }else{
            status.setText("Status: Non-Aktif");
        }


        return convertView;
    }

}
