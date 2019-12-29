
package example.progmob.com;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notification);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(new Intent(SplashScreen.this,LoginActivity.class));
        finish();
      }
    },3000);
  }
}