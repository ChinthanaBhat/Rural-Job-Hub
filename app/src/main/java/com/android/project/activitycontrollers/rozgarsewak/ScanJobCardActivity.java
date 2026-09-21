package com.android.project.activitycontrollers.rozgarsewak;

import android.os.Build;
import android.os.Bundle;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.google.zxing.integration.android.IntentIntegrator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class ScanJobCardActivity extends AppCompatActivity

        implements ZXingScannerView.ResultHandler {


    me.dm7.barcodescanner.zxing.ZXingScannerView qrCodeScanner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanjobcard);
        initializeUIComponents();
    }

    public void initializeUIComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.MY_CAMERA_REQUEST_CODE);
                return;
            }
        }
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 500L);

        /*integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setPrompt("Scan")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(false)
                .setOrientationLocked(true)
                .setTimeout(10000)
                .initiateScan();*/

        integrator.setCaptureActivity(ScanJobCardActivity.class);


        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        //setScannerProperties();
    }

    private void setScannerProperties() {


        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);

        qrCodeScanner.setFormats(formats);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
        qrCodeScanner.setAspectTolerance(0.5f);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.MY_CAMERA_REQUEST_CODE);
                return;
            }
        }

        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);

    }

    @Override
    public void handleResult(com.google.zxing.Result result) {
        if (result != null) {
            qrCodeScanner.stopCameraPreview();
            qrCodeScanner.stopCamera();
            Toast.makeText(this, result.getText(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, ViewQRCodeDetails.class);
            i.putExtra("Details", result.getText());
            i.putExtra(Constants.ID_KEY, getIntent().getLongExtra(Constants.ID_KEY, 0));

            startActivity(i);
            finish();

        } else {
            qrCodeScanner.stopCameraPreview();
            qrCodeScanner.stopCamera();

            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

        }
    }





   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
               // Toast.makeText(this, "Activity Result No contents", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Activity Result Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            qrCodeScanner.stopCameraPreview();
            qrCodeScanner.stopCamera();

            Toast.makeText(this, "Activity Result null", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentRozgarSewak(null);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;
            case R.id.changePassword:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                android.app.AlertDialog.Builder helpDialogBuilder = new android.app.AlertDialog.Builder(this);
                helpDialogBuilder.setIcon(R.drawable.applogo);
                helpDialogBuilder.setTitle(R.string.app_name);
                helpDialogBuilder.setMessage(Constants.HELP_MESSAGE);
                helpDialogBuilder.create();
                helpDialogBuilder.show();
                return true;
            case R.id.feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.FEEDBACK_SUBJECT);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.FEEDBACK_MAILID});

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send complaint..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                }
                return true;

        }

        return false;
    }
}
