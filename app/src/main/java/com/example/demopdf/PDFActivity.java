package com.example.demopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;

public class PDFActivity extends AppCompatActivity {

    PDFView pdfView;
    String path;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfactivity);
        pdfView = findViewById(R.id.pdfView);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        file = new File(path);
        pdfView.fromFile(file).load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean delete = false;
        try {
            delete = file.getCanonicalFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(delete){
            Log.e("TAG","xoa thanh cong");
        }
    }
}