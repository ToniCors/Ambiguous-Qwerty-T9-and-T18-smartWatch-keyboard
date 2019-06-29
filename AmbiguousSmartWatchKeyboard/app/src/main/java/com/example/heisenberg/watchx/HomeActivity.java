package com.example.heisenberg.watchx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import it.unisa.di.clueleab.isd.DictMap;

public class HomeActivity extends Activity {

    private Spinner e1;
    private Spinner e2;
    private Intent intent;
    //serialization file
    private File file;
    //backgruond process
    private DictLoader loadr;

    private static final int Keyboard_3x3 = 33;
    private static final int Keyboard_3x6 = 36;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                e1 = (Spinner)findViewById(R.id.code1);
                e2 = (Spinner)findViewById(R.id.code2);


            }
        });

            Logw.folder = new File(Environment.getExternalStorageDirectory()+"/AmbiguousKeyboard/");
            if(!Logw.folder.exists()){
                if(Logw.folder.mkdirs())
                    Toast.makeText(this, "New Folder Created", Toast.LENGTH_SHORT).show();
            }
            /*
        Date tmp= new Date();
        String nameF="Test"+(tmp.toString().substring(4,7))+(tmp.toString().substring(11,19));
        String prefix = Environment.getExternalStorageDirectory()+"/AmbiguousKeyboard/" + File.separator ;
        String ext = ".xls";
        Logw.file = new File(Logw.folder, nameF ) ;
        Logw.low = new File(Logw.folder, nameF+"low");
        Logw.low2 = new File(Logw.folder, nameF+"low2");*/

    }

    public  void init(String u, String t){


        Log.d("*************", u+"-"+t);

        intent.putExtra("code1", u);
        intent.putExtra("code2", t);

        setContentView(R.layout.loding_layout);

        long l = System.currentTimeMillis();
        String[] dictionary = getResources().getStringArray(R.array.dictionary);
        Log.d("LOAD dictionary TIME", "" + (System.currentTimeMillis() - l));
        loadr = new DictLoader(dictionary);
    }



    public void Choose3x3 (View v) throws IOException, ClassNotFoundException {
        intent = new Intent(this, Ambiguous3x3Activity.class);

            String u=""+e1.getSelectedItem().toString();
            String t=""+e2.getSelectedItem().toString();
            init(u,t);
        file= new File(Environment.getExternalStoragePublicDirectory("/AmbiguousKeyboard/"), "serializedict3x3");

        if(file.exists()){
            Log.d("3x3file exist? ", "carico file serealizzato.....");
           /// Costant.startTime();
            ///loadr.execute(0,Keyboard_3x3);
            ///Costant.getTime();

        }else{
            Log.d("3x3file non exist? ", "creo file serializzato....");
            //Costant.startTime();
           /// loadr.execute(1,Keyboard_3x3);
           // Costant.getTime();

        }

        Date tmp= new Date();
        String nameF="3x3["+u+"-"+t+"]"+(tmp.toString().substring(0,11))+(tmp.toString().substring(11,16));
        String prefix = Environment.getExternalStorageDirectory()+"/AmbiguousKeyboard/" + File.separator ;
        String ext = ".xls";
        Logw.file = new File(Logw.folder, nameF ) ;
        Logw.low = new File(Logw.folder, nameF+"low");
        Logw.low2 = new File(Logw.folder, nameF+"low2");
        //esecuzione in backgruond diretta senza serializzazione
        loadr.execute(7357,Keyboard_3x3);

        //startActivity(intent);

        }


    public void Choose3x6 (View v) throws IOException, ClassNotFoundException {
        intent = new Intent(this, Ambiguous3x6Activity.class);

        String u=""+e1.getSelectedItem().toString();
        String t=""+e2.getSelectedItem().toString();
        init(u,t);

        file= new File(Environment.getExternalStoragePublicDirectory("/AmbiguousKeyboard/"), "serializedict3x6");

        if(file.exists()){
            Log.d("3x6file exist? ", "carico file serealizzato.....");
            /// Costant.startTime();
            //loadr.execute(0,Keyboard_3x6);
            // Costant.getTime();

        }else{
            Log.d("3x6file non exist? ", "creo file serializzato....");
            /// Costant.startTime();
            ///loadr.execute(1,Keyboard_3x6);
            ///Costant.getTime();

        }

        Date tmp= new Date();
        String nameF="3x6["+u+"-"+t+"]"+(tmp.toString().substring(0,11))+(tmp.toString().substring(11,16));
        String prefix = Environment.getExternalStorageDirectory()+"/AmbiguousKeyboard/" + File.separator ;
        String ext = ".xls";
        Logw.file = new File(Logw.folder, nameF ) ;
        Logw.low = new File(Logw.folder, nameF+"low");
        Logw.low2 = new File(Logw.folder, nameF+"low2");
        //esecuzione in backgruond diretta senza serializzazione
        loadr.execute(7357,Keyboard_3x6);

        //startActivity(intent);

    }

    public class DictLoader extends AsyncTask<Integer, Integer, DictMap> {
        /*
        private InputStreamReader in ;



        public DictLoader(InputStreamReader in){
            this.in = in;

        }
        */
        private String[] dictionary;

        public DictLoader(String[] dictionary) {
            this.dictionary = dictionary;
        }

        private void createDict(int keyboardType){
            Costant.startTime();

            List<String> formato = null;

            if (keyboardType == 33){
                formato = Costant.OPT_T9_INDEXES;
            }if(keyboardType == 36){
                formato = Costant.OPT_T9_INDEXES_3x6;
            }

            /*
            LinkedHashSet<String> words = new LinkedHashSet<String>();
            BufferedReader br = new BufferedReader(in);
            Pattern pattern = Pattern.compile("[^a-z]");

            try {
                for (String line; (line = br.readLine()) != null; ) {
                    line = line.toLowerCase(Locale.ENGLISH);
                    for (String s : pattern.split(line)) {
                        if (!s.isEmpty()) {
                            words.add(s);
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }


            for (char c = 'a'; c <= 'z'; c++) {
                words.add(String.valueOf(c));
            }
            Costant.getTime();

            Costant.startTime();
            Costant.dictMap = new DictMap(words, formato, Costant.longerWordTop, Costant.firstNoLonger);
            Costant.getTime();

*/
            long l = System.currentTimeMillis();
            Costant.dictMap = new DictMap(Arrays.asList(dictionary), formato, Costant.longerWordTop, Costant.firstNoLonger);
            Log.d("CREATE dictMap TIME", "" + (System.currentTimeMillis() - l));
        }

        @Override
        protected DictMap doInBackground(Integer... action) {
            //action[0] =0 for load, 1 for read, testing altrimenti
            //action[1] =33 for layout 3x3, =36 for layout 3x6

            /*
            if(action[0]==0){
                try {
                    FileInputStream f = new FileInputStream(file);
                    Log.d("backgruound..", "start loading serealization..");
                    Costant.dictMap = DictMap.load(f);
                    Log.d("backgruound...", "end loading serealization...");

                } catch (ClassNotFoundException|IOException e) {
                    e.printStackTrace();
                }
                return null;

            }else if(action[0]==1){
                createDict(action[1]);
                try {
                    Log.d("backgruound...", "start saving serealization..");
                    Costant.dictMap.save(new FileOutputStream(file));
                    Log.d("backgruound...", "end saving serealization...");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Costant.dictMap;

            }else{
            */
                Log.d("backgruound...", "start dict creating..");
                createDict(action[1]);
                Log.d("backgruound...", "end dict creating...");
                //String[] categories = getResources().getStringArray(R.array.dictionary);
                //List<String> stringList = new ArrayList<>(Arrays.asList(categories));
                Log.d("backgruound...", "end dict creating...");

                return Costant.dictMap;
            //}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DictMap dictMap) {
            super.onPostExecute(dictMap);
            //setContentView(R.layout.activity_home);
            startActivity(intent);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }




}


