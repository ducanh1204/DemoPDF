package com.example.demopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import static com.example.demopdf.MainActivity.READ_PHONE;

public class PrintActivity extends AppCompatActivity {

    private Button btnPrint, btnPiarUnpair;
    private Printing printing=null;
    private byte[] imageByte;
    private static Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        btnPrint = findViewById(R.id.btnPrint);
        btnPiarUnpair = findViewById(R.id.btnPiarUnpair);

        initViews();
        initPrinter();
        checkPermission();
        initListeners();

        imageByte = getIntent().getByteArrayExtra("imageByteArray");
        bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
    }

    private void initPrinter() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
        }
        if (printing != null) {
            printing.setPrintingCallback(new PrintingCallback() {
                @Override
                public void connectingWithPrinter() {
                    Toast.makeText(PrintActivity.this, "Connecting with printer", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void printingOrderSentSuccessfully() {
                    Toast.makeText(PrintActivity.this, "Order sent to printer", Toast.LENGTH_SHORT).show();
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
            });
        }
    }

    private void initViews() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            btnPiarUnpair.setText("Un-pair " + Printooth.INSTANCE.getPairedPrinter().getName());
            if (Printooth.INSTANCE.getPairedPrinter().getName() == null) {
                btnPiarUnpair.setText("Un-pair " + Printooth.INSTANCE.getPairedPrinter().getAddress());
            }
        } else {
            btnPiarUnpair.setText("Pair with printer");
        }
    }


    private void initListeners() {
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Printooth.INSTANCE.hasPairedPrinter())
                    Toast.makeText(PrintActivity.this, "Chưa kết nối thiết bị", Toast.LENGTH_SHORT).show();
                else
                    printSomePrintable();

            }
        });
        btnPiarUnpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        unpairDevice();
                    }
                }, 1500);

                if (Printooth.INSTANCE.hasPairedPrinter()) {
                    Printooth.INSTANCE.removeCurrentPrinter();
                } else {
                    startActivityForResult(new Intent(PrintActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                }
                initViews();
            }
        });
    }

    private void printSomePrintable() {
        ArrayList<Printable> printables = getSomePrintables();
        if (printing != null) {
            printing.print(printables);
        }

    }

    private void unpairDevice() {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                }
            }
        }

    }

    private ArrayList<Printable> getSomePrintables() {
        ArrayList<Printable> printables = new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build());
//        printables.add(new TextPrintable.Builder()
//                .setText(" Hello World ")
//                .setNewLinesAfter(1)
//                .build());
        printables.add(new ImagePrintable.Builder(bmp).build());
        return printables;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, READ_PHONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            initPrinter();
        initViews();
    }

}