package example.progmob.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailHistoryActivity extends AppCompatActivity {

    TextView coba;
    String var1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);

        var1 = getIntent().getStringExtra("id_transaksi");
        coba = findViewById(R.id.kontol);
        coba.setText(var1);



    }
}
