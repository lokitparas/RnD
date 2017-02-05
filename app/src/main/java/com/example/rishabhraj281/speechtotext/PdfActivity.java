package com.example.rishabhraj281.speechtotext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
    }
}
