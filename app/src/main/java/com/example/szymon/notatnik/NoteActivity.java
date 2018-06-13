package com.example.szymon.notatnik;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private boolean mIsViewingOrUpdating;
    private long mNoteCreationTime;

    private EditText mEtTitle;
    private EditText mEtContent;

    private String mNoteFileName; //hold the file name from main activity, do odczytu nazwy
    private Note mLoadedNote;     //do odczytu notatki
    //do tworzenia nowych notatek i podglądu potrzebnych używamy tego samego activity!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mEtTitle = findViewById(R.id.note_et_title);
        mEtContent = findViewById((R.id.note_et_content));

        //otwieranie notatek
        mNoteFileName = getIntent().getStringExtra("NOTE FILE");
        if(mNoteFileName != null && !mNoteFileName.isEmpty()) { //sprawdzamy czy mamy mNoteFileName

            mLoadedNote = Utilities.getNotebyname(this, mNoteFileName);
            if(mLoadedNote != null){
                mEtTitle.setText(mLoadedNote.getTitle());
                mEtContent.setText(mLoadedNote.getContent());
                mNoteCreationTime = mLoadedNote.getDateTime(); //tu dodalismy, żeby sobie rozdzielić na inne menu przy tworzeniu a odczycie

                mIsViewingOrUpdating = true;
            }else {
                mNoteCreationTime = System.currentTimeMillis();
                mIsViewingOrUpdating = false;
            }
        }

    }

    //zeby był
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mIsViewingOrUpdating) { //podgląd notatki
            getMenuInflater().inflate(R.menu.menu_note_view, menu);
        } else { //utworzenie nowej noatki
            getMenuInflater().inflate(R.menu.menu_note_add, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case  R.id.action_note_save:
                //wywołujemy funkcje z Utilities
                saveNote();
                break;

            case R.id.action_note_delete:
                deleteNote();
                break;

            case R.id.action_cancel: //anuluje zmianny
                actionCancel();
                break;
        }

        return super.onOptionsItemSelected(item); //bo mówimy API,że poradziliśmy sobie z kliknięciem (wcześniej było true)
    }

    @Override
    public void onBackPressed() {
        actionCancel();
    }


    private void saveNote(){
        Note note;

        if(mEtTitle.getText().toString().trim().isEmpty() || mEtContent.getText().toString().trim().isEmpty()){//trim bo chcemy uniknac bialych przestrzeni
            Toast.makeText(this, "proszę wprowadzić tytuł i treść!", Toast.LENGTH_SHORT).show();
            return; //wróć bez zapisywania notatki
        }

        //jak jest w jednym activity zapis i odczyt to kolizja zapisu daty(tytułu) po wyjściu!
        if(mLoadedNote == null) {
            note = new Note(System.currentTimeMillis(), mEtTitle.getText().toString()
                    , mEtContent.getText().toString());
        }else {
            note = new Note(mLoadedNote.getDateTime(), mEtTitle.getText().toString()
                    , mEtContent.getText().toString());
        }

        if(Utilities.saveNote(this, note)){  //activity is a context in some ways
            Toast.makeText(this, "zapisałeś notatke" , Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "nie można zapisać notatki, za mało miejsca" , Toast.LENGTH_SHORT).show();

        }

        finish(); //wraca do MainActivity (ostatniej używanej)

    }
    //usunięcie notatki
    private void deleteNote() {
        if(mLoadedNote == null){ //jak jest null ot jest new note, nigdy jej nie zapisał, wystarczy, że wyjdzie z menu notatki
            finish();
        } else {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("usuń")
                    .setMessage("Czy aby na pewno chcesz usunąć notatke " + mEtTitle.getText().toString() + " ?")
                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utilities.deleteNote(getApplicationContext()
                                    , mLoadedNote.getDateTime() + Utilities.FILE_EXTENSION);

                            Toast.makeText(getApplicationContext()
                                    , "Notatka " + mEtTitle.getText().toString() + " została usunięta.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("non", null) //nic nie rób
                    .setCancelable(false); //nie wyłącza się po kliknięciu poza okienko usunięcia

            dialog.show();


        }
    }

    private void actionCancel() { //strzałeczka do cancele biiitch

        if(!checkNoteAltred()) { //tylko wszedłem i chce wyjść
            finish();
        } else { //coś wpisaliśmy
            AlertDialog.Builder dialogCancel = new AlertDialog.Builder(this)
                    .setTitle("odrzucenie zmian...")
                    .setMessage("jesteś pewny, że nie chcesz zapisać wprowadzonych zmian?")
                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); //wraca do MainActivity
                        }
                    })
                    .setNegativeButton("non", null); //null = pozostanie
            dialogCancel.show();
        }
    }
    private boolean checkNoteAltred() {
        if(mIsViewingOrUpdating) { //są zmiany
            return mLoadedNote != null && (!mEtTitle.getText().toString().equalsIgnoreCase(mLoadedNote.getTitle())
                    || !mEtContent.getText().toString().equalsIgnoreCase(mLoadedNote.getContent()));
        } else { //nie ma zmian
            return !mEtTitle.getText().toString().isEmpty() || !mEtContent.getText().toString().isEmpty();
        }
    }
}
