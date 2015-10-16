package DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Admin on 30.03.2015.
 */
public class DB_Ticket extends SQLiteOpenHelper {

    public DB_Ticket(Context context,Integer DB_Version){
        super(context,"DataBase_Ticket",null,DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table Ticket_table("+
                        "id_ticket integer primary key,"+
                        "zon_ticket text,"+
                        "name_row_ticket text,"+
                        "row_ticket text,"+
                        "place_ticket text,"+
                        "price_ticket text,"+
                        "name_user text,"+
                        "last_name_user text,"+
                        "id_place_schema text,"+
                        "id_event text" + ");"
        );

        db.execSQL(
                "create table Event_table("+
                        "id_event integer primary key,"+
                        "name_event text,"+
                        "date_event text,"+
                        "time_event text,"+
                        "place_event text,"+
                        "url_img text"+ ");"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
