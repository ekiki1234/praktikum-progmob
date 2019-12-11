package example.progmob.com.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import example.progmob.com.R;
import example.progmob.com.data.Data;

import static java.security.AccessController.getContext;

public class AdapterUser extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Data> items;
    private int mCounter = 0;

    public AdapterUser(Activity activity, List<Data> items) {
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

    public interface BtnClickListener {
        void onClick(View view, int position);

    }

    private BtnClickListener mClickListener = null;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_kue_user, null);

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView nama = (TextView) convertView.findViewById(R.id.nama);
        TextView keterangan = (TextView) convertView.findViewById(R.id.keterangan);
        TextView harga = (TextView) convertView.findViewById(R.id.harga);
        final TextView counter = convertView.findViewById(R.id.counter);

        Button plus =  convertView.findViewById(R.id.plus);
        Button minus = convertView.findViewById(R.id.minus);


        Data data = items.get(position);

        id.setText(data.getId());
        nama.setText(data.getNama());
        keterangan.setText(data.getKeterangan());
        harga.setText("Rp." + data.getHarga());

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter++;
                counter.setText(Integer.toString(mCounter));
            }
        });

        return convertView;
    }

}
