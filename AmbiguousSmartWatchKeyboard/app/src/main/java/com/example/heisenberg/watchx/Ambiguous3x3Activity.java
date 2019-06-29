package com.example.heisenberg.watchx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Editable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unisa.di.clueleab.isd.DictMap;

public class Ambiguous3x3Activity extends AppCompatActivity {


    private DictMap optT9Map;
    private final String DELIM = "\t";
    private ArrayList<CharSequence> answers = new ArrayList<>();// arrey di suggeriemnti
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private String editTextContnent; //indica il contenuto dell edit text

    private EditText editText;

    private int counter = 1, selw = 0, cchar = 0, maxLen = 12, len = 0;
    private String frase = "Frase", actCode = "", regex = "", pareg="", msgpass="";
    private final static int maxPhrase = 5;
    private long start_time=0, end_time=0;
    private boolean bol1 = false, bol2 = true, first=true, first0=true;
    private PrintWriter loggy ;
    private PrintWriter loglow;
    private PrintWriter loglow2;
    FileOutputStream os = null, os2=null, os3=null;
    float xPrec, yPrec, zPrec;
    private Sensor mySensor;
    private SensorManager SM;
    private double del=0;
    private double numBksp=0;
    private double KSPC=0;
    SensorEvent mSensorEvent;
    private long keystrokeTime, currtime, delay = 400;
    private final static float acceleration = 0.3f;
    StringBuffer buf;
    int start;
    String sentence = "";
    int character;
    final double filter = 0.85;

