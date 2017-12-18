package team.birdhead.example.findinpage;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final Uri URI = Uri.parse("https://qiita.com/");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.web_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class).setData(URI));
            }
        });

        findViewById(R.id.chrome_custom_tabs).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new CustomTabsIntent.Builder().build().launchUrl(MainActivity.this, URI);
            }
        });
    }
}
