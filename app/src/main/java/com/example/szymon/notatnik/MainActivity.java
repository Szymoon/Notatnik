package com.example.szymon.notatnik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ListView mListViewNotes;  //wywołujemy przy robieniu wyświetlania listy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListViewNotes = findViewById(R.id.main_listview_notes); //klasa Resources, identyfikatry
    }

    //ctrl+O
    //opcja menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //żeby się coś działo jak kliknie
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_main_new_note:
                //intent wywoła nowe activity; this-context w którym jesteśmy;
                Intent newNoteActivity = new Intent(this, NoteActivity.class);
                startActivity(newNoteActivity);
                break;
        }
        return true; // bo boolean i API tego oczekuje
    }

    //musimy osadzić ListViev najlepiej podczas wznowienia
    //onResume - stan gdzie apka oddziaływuje z użytkownikiem
    //widok adaptera nie jest w stanie samodzielnie wyświetlać żadnych danych
    //jego zawartość jest zawsze określona przez inny obiekt - adapter
    @Override
    protected void onResume() { //musimy załadować notatki z prywatnego przechowywania w którym je zapisalismy
        super.onResume();
        mListViewNotes.setAdapter(null);

        ArrayList<Note> notes = Utilities.getAllSavedNotes(this); //w androidzie acticities i context to praktycznie to w większości to samo

        //sortuje od najnowszej no najstarszej
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                if(lhs.getDateTime() > rhs.getDateTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        //sprawdzamy czy mamy jakies notatki
        if(notes == null || notes.size() == 0){
            Toast.makeText(this, "Nie masz zapisanych notatek", Toast.LENGTH_SHORT).show();
            return;
        }else { //wyświetlanie listy
            NoteAdapter na = new NoteAdapter(this, R.layout.item_note, notes); //ma wypelnić R.layout.item_note, notes - 3 obiekt to ten załadowany z dysku
            mListViewNotes.setAdapter(na);

            mListViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() { //funkcja wchodzenia w notatki
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String fileName = ((Note)mListViewNotes.getItemAtPosition(position)).getDateTime()
                            + Utilities.FILE_EXTENSION;
                    //chcemy otwierać w activity kiedy notatka została kliknieta
                    Intent viewNoteIntent = new Intent(getApplicationContext(), NoteActivity.class);
                    viewNoteIntent.putExtra("NOTE FILE", fileName); //pass data between your activity, bedziemy znali nazwe
                    startActivity(viewNoteIntent); // startujemy activity które chcemy stworzyć

                }
            });
        }
    }
}
