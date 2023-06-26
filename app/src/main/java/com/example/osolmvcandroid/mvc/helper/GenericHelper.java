package com.example.osolmvcandroid.mvc.helper;

import java.util.Arrays;

public class GenericHelper {
    public static boolean in_array(String[] haystack, String needle){
        Arrays.sort(haystack);//https://stackoverflow.com/a/13734743
        int indexOfNeedle = Arrays.binarySearch(haystack, needle);
        boolean foundNeedleInHaystack = (indexOfNeedle >= 0) ? true : false;
        return foundNeedleInHaystack;
    }
}
