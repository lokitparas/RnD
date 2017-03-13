package com.example.lokit.text2speech;

/**
 * Created by lokit on 01-Mar-17.
 */
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfAcroForm;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Fillform extends AppCompatActivity {
    TextToSpeech t1;
    ArrayList <EditText> ed = new ArrayList<EditText>();
    ArrayList <TextView> tv = new ArrayList<TextView>();
    Button b1;
    LinearLayout ll;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String language_selected = "en-us";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.home);
        final String file_name = intent.getStringExtra("file_path");
        final String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+intent.getStringExtra("file_path");
//        final File pdffile = new File(intent.getStringExtra("file_path"));

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Locale local = new Locale("hi-IND");
                    t1.setLanguage(local);
                }
            }
        });

        t1.setSpeechRate(1);

        ll = (LinearLayout) findViewById(R.id.linearlayout);

        try {
            readPDF(file_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<tv.size();i++){
            final int finalI = i;
            ed.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toSpeak = tv.get(finalI).getText().toString();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        promptSpeechInput(REQ_CODE_SPEECH_INPUT+ finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

//        ed = (EditText) findViewById(R.id.editText);
//        ed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String toSpeak = tv.getText().toString();
//                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                    promptSpeechInput(REQ_CODE_SPEECH_INPUT);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        ed1 = (EditText) findViewById(R.id.editText2);
//        ed1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String toSpeak = tv1.getText().toString();
//                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                    promptSpeechInput(REQ_CODE_SPEECH_INPUT+1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        ed2 = (EditText) findViewById(R.id.editText3);
//        ed2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String toSpeak = tv2.getText().toString();
//                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                    promptSpeechInput(REQ_CODE_SPEECH_INPUT+2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        tv = (TextView) findViewById(R.id.textView);
//        tv1 = (TextView) findViewById(R.id.textView2);
//        tv2 = (TextView) findViewById(R.id.textView3);



        Button b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveDoc(pdffile);
                try {
                    fillPDF(file_path,file_name);
                    finish();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillPDF(String file_path, String file_name) throws DocumentException {
        Document doc = new Document();
        try {
            PdfReader reader = new PdfReader(file_path);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Filled_"+ file_name));
            AcroFields form  = stamper.getAcroFields();

            for(int i=0; i<tv.size();i++){
                form.setField(tv.get(i).getText().toString(),ed.get(i).getText().toString());
            }
            stamper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readPDF(String file_path) throws IOException {
        Document doc = new Document();
        PdfReader reader = new PdfReader(file_path);
        AcroFields form  = reader.getAcroFields();
        HashMap<String,AcroFields.Item> fields = (HashMap<String, AcroFields.Item>) form.getFields();
        Set<Map.Entry<String, AcroFields.Item>> entrySet = fields.entrySet();
        for (Map.Entry<String, AcroFields.Item> entry : entrySet) {
            String key = entry.getKey();
            TextView temp = new TextView(this);
            temp.setText(key);
            ll.addView(temp);

            EditText temp_edit = new EditText(this);
            ll.addView(temp_edit);

            tv.add(temp);
            ed.add(temp_edit);
        }
    }

    private void saveDoc(File pdffile){
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter writer =  PdfWriter.getInstance(doc, new FileOutputStream(pdffile));
            writer.setPageEvent(new PdfPageEventHelper(){
                @Override
                public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
                    TextField field = new TextField(writer, rect, text);
                    try {
                        writer.addAnnotation(field.getTextField());
                    } catch (IOException ex) {
                        throw new ExceptionConverter(ex);
                    } catch (DocumentException ex) {
                        throw new ExceptionConverter(ex);
                    }
                }
            });

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        doc.open();
        Paragraph p = new Paragraph();

        for(int i=0; i<tv.size();i++){
            p.add(tv.get(i).getText().toString());
            Chunk day = new Chunk("            ");
            day.setGenericTag(tv.get(i).getText().toString());
            p.add(day);

            p.add("\n");
        }
        try {
            doc.add(p);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

//        Paragraph para = new Paragraph(tv.getText().toString()+" : " );
//        Paragraph para1 = new Paragraph(tv1.getText().toString()+" : " + ed1.getText().toString());
//        Paragraph para2 = new Paragraph(tv2.getText().toString()+" : " + ed2.getText().toString());
//        try {
//            doc.add(para);
//            doc.add(para1);
//            doc.add(para2);
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
        doc.close();
//        try {
//            readPDF();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
    }

    private void promptSpeechInput(int req_code){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language_selected);
        //Locale.getDefault()
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));

        try{
            startActivityForResult(intent,req_code);

        }
        catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String s;

        if(requestCode >= REQ_CODE_SPEECH_INPUT){
            int index = requestCode-REQ_CODE_SPEECH_INPUT;
            if( resultCode == RESULT_OK && data!=null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                s = result.get(0);
                Log.d("MainActivity",s);
                ed.get(index).setText(s);
            }
        }

//        switch(requestCode){
//            case REQ_CODE_SPEECH_INPUT : {
//                if( resultCode == RESULT_OK && data!=null){
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    s = result.get(0);
//                    Log.d("MainActivity",s);
//                    ed.get(0).setText(s);
//                }
//                break;
//            }
//            case REQ_CODE_SPEECH_INPUT+1 : {
//                if( resultCode == RESULT_OK && data!=null){
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    s = result.get(0);
//                    Log.d("MainActivity",s);
//                    ed1.setText(s);
//                }
//                break;
//            }
//            case REQ_CODE_SPEECH_INPUT+2 : {
//                if( resultCode == RESULT_OK && data!=null){
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    s = result.get(0);
//                    Log.d("MainActivity",s);
//                    ed2.setText(s);
//                }
//                break;
//            }
//
//        }
    }
}
