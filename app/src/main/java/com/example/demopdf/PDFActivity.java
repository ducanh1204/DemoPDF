package com.example.demopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFActivity extends AppCompatActivity {

    PDFView pdfView;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfactivity);
        pdfView = findViewById(R.id.pdfView);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        File file = new File(path);
        pdfView.fromFile(file).load();
    }
}