package se.ottomatech.marcusjacobsson.eaglenote.arrayadapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;
import se.ottomatech.marcusjacobsson.eaglenote.util.QwkNoteTimeUtil;

/**
 * Created by Marcus Jacobsson on 2015-01-22.
 */
public class NotesArrayAdapter extends ArrayAdapter<Note> implements View.OnClickListener {

    private ArrayList<Note> allNotes;
    private NotesAdapterCallback callback;
    private QwkNoteTimeUtil timeUtil;

    public interface NotesAdapterCallback {
        public void checkBoxChecked(Note which);

        public void checkBoxUnchecked(Note which);
    }

    public NotesArrayAdapter(Context ctx, ArrayList<Note> allNotes) {
        super(ctx, R.layout.list_item_note, allNotes);
        this.allNotes = allNotes;
        this.timeUtil = new QwkNoteTimeUtil();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(
                    R.layout.list_item_note, null);
        } else {
            row = convertView;
        }

        TextView tvTitle = (TextView) row.findViewById(R.id.tvNotesListAdapterTitle);
        TextView tvTimeStamp = (TextView) row.findViewById(R.id.tvNotesListAdapterTimestamp);
        CheckBox cbNote = (CheckBox) row.findViewById(R.id.cb_notes);


        String timeStamp = allNotes.get(position).getTimestamp();
        String note = allNotes.get(position).getNote();

        String formatDate = timeUtil.makeTime(timeStamp);

        tvTitle.setText(note);
        tvTimeStamp.setText(formatDate);

        //Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean timestampEnabled = sharedPref.getBoolean(SettingsActivity.pref_key_show_timestamp_list, true);
        String textSize = sharedPref.getString(SettingsActivity.pref_key_general_text_size, "");
        String textStyle = sharedPref.getString(SettingsActivity.pref_key_general_text_style, "");
        if(textStyle.equals("Default")){
            tvTimeStamp.setTypeface(Typeface.DEFAULT);
            tvTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        }else{
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), textStyle);
            tvTitle.setTypeface(typeface, Typeface.BOLD);
            tvTimeStamp.setTypeface(typeface);
        }
        if(!timestampEnabled){
            tvTimeStamp.setText("");
        }
        tvTimeStamp.setTextSize(Float.parseFloat(textSize));
        tvTitle.setTextSize(Float.parseFloat(textSize));

        cbNote.setTag(allNotes.get(position));
        cbNote.setChecked(allNotes.get(position).isChecked());
        cbNote.setOnClickListener(this);

        return row;

    }

    public void setCallback(NotesAdapterCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        CheckBox cb = (CheckBox) v;
        Note note = (Note) v.getTag();
        note.setChecked(cb.isChecked());
        if(cb.isChecked()){
            callback.checkBoxChecked(note);
        }else{
            callback.checkBoxUnchecked(note);
        }
    }
}


