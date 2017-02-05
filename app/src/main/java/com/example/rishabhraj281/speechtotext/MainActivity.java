package com.example.rishabhraj281.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button btnSpeak;
    private TextView outputTxt;
    private TextView instructionTxt;
    private Spinner spinnerLanguageOptions;
    private String language_selected = "en-us"; // Default language
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionTxt = (TextView) findViewById(R.id.txtOptions);
        instructionTxt.setText(getString(R.string.language_prompt));

        outputTxt = (TextView) findViewById(R.id.txtOutput);

        spinnerLanguageOptions = (Spinner) findViewById(R.id.spinnerOptions);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_options, R.layout.support_simple_spinner_dropdown_item);
        spinnerLanguageOptions.setAdapter(adapter);

        spinnerLanguageOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    language_selected = "en-us";

                }
                else if(position == 1){
                    language_selected = "hi";
                }
                else{
                    Log.d("MainActivity", "Postion other than 0 or 1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language_selected);
        //Locale.getDefault()
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));

        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);

        }
        catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQ_CODE_SPEECH_INPUT : {
                if( resultCode == RESULT_OK && data!=null){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String s = result.get(0);
                    Log.d("MainActivity",s);
                    outputTxt.setText(s);
                }
                break;
            }
        }
    }
}
