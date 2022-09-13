package com.example.saveit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

/**
 * This Activity can show a Photo in full Screen.
 * @author Zilin.Song
 */
public class BigPhotoActivity extends AppCompatActivity {

    private ImageView bigImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_photo);
        bigImageView = findViewById(R.id.bigImageView);
        Bundle bundle = this.getIntent().getExtras();
        String currentPhotoPath = (String) bundle.get("URI");
        File photoFile = new File (currentPhotoPath);
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.saveit",
                photoFile);
        bigImageView.setImageURI(photoURI);
    }
}