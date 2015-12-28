package Format;

import android.util.Log;
import java.util.Locale;


public class EncodingTicketCount {

    public EncodingTicketCount(){ }
    public String getNumEnding (String count){

        Log.i("language",Locale.getDefault().getDisplayLanguage());

        if (Locale.getDefault().getDisplayLanguage().equals("українська")){
            Log.i("language","українська");
            return getNumEndingUA(count);
        }else if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("русский")){
            Log.i("language","русский");
            return getNumEndingRU(count);
        }else {
            return getNumEndingEN(count);
        }
    }

    private String getNumEndingUA (String count){
        Integer integerCount;
        if (Integer.parseInt(count) <= 20)
        {
            integerCount = Integer.parseInt(count);
        }else {
            integerCount = Integer.parseInt(String.valueOf(count.charAt(count.length()-1)));
        }
        if (integerCount == 0 || integerCount >= 5 && integerCount <= 20){return " квитків";}
        else if (integerCount == 1){return " квиток";}
        else if (integerCount >= 2 && integerCount <= 4){return " квитки";}
        else {return null;}
    }

    private String getNumEndingRU (String count){
        Integer integerCount;
        if (Integer.parseInt(count) <= 20)
        {
            integerCount = Integer.parseInt(count);
        }else {
            integerCount = Integer.parseInt(String.valueOf(count.charAt(count.length()-1)));
        }
        if (integerCount == 0 || integerCount >= 5 && integerCount <= 20){return " билетов";}
        else if (integerCount == 1){return " билет";}
        else if (integerCount >= 2 && integerCount <= 4){return " билета";}
        else {return null;}
    }

    private String getNumEndingEN (String count){
        Integer integerCount = Integer.parseInt(count);
        switch (integerCount){
            case 1:
                return " ticket";
            default:
                return " tickets";
        }

    }
}