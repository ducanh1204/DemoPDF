package com.example.demopdf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnPDFActivity;
    private Display mDisplay;
    private String path;
    private int totalHeight;
    private int totalWidth;
    public static final int READ_PHONE = 110;
    private String file_name = "Screenshot";
    private View view;
    private NestedScrollView nestedScrollView;
    private Bitmap bitmap;
    private byte [] imageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPDFActivity = findViewById(R.id.btnPDFActivity);
        view = findViewById(R.id.ticket);
        nestedScrollView = findViewById(R.id.ticket);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PHONE);
            }
        }
        btnPDFActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenShot();
                Bitmap bitmapa = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapa);
                nestedScrollView.draw(canvas);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapa.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageByteArray = stream.toByteArray();
                Intent intent = new Intent(MainActivity.this, PDFActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("imageByteArray",imageByteArray);
                startActivity(intent);
            }
        });
    }


    public Bitmap getBitmapFromView(View view) {
        totalWidth = nestedScrollView.getChildAt(0).getWidth();
        totalHeight = nestedScrollView.getChildAt(0).getHeight();
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void takeScreenShot() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/FilePdfs/");
        if (!folder.exists()) {
            boolean success = folder.mkdir();
        }
        path = folder.getAbsolutePath();
        path = path + "/" + file_name + System.currentTimeMillis() + ".pdf";
        bitmap = getBitmapFromView(view);
        createPdf();
    }

    private void createPdf() {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawPaint(paint);
        Bitmap bitmap = Bitmap.createScaledBitmap(this.bitmap, this.bitmap.getWidth(), this.bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File filePath = new File(path);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        document.close();
    }
}

