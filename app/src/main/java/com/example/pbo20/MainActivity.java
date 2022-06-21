package com.example.cameradetectpbo;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    String sImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.buttonLoadPicture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            String imgPath = String.valueOf(imageUri);

            // Replace this with your own url
            String my_data = "Hello my First Request Without any library";// Replace this with your data
            System.out.println(imgPath);
            try{
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] b = baos.toByteArray();
                String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                new MyHttpRequestTask().execute("http:/localhost:5000/",my_data,encImage);

            }catch(IOException e){
                e.printStackTrace();
            }


        }
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String my_url = params[0];
            String my_data = params[1];
            String my_img = params[2];
            try {
                System.setProperty("java.net.preferIPv6Stack" , "true");
                URL url = new URL("http://10.0.2.2:5000/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // setting the  Request Method Type
                httpURLConnection.setRequestMethod("POST");
                // adding the headers for request
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Host", "localhost:5000");
                try{
                    //to tell the connection object that we will be wrting some data on the server and then will fetch the output result
                    httpURLConnection.setDoOutput(true);
                    // this is used for just in case we don't know about the data size associated with our request
                    //httpURLConnection.setChunkedStreamingMode(0);

                    // to write tha data in our request
                    OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    outputStreamWriter.write(my_img);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    System.out.println("======================");
                    String objectDetect = String.valueOf(readFullyAsString(httpURLConnection.getInputStream(), "UTF-8"));

                    System.out.println(objectDetect);
                    MediaPlayer mp=new MediaPlayer();
                    button.setText(objectDetect);
                    mp.setDataSource("/storage/sdcard0/uang.mp3");//Write your location here
                    mp.prepare();
                    mp.start();
                    return httpURLConnection.getResponseMessage();

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // this is done so that there are no open connections left when this task is going to complete
                    httpURLConnection.disconnect();
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
            return readFully(inputStream).toString(encoding);
        }

        private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length1 = 0;
            while ((length1 = inputStream.read(buffer)) != -1) {
                baos2.write(buffer, 0, length1);
            }
            return baos2;
        }
    }
}
