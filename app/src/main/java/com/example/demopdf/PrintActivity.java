package com.example.demopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import java.util.ArrayList;
import java.util.List;

import static com.example.demopdf.MainActivity.READ_PHONE;

public class PrintActivity extends AppCompatActivity implements PrintingCallback {

    private Button btnPrint, btnConnect, btnDisconnect;
    private Printing printing;
    private byte [] imageByte;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        btnPrint = findViewById(R.id.btnPrint);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
        }
        initListeners();
        checkPermission();
        imageByte = getIntent().getByteArrayExtra("imageByteArray");
        bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
    }

    private void initListeners() {
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Printooth.INSTANCE.hasPairedPrinter()){
                    Toast.makeText(PrintActivity.this,"Chưa kết nối thiết bị",Toast.LENGTH_SHORT).show();
                }else {
                    printDocument();
                }
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Printooth.INSTANCE.hasPairedPrinter()) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(new Intent(PrintActivity.this,
                                            ScanningActivity.class),
                                    ScanningActivity.SCANNING_FOR_PRINTER);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.BLUETOOTH}, READ_PHONE);
                        }
                    }
                } else {
                    Toast.makeText(PrintActivity.this, "Đã kết nốt thiết bị", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Printooth.INSTANCE.hasPairedPrinter()) {
                    Printooth.INSTANCE.removeCurrentPrinter();
                    Toast.makeText(PrintActivity.this, "Đã ngắt kết nối thiết bị", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrintActivity.this, "Đã ngắt kết nối thiết bị", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (printing != null) {
            printing.setPrintingCallback(this);
        }

    }

    private void printDocument() {
        ArrayList<Printable> printables = new ArrayList<>();
//        printables.add(new ImagePrintable.Builder(bmp).build());
        printables.add(new TextPrintable.Builder()
                .setText("Hello World")
                .setNewLinesAfter(1)
                .build());
        printing.print(printables);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH}, READ_PHONE);
            }
        }
    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(PrintActivity.this, "Connecting with printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(String s) {

        Toast.makeText(PrintActivity.this, "Failed to connect printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {

        Toast.makeText(PrintActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String s) {

        Toast.makeText(PrintActivity.this, "Message: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {

        Toast.makeText(PrintActivity.this, "Order sent to printer", Toast.LENGTH_SHORT).show();
    }
}