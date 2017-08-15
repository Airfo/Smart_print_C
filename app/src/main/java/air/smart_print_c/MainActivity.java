package air.smart_print_c;

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
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
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



import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity{
    //public class MainActivity extends AppCompatActivity {
    private ZXingScannerView zXingScannerView;
    final String FILENAME = "IP_server";
    final String LOG_TAG = "myLogs";
    public String[] message = new String[100];
    public String server;
    public int port = 9595;

    public Socket mSocket = null;
    //последний элемент массива
    public int last_item=0;
    Button Add_element_but;
    Button clear_button;
    Button Scan_cena;
    Button send_button;
    Button Scanner_button;
    Button Delete_button;
    RadioButton sh1_radioButton;
    RadioButton sh2_radioButton;
    RadioButton sh3_radioButton;
    RadioButton sht_radioButton;
    RadioButton qr_radioButton;

    public String pref="?//29";
    EditText Art_editText;
    EditText Shtrih_editText;
    ListView list_print;
    TextView label_server_ip;
    public ArrayList<String> Element = new ArrayList<String>();
    public ArrayAdapter<String> adapter;
    //Просто заглушка для проверки возможности удаления элемента списка, это значение не позволяет удалять
    public int PositionToRemove=1000;
   // SoundPool mSoundPool;
   //AssetManager assets;
    //int shotSound;
   private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                server = str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            server = "10.17.1.3";
        } catch (IOException e) {
            e.printStackTrace();
            server = "10.17.1.3";
        }




        Intent intent = new Intent(this, activity_set_ip.class);

        Add_element_but = (Button) findViewById(R.id.Add_element_button);
        Scan_cena = (Button) findViewById(R.id.Button_scan_cena);
        clear_button = (Button) findViewById(R.id.clear_button);
        send_button = (Button) findViewById(R.id.send_button);
        Scanner_button = (Button) findViewById(R.id.Scanner_button);
        Delete_button = (Button) findViewById(R.id.delete_button);
        label_server_ip = (TextView) findViewById(R.id.label_server_ip);
        sh1_radioButton = (RadioButton) findViewById(R.id.sh1_radioButton);
        sh2_radioButton = (RadioButton) findViewById(R.id.sh2_radioButton);
        sh3_radioButton = (RadioButton) findViewById(R.id.sh3_radioButton);
        sht_radioButton = (RadioButton) findViewById(R.id.sht_radioButton);
        qr_radioButton = (RadioButton) findViewById(R.id.qr_radioButton);
        Art_editText = (EditText) findViewById(R.id.Art_editText);
        Shtrih_editText = (EditText) findViewById(R.id.Shtrih_editText);
        list_print = (ListView) findViewById(R.id.list_print_listView);
        label_server_ip.setText(server);

        //Scan_cena.setEnabled(false);

        adapter = new ArrayAdapter<String>(this, R.layout.list_custom, R.id.list_print_listView, Element);
        list_print.setAdapter(adapter);




        //Обработка нажатия кнопки добавления элемента в список

        final View.OnClickListener Add_element = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Art_editText.getText().toString().trim().isEmpty() && Shtrih_editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Введите или артикул или штрихкод!", Toast.LENGTH_SHORT).show();

                } else if ((Art_editText.getText().toString().trim().length() > 0) && (Shtrih_editText.getText().toString().trim().length() > 0)) {
                    Toast.makeText(getApplicationContext(), "Или артикул или штрихкод (вместе нельзя)!", Toast.LENGTH_SHORT).show();
                } else if (last_item >= 100) {
                    Toast.makeText(getApplicationContext(), "Достигнуто максимальное количество элементов списка(100)!", Toast.LENGTH_SHORT).show();
                } else if (Art_editText.getText().toString().trim().length() > 0) {
                    //Введен артикул, добавляем
                    if (sh1_radioButton.isChecked()) {
                        Element.add(0, Art_editText.getText().toString() + "?//11");
                        adapter.notifyDataSetChanged();
                        Art_editText.setText("");
                        Add_same();
                    } else if (sh2_radioButton.isChecked()) {

                        Element.add(0, Art_editText.getText().toString() + "?//12");
                        adapter.notifyDataSetChanged();
                        Art_editText.setText("");
                        Add_same();
                    } else if (sh3_radioButton.isChecked()) {
                        Element.add(0, Art_editText.getText().toString() + "?//13");
                        adapter.notifyDataSetChanged();
                        Art_editText.setText("");
                        Add_same();
                    }
                } else if (Shtrih_editText.getText().toString().trim().length() > 0) {
                    //Введен штрихкод, добавляем
                    if (sh1_radioButton.isChecked()) {
                        Element.add(0, Shtrih_editText.getText().toString() + "?//21");
                        adapter.notifyDataSetChanged();
                        Shtrih_editText.setText("");
                        Add_same();
                    } else if (sh2_radioButton.isChecked()) {
                        Element.add(0, Shtrih_editText.getText().toString() + "?//22");
                        adapter.notifyDataSetChanged();
                        Shtrih_editText.setText("");
                        Add_same();
                    } else if (sh3_radioButton.isChecked()) {
                        Element.add(0, Shtrih_editText.getText().toString() + "?//23");
                        adapter.notifyDataSetChanged();
                        Shtrih_editText.setText("");
                        Add_same();
                    }

                }

            }
        };


        list_print.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                PositionToRemove = position;
                v.setSelected(true);
                adapter.notifyDataSetChanged();

            }
        });

        View.OnClickListener Delete_element = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PositionToRemove != 1000) {
                    Element.remove(PositionToRemove);
                    PositionToRemove = 1000;
                    list_print.clearChoices();
                    adapter.notifyDataSetChanged();
                    last_item--;
                } else {
                    Toast.makeText(getApplicationContext(), "Выберите элемент, который нужно удалить!", Toast.LENGTH_SHORT).show();
                }
                if (Element.isEmpty()) {
                    clear_button.setEnabled(false);
                    Delete_button.setEnabled(false);
                    send_button.setEnabled(false);
                }
            }
        };

        View.OnClickListener Send_element = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //чтобы избежать клацания
                send_button.setEnabled(false);
                //последний элемент last_item
                //заливаем в массив, мусор, что может появиться в ходе работы массива нас не волнует, потому как будут использоваться только элементы до last_item
                message = Element.toArray(message);

            //теперь отправка
 /*         Создаем новый сокет. Указываем на каком компютере и порту запущен наш процесс,
            который будет принамать наше соединение.
        */

                Thread cThread = new Thread(new ClientThread());
                cThread.start();
                send_button.setEnabled(true);
            }
        };


        View.OnClickListener Clear_element = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Element.clear();
                adapter.notifyDataSetChanged();
                clear_button.setEnabled(false);
                Delete_button.setEnabled(false);
                send_button.setEnabled(false);
                PositionToRemove = 1000;
                list_print.clearChoices();
                adapter.notifyDataSetChanged();
                last_item = 0;
            }
        };

        View.OnClickListener Scan_cena_shtrih = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qr=0;
                if (sht_radioButton.isChecked()) {
                    qr = 0;
                } else{
                    qr=1;
                }

                Intent scan_sht_intent = new Intent(MainActivity.this, scan_shtrih_cena.class);
                scan_sht_intent.putExtra("server", server);
                scan_sht_intent.putExtra("QR", qr);
                startActivityForResult(scan_sht_intent,3);
            }
        };

        //Обработка нажатия клавиши Done на клавиатуре

        Art_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Add_element_but.performClick();
                    handled= true;
                }
                return handled;
            }
        });
        //Обработка нажатия клавиши Enter на клавиатуре
        Art_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                boolean handled = false;
                if( event.getAction()==KeyEvent.ACTION_DOWN && event.getKeyCode()== KeyEvent.KEYCODE_ENTER){
                    Add_element_but.performClick();
                    handled= true;
                }
                return handled;
            }
        });

        //Обработка нажатия клавиши Done на клавиатуре
        Shtrih_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Add_element_but.performClick();
                    handled= true;
                }
                return handled;
            }
        });
        //Обработка нажатия клавиши Enter на клавиатуре
        Shtrih_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                boolean handled = false;
                int h = KeyEvent.KEYCODE_ENTER;
                if(event.getAction()==KeyEvent.ACTION_DOWN)
                    if(event.getKeyCode() == h){
                        Add_element_but.performClick();
                        handled= true;
                    }
                return handled;
            }
        });

        //Scanner_button.setOnClickListener(Scan);
        Delete_button.setOnClickListener(Delete_element);
        Add_element_but.setOnClickListener(Add_element);
        clear_button.setOnClickListener(Clear_element);
        Scan_cena.setOnClickListener(Scan_cena_shtrih);
        send_button.setOnClickListener(Send_element);
        clear_button.setEnabled(false);
        Delete_button.setEnabled(false);
        send_button.setEnabled(false);

    }

    public void Add_same()
    {
        //Добавление элемента, повторяющиеся команды
        clear_button.setEnabled(true);
        Delete_button.setEnabled(true);
        send_button.setEnabled(true);
        last_item++;
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
                    Socket socket = new Socket(server, port);
                    OutputStream out;
                    InputStream in;
                    String data="";
                    for(int i=0;i<last_item;i++)
                    {
                        out=socket.getOutputStream();
                        in=socket.getInputStream();
                        byte bufr[] = new byte[64*1024];
                        byte buf[]=message[i].getBytes();
                        out.write(buf,0,buf.length);
                        out.flush();
                        c=in.read(bufr);
                        data = new String(buf, 0, c);

                    }
                    socket.close();
                    //Toast.makeText(getApplicationContext(), data+ " sds", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);}
            }
            catch (Exception e) { Log.e("ClientActivity", "C: Error", e);}
        }
    }
    public void scan(View view){
        int Shab=9, mode;
        int qr=0;
        if (last_item >= 100) {
            Toast.makeText(getApplicationContext(), "Достигнуто максимальное количество элементов списка(100)!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (sh1_radioButton.isChecked()) {
                Shab = 1;
            } else if (sh2_radioButton.isChecked()) {
                Shab = 2;
            } else if (sh3_radioButton.isChecked()) {
                Shab = 3;
            }
            if (sht_radioButton.isChecked()) {
                qr = 0;
            } else{
                qr=1;
            }
            Intent scan_intent = new Intent(MainActivity.this, Scan.class);
            scan_intent.putExtra("Shablon", Shab);
            scan_intent.putExtra("last", last_item);
            scan_intent.putExtra("QR", qr);
            startActivityForResult(scan_intent, 2);
            // zXingScannerView =new ZXingScannerView(getApplicationContext());
            // setContentView(zXingScannerView);
            //  zXingScannerView.setResultHandler(this);
//        zXingScannerView.startCamera();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (item.getItemId() == R.id.action_settings){
            //Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, activity_set_ip.class);
            startActivityForResult(intent,1);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        //обработка результатов по ip адресу
        if (requestCode==1){
            if (resultCode==RESULT_OK) {
                String name = data.getStringExtra("server_ip");
                server = name;
                label_server_ip.setText(server);
            }
        }
        else if(requestCode==2){
         //обработка результатов сканирования
            if (resultCode==RESULT_OK) {
                //Intent intent = getIntent();
                String[] ArrMemory = new String[100];
                int last_scan_arr =  data.getIntExtra("last_it",0);
                ArrMemory = data.getStringArrayExtra("massive");
                //Toast.makeText(getApplicationContext(),"= "+ArrMemory[2],Toast.LENGTH_SHORT).show();
                for(int i=last_item;i<=last_scan_arr-1;i++){
                    Element.add(0, ArrMemory[i]);
                    adapter.notifyDataSetChanged();
                    Add_same();
                }
            }

        }

    }
   // @Override
   // public boolean onKeyDown(int keyCode, KeyEvent event)  {
       // if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

       //     return true;
       // }

       // return super.onKeyDown(keyCode, event);
   // }


  //  private int loadSound(String fileName) {
 //       AssetFileDescriptor afd = null;
 //       try {
  //          afd = assets.openFd(fileName);
 //       } catch (IOException e) {
 //           e.printStackTrace();
   //         Toast.makeText(this, "Couldn't load file '" + fileName + "'", Toast.LENGTH_SHORT).show();
         //   return -1;
   //     }
   //     return mSoundPool.load(afd, 1);
    //}
}
