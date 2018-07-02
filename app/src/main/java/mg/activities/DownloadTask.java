package mg.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.ProcessedData;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.internal.InternalTokenProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.sql.Connection;

public class DownloadTask extends AppCompatActivity {
    ImageView downloadBtn;
    EditText editText;
    String filename;
    ProgressDialog progressDialog;
    String link;
    public static final int progress_bar_type=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_task);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();



        editText=findViewById(R.id.url_editText);
        downloadBtn=findViewById(R.id.url_downloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    link=editText.getText().toString();
                    URL url=new URL(link);
                    filename= URLDecoder.decode(link,"UTF-8");
                    filename=filename.substring(filename.lastIndexOf('/'));
                    System.out.println(filename);

                    progressDialog=new ProgressDialog(DownloadTask.this);
                    progressDialog.setCancelable(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setMessage("Downloading...");
                    progressDialog.setMax(100);

                    String writePermission= Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    String readPermission= Manifest.permission.READ_EXTERNAL_STORAGE;

                    if(checkCallingOrSelfPermission(writePermission)==PackageManager.PERMISSION_GRANTED &&
                            checkCallingOrSelfPermission(readPermission)==PackageManager.PERMISSION_GRANTED){
                        new DownloadURL().execute(url);
                    }
                    else{
                        Toast.makeText(DownloadTask.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },0);

                    }



                } catch (Exception e) {
                    Toast.makeText(DownloadTask.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public class DownloadURL extends AsyncTask<URL,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String url) {
            progressDialog.dismiss();

            String ext=filename.substring(filename.lastIndexOf('.')+1);
            System.out.println("EXT-------->"+ext);
            Uri uri=Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+filename));
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri,MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected String doInBackground(URL... urls) {
            int count;
            try{
                URLConnection connection=urls[0].openConnection();
                int size=connection.getContentLength();
                InputStream inputStream=new BufferedInputStream(urls[0].openStream(),8192);
                OutputStream outputStream=new FileOutputStream(Environment.getExternalStorageDirectory().toString()+filename);
                byte[] data=new byte[1024];
                long total=0;
                while((count=inputStream.read(data))!=-1){
                    total+=count;
                    publishProgress(""+(int)(total*100/size));
                    outputStream.write(data,0,count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();


            } catch (Exception e){
                System.out.println(e.getMessage());
                //Toast.makeText(DownloadTask.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            return null;
        }
    }

}
