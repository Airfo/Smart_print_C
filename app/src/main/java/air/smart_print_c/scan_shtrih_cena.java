package air.smart_print_c;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.aztec.encoder.Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.PendingIntent.getActivity;
import static com.google.zxing.aztec.encoder.Encoder.encode;

/**
 * Created by Air on 26.07.2017.
 */


    public class scan_shtrih_cena  extends Activity implements ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
        String TAG="QRREADER";
        SoundPool mSoundPool;
        AssetManager assets;
        int shotSound;
        public String pref="?//29";
        public String Shtrih = "";
        public String data="";
        int qrc=0;
        String server="";

        @Override
        public void onCreate(Bundle state) {
           super.onCreate(state);
           String qr="";
           server=getIntent().getStringExtra("server");
           qrc=getIntent().getIntExtra("QR",0);
           mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
           setContentView(mScannerView);                // Set the scanner view as the content view
           mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
           assets=getAssets();
           shotSound=loadSound("shot.wav");
            if(qrc==0){
                pref="?//29";
                qr="СКАНИРУЮ ШТРИХКОДЫ!";
            } else{
                pref="?//19";
                qr="СКАНИРУЮ QR-КОДЫ(DataMatrix)!";
            }
            Toast.makeText(getApplicationContext(),qr,Toast.LENGTH_SHORT).show();
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
            Shtrih= rawResult.getText()+pref;
            Thread cThread = new Thread(new scan_shtrih_cena.ClientThread());
            cThread.start();
            try {
                //задержка
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(data).setTitle("Товар")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            data="";
                            mScannerView.resumeCameraPreview(air.smart_print_c.scan_shtrih_cena.this);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();



        }

    public class ClientThread implements Runnable
    {
        public void run()
        {

            boolean k=false;
            try
            {

                int len;
                int c,l;
                String s=null;
                String result="";
                try
                {
                    Socket socket = new Socket(server, 9595);
                    OutputStream out;
                    InputStream in=socket.getInputStream();;

                    //InputStream in=new InputStream("UTF-8");
                    out=socket.getOutputStream();

                    InputStreamReader isr = new InputStreamReader(socket.getInputStream(), "UTF-8");
                    //BufferedReadereader br = new BufferedReader(isr);
                    byte buf[]=Shtrih.getBytes();
                    out.write(buf,0,buf.length);
                    out.flush();
                    byte bufr[] = new byte[64*1024];
                    //c=in.read(buf,0,buf.length);
                    //c=isr.read(bufr,0,bufr.length);
                    c=in.read(bufr);

                    data = new String(bufr, 0, c);
                    //data=new String(data.getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
                    //data= new String(data.getBytes(StandardCharsets.UTF_8), "UTF-8");
                    socket.close();



                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);}
            }
            catch (Exception e) { Log.e("ClientActivity", "C: Error", e);}

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

               //Intent myActivity = new Intent();
               //setResult(RESULT_CANCELED, myActivity);
               finish();
               return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }


