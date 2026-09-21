package com.android.project.activitycontrollers.labourer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.Labourer;
import com.android.project.model.QRCode;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.android.project.utility.QRCodeHelper;
import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class GenerateQRCodeActivity extends AppCompatActivity {
    private ImageView iconIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generateqrcode);
        iconIV = findViewById(R.id.image);
    }


    public void generate(View view)
    {
        Labourer labourer = ((AppInstance)getApplicationContext()).getCurrentLabourer();

        com.android.project.model.QRCode qrCode = new QRCode();
        qrCode.setLabourerID(labourer.getLabourerID());

        Gson gson = new Gson();
        String serializeString = gson.toJson(qrCode);

        Bitmap bitmap = QRCodeHelper
                .newInstance(this)
                .setContent(serializeString)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                .setMargin(2)
                .getQRCOde();
        iconIV.setVisibility(View.VISIBLE);
        iconIV.setImageBitmap(bitmap);
        Toast.makeText(getApplicationContext(), Constants.QR_CODE_GENERATED_SUCCESSFULLY, Toast.LENGTH_LONG).show();

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.logout:
                ((AppInstance)getApplicationContext()).setCurrentLabourer(null);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;

        }
        return false;
    }
}
