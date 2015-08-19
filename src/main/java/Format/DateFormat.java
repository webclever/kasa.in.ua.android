package Format;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zhenya on 13.07.2015.
 */
public class DateFormat {

    public DateFormat(){}

    public String getData(String strData)
    {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = simpleDateFormatDate.parse(strData);
            return simpleDateFormatDate.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTime(String strData)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
        try {
            Date time = simpleDateFormat.parse(strData);
            return simpleDateFormatTime.format(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
