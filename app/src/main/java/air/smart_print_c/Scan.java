package air.smart_print_c;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnKeyListener;
import java.util.Scanner;
import java.lang.*;
import java.net.*;
import java.io.*;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Set;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import com.google.zxing.Result;
import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.attr.data;

/**
 * Created by akhil on 28-12-16.
 */

public class Scan extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    String TAG="QRREADER";
    SoundPool mSoundPool;
    AssetManager assets;
    int shotSound,qrc=0;
    public String pref="?//11";
    public String[] ArrShtrih = new String[100];
    public int last_item;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        String sh="10x5", qr="СКАНИРУЮ ШТРИХКОДЫ";
        int param = getIntent().getIntExtra("Shablon",0);
        last_item=getIntent().getIntExtra("last",0);
        qrc=getIntent().getIntExtra("QR",0);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mSoundPool = new SoundPool(3,AudioManager.STREAM_MUSIC, 0);
        assets=getAssets();
        shotSound=loadSound("shot.wav");

        switch(param) {
            case 1:
                sh="10x5";
                if(qrc==0){
                    pref="?//21";
                    qr="СКАНИРУЮ ШТРИХКОДЫ";
                } else{
                    pref="?//11";
                    qr="СКАНИРУЮ QR КОДЫ";
                }
                break;
            case 2:
                sh="70x40";
                if(qrc==0){
                    pref="?//22";
                    qr="СКАНИРУЮ ШТРИХКОДЫ";
                } else{
                    pref="?//12";
                    qr="СКАНИРУЮ QR КОДЫ";
                }
                break;
            case 3:
                sh="80x55";
                if(qrc==0){
                    pref="?//23";
                    qr="СКАНИРУЮ ШТРИХКОДЫ";
                } else{
                    pref="?//13";
                    qr="СКАНИРУЮ QR КОДЫ";
                }
                break;
        }

        Toast.makeText(getApplicationContext(),qr + ", ВЫБРАН ШАБЛОН "+sh,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        mSoundPool.play(shotSound, 1, 1, 0, 0, 1);
        // call the alert dialog
        //Alert(rawResult);
        ArrShtrih[last_item]= rawResult.getText()+pref;
        last_item= last_item+1;
        if(last_item<100) {
            Toast.makeText(getApplicationContext(), ArrShtrih[last_item - 1], Toast.LENGTH_SHORT).show();
            try {
                //задержка
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mScannerView.resumeCameraPreview(Scan.this);
        }
        else{
            Toast.makeText(getApplicationContext(), "Достигнуто максимальное количество элементов списка(100)!", Toast.LENGTH_SHORT).show();
            Intent myActivity = new Intent();
            myActivity.putExtra("last_it",last_item);
            myActivity.putExtra("massive",ArrShtrih);
            setResult(RESULT_OK, myActivity);
            finish();
        }
    }
    private int loadSound(String fileName) {
        AssetFileDescriptor afd = null;
        try {
            afd = assets.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't load file '" + fileName + "'", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(last_item==0) {
                Intent myActivity = new Intent();
                myActivity.putExtra("last_it",last_item);
                setResult(RESULT_CANCELED, myActivity);
                finish();
            }
            else
            {
                Intent myActivity = new Intent();
                myActivity.putExtra("last_it",last_item);
                myActivity.putExtra("massive",ArrShtrih);
                setResult(RESULT_OK, myActivity);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}