package com.nency.note.room;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converter {
    @TypeConverter
    public static String toString(List<String> list){
        String newString = "";
        for (String s : list) {
            newString = newString + s +";";
        }

        return newString;
    }

    @TypeConverter
    public static List<String> toList(String stringList){
        if(TextUtils.isEmpty(stringList)){
            return new ArrayList<>();
        }
        return Arrays.asList(stringList.split(";"));
    }
}
