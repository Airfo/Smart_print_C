package air.smart_print_c;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;


public class activity_set_ip extends AppCompatActivity {
    Button save_ip_but;
    Button set_ip_but;
    EditText ip_editText;
    public String server_ip="";
    final String FILENAME = "IP_server";
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        ip_editText=(EditText) findViewById(R.id.ip_editText);
        set_ip_but=(Button) findViewById(R.id. set_ip_but);
        save_ip_but=(Button) findViewById(R.id.save_ip_but);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // фильтр ввода ip адрес
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ip_editText.setFilters(filters);


        View.OnClickListener Save_IP = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( server_ip=="") {
                   Toast.makeText(getApplicationContext(), "Введите и установите IP-адрес!", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   try {
                       // отрываем поток для записи
                       BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                               openFileOutput(FILENAME, MODE_PRIVATE)));
                       // пишем данные
                       bw.write(server_ip);
                       // закрываем поток
                       bw.close();
                       Log.d(LOG_TAG, "Файл записан");
                       Toast.makeText(getApplicationContext(), "Записан IP-адрес: "+server_ip, Toast.LENGTH_SHORT).show();
                   } catch (FileNotFoundException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

            }
        };

        View.OnClickListener Set_IP = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ip_editText.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Введите IP-адрес!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int pos=0, count=0;
                    String IP=ip_editText.getText().toString();
                    for(int i=0;i<IP.length(); i++)
                    {
                        char c=IP.charAt(i);;
                        if(IP.substring(i,i+1).equals("."))
                        {
                            count++;
                            if(count==3)
                                pos=i;
                        }
                    }

                    if(count==3&&IP.length()>(pos+1))
                    {
                        ip_editText.setText("");
                        server_ip=IP;
                        Toast.makeText(getApplicationContext(), "Установлен IP-адрес: "+server_ip, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Неверный формат IP-адреса!", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        };


        save_ip_but.setOnClickListener(Save_IP);
        set_ip_but.setOnClickListener(Set_IP);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(server_ip=="") {
                Intent myActivity = new Intent();
                myActivity.putExtra("server_ip", server_ip);
                setResult(RESULT_CANCELED, myActivity);
                finish();
            }
            else
            {
                Intent myActivity = new Intent();
                myActivity.putExtra("server_ip", server_ip);
                setResult(RESULT_OK, myActivity);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