    private String filename ="";
    private String filesFolder ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ambiguous3x3);

        //--------------------------------inizializzazione DictMap
        optT9Map = Costant.dictMap;
        Logw.phrase = new ArrayList<String>();
        //--------------------inizializzazione degli elemnti della view

        editText = (EditText) findViewById(R.id.insertText);
        editText.requestFocus();
        editText.setText("");

        Intent intent = getIntent();
        filesFolder = "phrase/";
        filename ="t9_"+ intent.getStringExtra("code1")+"_"+intent.getStringExtra("code2");
        Log.d("file name", filename);


        //-----------------------inizializzazione del file delle frasi da testare
        Log.d("1-------------file id", ""+filename);
        int id = this.getResources().getIdentifier(filename, "raw", this.getPackageName());
        Log.d("2-------------file id", ""+id);
        readStringFromResource(this,id);

        //-----------------------inizializzazione lista delle scelte della parola
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, answers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //-----------------------spazio per inserire la frase da testare
        TextView numFrase = (TextView) findViewById(R.id.numP);
        /// String Tex0 = filename+": "+Logw.phrase.get(counter-1);
        numFrase.setText(filename+": "+Logw.phrase.get(counter-1));


        //----------------------------------inizializzazione tastiera
        Keyboard mKeyboard = new Keyboard(Ambiguous3x3Activity.this, R.xml.keyboard_12key);
        final KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mKeyboardView.setVisibility(View.VISIBLE);
                mKeyboardView.setEnabled(true);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        try {
            os = new FileOutputStream(Logw.file, true);
            os2 = new FileOutputStream(Logw.low, true);
            os3 = new FileOutputStream(Logw.low2, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String tmp="Tastiera 3x3 log\n";
            String message = "Log opened: " + new Date() + " (" + SystemClock.uptimeMillis() + ")";
            loggy= new PrintWriter(new FileOutputStream(Logw.file), true);
            loglow=new PrintWriter(new FileOutputStream(Logw.low), true);
            loglow2=new PrintWriter(new FileOutputStream(Logw.low2), true);
            loggy.println(message);
            loglow.println(message);
            loglow2.println(message);
            loglow.println("Time"  + DELIM + "Key"  + DELIM + "Value" + DELIM + "PreText"+ DELIM + "AfterText");
            loglow.println("#start: "+Logw.phrase.get(0));
            loggy.println("Frase n." + DELIM + "presented" + DELIM + "transcribed" + DELIM + "presented_characters" + DELIM + "transcribed_characters" + DELIM + "input_time(sec)" + DELIM +"wpm" + DELIM + "msd" + DELIM + "numBksp" + DELIM + "numDelChars" + DELIM + "total_error" +  DELIM + "cor_error" + DELIM + "uncor_error" +  DELIM + "keystrokes" + DELIM + "kspc");
            loglow2.println("ACTION"+ DELIM + "X" + DELIM + "Y"+DELIM+"ROWx"+DELIM+"ROWy"+DELIM+"MILLISECOD");
            loglow2.println("#start: "+Logw.phrase.get(0));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyboardView.setVisibility(View.VISIBLE);
                mKeyboardView.setEnabled(true);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Editable editable = editText.getText();
        start = editText.getSelectionStart();

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(editText.getText().length()!=0){
                if (counter <= maxPhrase) {

                    loglow.println("#end: "+Logw.phrase.get(counter-1));
                    loglow2.println("#end: "+Logw.phrase.get(counter-1));


                    end_time=System.currentTimeMillis();
                    loglow.println(end_time-start_time+"\t"+13+"\t"+"[enter]"+"\t"+""+editText.getText());
                    int cc = editText.getText().toString().length();
                    String s1= Logw.phrase.get(counter-1);
                    String s2= editText.getText().toString();
                    double sec=(double)(end_time-start_time)/1000;
                    MSD ss= new MSD(s1,s2);
                    //KSPC= ((double)s2.length()+(del*2))/(double)s2.length();
                    //loggy.append(frase + " n. " + DELIM + counter+"\nP: "+s1+"\nT: "+s2+"\nTime = "+sec+", NumPress = "+cchar+","+String.format(" WPM = %.2f", ((((double)cc/5)/(sec/60))))+","+String.format(" Error rate = %.4f%%", ss.getErrorRate())+String.format(" MSD ErrorRateNew = %.4f%%", ss.getErrorRateNew())+String.format(" KSPC =  %.2f ", ((((double)s2.length())+(del*2))/((double)s2.length())) )+"\n\n");
                    int contpres=s1.length();

                    int conttras=s2.length();
                    if(conttras>0){
                        if(s2.charAt(s2.length()-1)  == ' '){
                            conttras--;
                        }}


                    String wpm=String.format("%.2f", ((((double)cc/5)/(sec/60))));
                    String msd=String.format("%.2f", (double)ss.getMSD());
                    String kspc=String.format("%.2f ", (((conttras)+(del+numBksp))/(conttras)));
                    String keyStrokes=String.format("%.2f ", ((conttras)+(del+numBksp)));
                    String totalError=String.format("%.2f", ss.totalErrorRate(s1,s2,del));
                    String corError=String.format("%.2f", ss.uncorrErrorRate(s1,s2,del));
                    String uncorError=String.format("%.2f", ss.corrErrorRate(s1,s2,del));
                    //The IF keystrokes are those in the input stream, but not in the transcribed text, that are not editing keys.
                    loggy.println( counter +  DELIM  + s1 + DELIM + s2 + DELIM + contpres + DELIM + conttras + DELIM + sec +/* DELIM + "" + DELIM + sec +*/ DELIM + wpm + DELIM + msd + DELIM + numBksp + DELIM + del + DELIM + totalError +  DELIM + corError + DELIM + uncorError + DELIM + keyStrokes + DELIM + kspc);
                    first=true;
                    //String tmp0= frase + " n. " + counter+"\nP: "+s1+"\nT: "+s2+"\n";
                    cchar=0;
                    counter++;
                    start_time=0;
                    conttras=0;
                    numBksp=0;
                    del=0;
                    //String Tex = msgpass + ": " + frase + " n." + counter;
                    //TextView numPh = (TextView) findViewById(R.id.numP);
                    //numPh.setText(Tex);
                    //editable.clear();
                    sentence = "";
                    clean();
                    editText.setText("");
                    answers.clear();
                    adapter = new RecyclerViewAdapter(getApplicationContext(), answers);
                    recyclerView.setAdapter(adapter);

                    if(counter<=5){
                        loglow.println("#start: "+Logw.phrase.get(counter-1));
                        loglow2.println("#start: "+Logw.phrase.get(counter-1));
                        numFrase.setText(Logw.phrase.get(counter-1));
                    }

                    if(counter>maxPhrase) {
                        Intent intent = getIntent();
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                }
                }else{
                    //Toast.makeText(getApplicationContext(),"EDIT TEXT VUOTA",Toast.LENGTH_SHORT);
                    //non inviare stringa vuota
                }
                return true;
            }
        });
    }

    public void clean(){
        bol1 = false;
        bol2 = true;
        selw = 0;
        answers.clear();
        actCode = "";
        regex="";
        actCode="";
    }


    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            Log.e("primaryCode: "+primaryCode, " keyCodes: " + keyCodes[keyCodes.length - 1] + " " + keyCodes.length);

            if(first0) {
                start_time = System.currentTimeMillis();
                first0 = false;
            }
            Editable editable = editText.getText();
            start = editText.getSelectionStart();

            editTextContnent =""+editText.getText();

            if (primaryCode == 16) {
                //cancell

                loglow.print(System.currentTimeMillis()-start_time+"\t"+primaryCode+"\t"+"[delete]"+"\t"+""+editText.getText());
                numBksp++;


                if(editTextContnent.length() !=0){
                    del++;
                    editTextContnent = editTextContnent.substring(0, editTextContnent.length() - 1);
                    if (editTextContnent.length() == 0 || editTextContnent.charAt(editTextContnent.length() - 1) == ' ') {
                        Log.e("Cancel key: ", " in if: " + " " + editTextContnent.length());
                    }
                } else{
                    first=true;
                    cchar=0;
                    first0=true;
                }
            } else {
                //space or letter
                if (primaryCode == 12) {
                    loglow.print(System.currentTimeMillis()-start_time+"\t"+primaryCode+"\t"+"[space]"+"\t"+""+editText.getText());
                    editTextContnent = editTextContnent+" ";
                }else{
                    if(primaryCode >= 1 && primaryCode <= 9){
                        if (first) {
                            start_time = System.currentTimeMillis();
                            first = false;
                        }
                        loglow.print(System.currentTimeMillis()-start_time+"\t"+primaryCode+"\t"+ Costant.OPT_T9_INDEXES.get(primaryCode - 1) +"\t"+""+editText.getText());
                        char character = Costant.OPT_T9_INDEXES.get(primaryCode - 1).charAt(0);
                        editTextContnent = editTextContnent+character;
                        Log.e("character",""+character);
                    }

                }

            }
            editText.setText(editTextContnent);
            editText.setSelection(editTextContnent.length());

            int scelta;

            if(primaryCode == 16){
                if(editTextContnent.length() >= 2) {
                    if (editTextContnent.charAt(editTextContnent.length() - 1) == ' ' && editTextContnent.charAt(editTextContnent.length() - 2) == ' ') {
                        scelta = 0;
                    } else {
                        scelta = 1;
                    }
                }else{
                    scelta=1;
                }

            }else if(primaryCode == 12){
                scelta=0;
            }else{
                scelta =1;
            }

            T9Options(scelta, getLastWord(editTextContnent));


        }

    };
    public boolean dispatchTouchEvent(MotionEvent event) {

        end_time=System.currentTimeMillis();

        String.format("%.2f", event.getX()) ;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            loglow2.println("ACTION_DOWN"+ DELIM + String.format("%.2f", event.getX())+"*" + DELIM + String.format("%.2f", event.getY())+"*"+DELIM+String.format("%.2f", event.getRawX())+"*"+DELIM+String.format("%.2f", event.getRawY())+"*"+DELIM+""+(end_time-start_time));
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            loglow2.println("ACTION_UP"+ DELIM + String.format("%.2f", event.getX())+"*" + DELIM + String.format("%.2f", event.getY())+"*"+DELIM+String.format("%.2f", event.getRawX())+"*"+DELIM+String.format("%.2f", event.getRawY())+"*"+DELIM+""+(end_time-start_time));
        }
        return super.dispatchTouchEvent(event);
    }



    public void T9Options(int i, String text) {
        answers.clear();

        if (i != 0) {
            List<Integer> indexs = optT9Map.toIndexes(text);
            List<String> list = optT9Map.getWords(indexs, true);
            answers.addAll(list);

            String newEditTextContnent = ""+getPreLastWord(editTextContnent);

            if(list.size()!=0){
                newEditTextContnent=newEditTextContnent+list.get(0);}
            loglow.println("\t"+newEditTextContnent);
            editText.setText(newEditTextContnent);
            editText.setSelection(newEditTextContnent.length());
        }

        adapter = new RecyclerViewAdapter(this, answers);
        recyclerView.setAdapter(adapter);
    }

    public String getLastWord(String text) {
        String[] lastWord = text.split(" ");
        // Log.e("last word ", "lenght: "+lastWord.length+ ", word: "+ lastWord[lastWord.length-1]);
        if(lastWord.length == 0)
            return "";
        else
            return lastWord[lastWord.length-1];
    }

    public String getPreLastWord(String text) {
        String[] lastWord = text.split(" ");

        String result = "";
        int n = lastWord.length;
        if (n == 1) {
            return result;
        } else {
            for (int i = 0; i <= n - 2; i++) {
                result = result + lastWord[i] + " ";
            }
        }
        Log.e("pre lasst word:", "[" + result + "]");

        return result;
    }

    static public void readStringFromResource(Context ctx, int resourceID) {

        try {
            InputStream is = ctx.getResources().openRawResource(resourceID);

            BufferedReader input =  new BufferedReader(new InputStreamReader(is), 1024*8);
            try {
                String line = null;
                while (( line = input.readLine()) != null){
                    Log.d("+----fileContent", "*"+line+"*");
                    Logw.phrase.add(line);
                }
                Log.d("+----fileContent",""+ Logw.phrase.size());

            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            Log.e("error", "Couldn't find the file " + resourceID  + " " + ex);
        }
        catch (IOException ex){
            Log.e("error", "Error reading file " + resourceID + " " + ex);
        }

    }


    /**
     * Creazione list view orizionatale
     */
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static final String TAG = "RecyclerViewAdapter";

        private ArrayList<CharSequence> optionList = new ArrayList<>();
        private Context mContext;

        public RecyclerViewAdapter(Context context, ArrayList<CharSequence> names) {
            optionList = names;
            mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {

            holder.option.setText(optionList.get(position));

            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: suggerimento clicked : " + optionList.get(position));
                    //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                    String text = editText.getText() + "";

                    String result = getPreLastWord(text);
                    Log.d("click ", "result 1:[" + result + "]");

                    result = result + optionList.get(position) + " ";
                    Log.d("click ", "result 2:[" + result + "]");

                    loglow.println(System.currentTimeMillis()-start_time+"\t"+"*"+position+"\t"+"["+optionList.get(position)+"]"+"\t"+""+editText.getText());

                    editText.setText(result);
                    editText.setSelection(result.length());
                }
            });
        }

        @Override
        public int getItemCount() {
            return optionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView option;

            public ViewHolder(View itemView) {
                super(itemView);
                option = (TextView) itemView.findViewById(R.id.itemTitle);
            }
        }
    }


}