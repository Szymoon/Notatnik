package com.example.szymon.notatnik;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Utilities {

    //android ma 2 typy przechowywania, wewnętrzny - internal i zewnętrzny - external
    //wewnętrzna jest wtedy, że gdy instalujesz aplikacji API androida daje ci od razu unikatowe miejsce dla tej aplikacji
    //(nie musisz pisać miejsc przechowywania sam)

    //ZAPIS OBIEKTU NA DYSKU - ZAOPATRZONE, ŻE TEN OBIEKT MA SERIALIZOWANY INTERFEJS

    public static final  String FILE_EXTENSION = ".bin"; //rozszerzenie naszego pliku

    public static boolean saveNote (Context context, Note note){

        String fileName = String.valueOf(note.getDateTime()) + FILE_EXTENSION; //nazwa pliku - data utworzenia

        FileOutputStream fos; // by zapisac w prywatnym miejscu przechowywaniu dla aplikacji
        ObjectOutputStream oos; //zapisuje do OutputStream

        try {
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(note); //zapis obiektu do strumienia
            oos.close();
            fos.close();

        } catch (IOException e){
            e.printStackTrace(); //da stack trace problemu co się robi, co i gdzie jest źle w kodzie
            return false;   //mowi, że coś poszło nie tak
            //pojawi się wtedy jak użytkownik będzie miał za mało miejsca na urządzeniu
        }
        return  true;
    }

    //robimy 'odwrotność' prywatnego by móc wczytywać notatki w onResume!!
    //zwracamy liste notatek więc ArrayList z Note
    public static ArrayList<Note> getAllSavedNotes(Context context){

        //robimy puste ArrayList
        ArrayList<Note> notes = new ArrayList<>();

        File filesDir = context.getFilesDir(); //musimy dostać się gdzie notatki są zapisywane, filDir - direction
        ArrayList<String> noteFiles = new ArrayList<>(); //lista plików które zapisalismy

        //idziemy przez wszystkie pliki w folderze, jak się kończą z .bin to nasze <3
        for(String file : filesDir.list()) { //w filesDir.list
            if (file.endsWith(FILE_EXTENSION)) {//jak tak to dodajemy to naszego  ArrayList noteFiles
                noteFiles.add(file);
            }
        }
        //musimy zrobić deserializacje, żeby z bajtów z powrotem do naszego życia
        FileInputStream fis;
        ObjectInputStream ois;

        for (int i=0; i<noteFiles.size(); i++ ){      //for bo chcemy wszystkie obiekty z listy

            try{

                fis = context.openFileInput(noteFiles.get(i));
                ois = new ObjectInputStream(fis);

                notes.add((Note)ois.readObject());  //w arraylist notes dodajemy nowy numer/obiekt; (Note) aby wiedziało jaki typ obiektu

                fis.close();
                ois.close();

            }catch (IOException | ClassNotFoundException e) { //jesli klasa Note nie jest znaleziona to ciąg wejściowy obiektu
                e.printStackTrace();                          //nie może oddać z powrotem zawartość pliku do klady Note,
                return  null;                                 //więc musimy złapać te dwa wyjątki
            }
        }
        return  notes;
    }

    //pobieranie nazwy pliku do otwierania
    public static Note getNotebyname(Context context, String fileName){

        File file = new File(context.getFilesDir(), fileName);
        Note note;
        if(file.exists()) {

            FileInputStream fis;
            ObjectInputStream ois;

            try {
                fis = context.openFileInput(fileName);
                ois = new ObjectInputStream(fis);

                note = (Note) ois.readObject();

                fis.close();
                ois.close();

            } catch (IOException | ClassNotFoundException e) { //jesli klasa note nie jest znaleziona to ciąg wejściowy obiektu
                e.printStackTrace();                          //nie może oddać z powrotem zawartość pliku do klasy uwaga, uwaga obiektu
                return null;

            }
            return note;
        }
        return null;
    }

    public static void deleteNote(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);

        if(file.exists()){
            file.delete();
        }

    }
}
