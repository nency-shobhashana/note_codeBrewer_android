package com.nency.note.room;

import androidx.room.TypeConverter;

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
        return Arrays.asList(stringList.split(";"));
    }
}
