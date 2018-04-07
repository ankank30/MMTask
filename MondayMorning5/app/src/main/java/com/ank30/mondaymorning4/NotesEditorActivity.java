package com.ank30.mondaymorning4;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class NotesEditorActivity extends AppCompatActivity {
    int notesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_editor);

        final EditText titleEditText = findViewById(R.id.title);
        final EditText contentEditText = findViewById(R.id.content);

        Intent intent = getIntent();
        notesId = intent.getIntExtra("notesId", -1);

        if(notesId != -1){
            titleEditText.setText(MainActivity.titleArray.get(notesId));
            contentEditText.setText(MainActivity.contentArray.get(notesId));
        } else {
            MainActivity.titleArray.add("");
            notesId = MainActivity.titleArray.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
            MainActivity.contentArray.add("");
            try {
                SQLiteDatabase notesDB = getApplicationContext().openOrCreateDatabase("Notes", MODE_PRIVATE, null);

                notesDB.execSQL("CREATE TABLE IF NOT EXISTS Notes (title VARCHAR, content VARCHAR, id INT(4))");
                notesDB.execSQL("INSERT INTO notes (id) VALUES (" + Integer.toString(notesId) + ")");

            } catch(Exception e){
                e.printStackTrace();
            }
            MainActivity.sqlIndexId.add(notesId);
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.titleArray.set(notesId, String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                try {
                    SQLiteDatabase notesDB = getApplicationContext().openOrCreateDatabase("Notes", MODE_PRIVATE, null);

                    notesDB.execSQL("CREATE TABLE IF NOT EXISTS Notes (title VARCHAR, content VARCHAR)");
                    notesDB.execSQL("UPDATE Notes SET title = '" + MainActivity.titleArray.get(notesId) + "' WHERE id = " + Integer.toString(MainActivity.sqlIndexId.get(notesId)));

                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.contentArray.set(notesId, String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                try {
                    SQLiteDatabase notesDB = getApplicationContext().openOrCreateDatabase("Notes", MODE_PRIVATE, null);
                    notesDB.execSQL("CREATE TABLE IF NOT EXISTS Notes (title VARCHAR, content VARCHAR)");
                    notesDB.execSQL("UPDATE Notes SET content = '" + MainActivity.contentArray.get(notesId) + "' WHERE id = " + Integer.toString(MainActivity.sqlIndexId.get(notesId)));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
