package com.example.szymon.notatnik;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

//adapter potrzebny do onResume do listView
//musi dziedziczyć od naszego editora
//używamy ArrayAdapter by zapewnić AdapterView i jest użyty dla klasy Note
//musimy wprowadzić konstruktory superklasy

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter( Context context, int resource,  ArrayList<Note> notes) { //potrzebujemy działać tylko na 3 argumentach
        super(context, resource, notes);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        //return super.getDropDownView(position, convertView, parent); //daje nam serie argumentów

        //sprawdzamy, czy convertView jest null czy nie, jak tak to sami musimy go zbudować
        //Layoutinflator tworzy plik XML układu w odpowiadających mu obiektach View.
        //z kontekt w którym jesteśmy(getContext) i potem influate the layout file
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_note, null);
        }

        //bedziemy umieszcząć każdą notatke w pozycji w listview
        Note note = getItem(position);

        //sprawdzamy czy notatka jest null czy nie
        //jak nie jest to kontynuuje się budowa tego listview
        //robimy odniesienie do tych 3 textviev z item_note
        if(note != null){

            TextView title =  convertView.findViewById(R.id.list_note_title);
            TextView date =  convertView.findViewById(R.id.list_note_date);
            TextView content =  convertView.findViewById(R.id.list_note_content);

            title.setText(note.getTitle()); //pobieram tytul jaki sobie wpisalem
            date.setText(note.getDateTimeFormatted(getContext()));

            //nie wyświetlanie całego tekstu notatki w listview
            if(note.getContent().length() > 35) {
                content.setText(note.getContent().substring(0,35));
            }else{
                content.setText(note.getContent());
            }

        }
        return convertView;



    }
}
