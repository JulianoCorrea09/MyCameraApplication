package com.example.mycameraapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView imageViewCamera;
    Button buttonCamera;
    static int CAMERA_INTENT_CODE = 101;
    static int CAMERA_PERMISION_CODE = 201;

    String picturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewCamera = findViewById(R.id.imageViewCamera);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestCameraPermission();
                }else{
                    sendCameraIntent();

                }

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestCameraPermission(){
        if(getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)){
            if(checkSelfPermission(Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                    Manifest.permission.CAMERA
                },CAMERA_PERMISION_CODE);
            }else{
                sendCameraIntent();
            }
        }else{
            Toast.makeText(MainActivity.this,
                    "No camera available",
                    Toast.LENGTH_LONG
                    ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendCameraIntent();
            }else{
                Toast.makeText(MainActivity.this,
                        "Camera permission denied",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    void sendCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION,true);
        if(intent.resolveActivity(getPackageManager())!=null){
            File pictureFile = null;
            try {
                String timeStamp = new
                        SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date());
                String picName = "pic_" + timeStamp;
                File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                pictureFile = File.createTempFile(picName, ".jpg", dir);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this,
                        "Photo file could not be created",
                        Toast.LENGTH_LONG
                        ).show();
            }
            if(pictureFile != null){
                picturePath = pictureFile.getAbsolutePath();
                Uri photoURI = FileProvider.
                        getUriForFile(MainActivity.this,
                                "com.example.mycameraapplication.fileprovider",
                                pictureFile
                        );
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(intent,CAMERA_INTENT_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_INTENT_CODE){
            if(resultCode != RESULT_OK){
                Toast.makeText(MainActivity.this,
                        "Problems getting the image from the camera",
                        Toast.LENGTH_LONG)
                        .show();
            }else{
                File file = new File(picturePath);
                if(file.exists()){
                    imageViewCamera.setImageURI(Uri.fromFile(file));
                }
            }
        }
    }
}
