package com.example.demopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String path;
    private File file;
    private byte[] imageByteArray;
    private Button btnPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfactivity);
        pdfView = findViewById(R.id.pdfView);
        btnPrint = findViewById(R.id.btnPrint);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        imageByteArray = intent.getByteArrayExtra("imageByteArray");
        file = new File(path);
        pdfView.fromFile(file).load();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFActivity.this, PrintActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("imageByteArray",imageByteArray);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
//        boolean delete = false;
//        try {
//            delete = file.getCanonicalFile().delete();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(delete){
//            Log.e("TAG","xoa thanh cong");
//        }
    }
}