package mg.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PostLogin extends AppCompatActivity {

    ImageView mapsBtn;
    ImageView sqliteBtn;
    ImageView qrBtn;
    ImageView downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        mapsBtn=findViewById(R.id.maps_iv);
        sqliteBtn=findViewById(R.id.sqlite_iv);
        qrBtn=findViewById(R.id.qrreader_iv);
        downloadBtn=findViewById(R.id.download_iv);

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostLogin.this,MapsTask.class);
                startActivity(intent);
            }
        });
        sqliteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostLogin.this,SQLiteTask.class);
                startActivity(intent);
            }
        });
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostLogin.this,QRTask.class);
                startActivity(intent);
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostLogin.this,DownloadTask.class);
                startActivity(intent);
            }
        });
    }
}
