package com.ank30.mondaymorning4;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Boolean> temp = new ArrayList<>();

    TextView textView1;
    TextView textView;
    ListView listView;

    static public ArrayList<String> titleArray = new ArrayList<>();
    static public ArrayAdapter arrayAdapter;
    static public ArrayList<String> contentArray = new ArrayList<>();
    static public List<Integer> sqlIndexId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView1 = findViewById(R.id.extraTextSpace);
        textView = findViewById(R.id.space_for_reminder);
        listView = findViewById(R.id.notesListView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotesEditorActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            sqlIndexId.clear();
            titleArray.clear();
            contentArray.clear();

            SQLiteDatabase notesDB = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            Cursor c = notesDB.rawQuery("SELECT * FROM Notes", null);
            int titleIndex = c.getColumnIndex("title");
            int contentIndex = c.getColumnIndex("content");
            int idIndex = c.getColumnIndex("id");
            c.moveToFirst();
            while (c != null) {
                sqlIndexId.add(c.getInt(idIndex));
                titleArray.add(c.getString(titleIndex));
                contentArray.add(c.getString(contentIndex));
                c.moveToNext();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.notesListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleArray);
        listView.setAdapter(arrayAdapter);

        listeners();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Nothing to Set in this 'Settings'!!!", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notes) {

            textView.setText(" ");
            textView1.setText(" ");

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleArray);
            listView.setAdapter(arrayAdapter);

            listeners();

        } else if (id == R.id.nav_reminder) {

            textView.setText("Sorry, Still Under Construction!!!");
            textView1.setText(" ");

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temp);
            listView.setAdapter(arrayAdapter);

        } else if(id == R.id.nav_contacts) {

            textView.setText("Ankesh Anku");
            textView1.setText("ankesh.anku30@gmail.com \n 9031767127");

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temp);
            listView.setAdapter(arrayAdapter);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void listeners(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), NotesEditorActivity.class);
                intent.putExtra("notesId", i);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int itemToDelete = i;
                final int sqlId = sqlIndexId.get(itemToDelete);

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                titleArray.remove(itemToDelete);
                                contentArray.remove(itemToDelete);
                                arrayAdapter.notifyDataSetChanged();;

                                try {
                                    SQLiteDatabase notesDB = getApplicationContext().openOrCreateDatabase("Notes", MODE_PRIVATE, null);

                                    notesDB.execSQL("DELETE FROM Notes WHERE id = " + Integer.toString(sqlId));
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }
}