package com.example.heisenberg.watchx;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import it.unisa.di.clueleab.isd.DictMap;

public class Costant {

    public static DictMap dictMap;
    public static int longerWordTop = 4;
    public static boolean firstNoLonger = true;

    private static long time= 0;

    public static final List<String> OPT_T9_INDEXES = Arrays.asList(
            "qw", "ertyui", "op",
            "as", "dfgh", "jkl",
            "zxc", "vbn", "m");

    public static final List<String> OPT_T9_INDEXES_3x6 = Arrays.asList(
            "qw", "e",   "r",  "t", "yui", "op",
            "a",  "s", "dfg",  "h",  "jk",  "l",
            "zx", "c",   "v",  "b",  "n",   "m");


    // parte il cronometro
    public static void startTime(){
        time = System.currentTimeMillis();
        Log.d("---T---start timing: ", "");

    }

    // viene fermato il cronometro e stamapto il risultato
    public static long getTime(){
        long res = (System.currentTimeMillis() - time) / 100;
        Log.d("---T---work finish in: ", "" + res+"(ds)");
        return res;
    }

}
