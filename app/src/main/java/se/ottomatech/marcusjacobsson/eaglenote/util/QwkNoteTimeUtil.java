package se.ottomatech.marcusjacobsson.eaglenote.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Marcus Jacobsson on 2015-01-29.
 */
public class QwkNoteTimeUtil {


    public String makeTime(String timeStamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            String firstOutput = outputFormat.format(date);
            char firstChar = Character.toUpperCase(firstOutput.charAt(0));
            String secondOutput = firstChar + firstOutput.substring(1);
            return secondOutput;
        } else {
            return null;
        }
    }
}
