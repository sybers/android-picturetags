package fr.stanyslasbres.picturetags.persistence;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Types converters for the application database
 */
public class AppConverters {
    @TypeConverter
    public static Date TimestamptoDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Uri stringToUri(String value) {
        return value == null ? null : Uri.parse(value);
    }

    @TypeConverter
    public static String uriToString(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public static String listToString(List<Long> value) {
        return value == null ? null : new Gson().toJson(value);
    }

    @TypeConverter
    public static List<Long> stringToList(String value) {
        Log.e("SLIP", "VALEUR DU BORDEL \" " + value + "\"");
        if(value == null) return new ArrayList<>();
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
}
