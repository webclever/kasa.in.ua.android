package Format;


/**
 * Created by Женя on 31.07.2015.
 */
public class EncodingTicketCount {

    public EncodingTicketCount(){ }
    public String getNumEnding (String count){
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

    public String getNumEndingRU (String count){
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

    public String getNumEndingEN (String count){
        Integer integerCount = Integer.parseInt(count);
        switch (integerCount){
            case 1:
                return " ticket";
            default:
                return " tickets";
        }

    }
}