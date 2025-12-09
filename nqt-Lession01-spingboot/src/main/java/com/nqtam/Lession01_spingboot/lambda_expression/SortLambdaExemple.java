package com.nqtam.Lession01_spingboot.lambda_expression;


import java.util.Arrays;
import java.util.List;

public class SortLambdaExemple {
    public static void main(String[] args){
        List<String> list
                = Arrays.asList("Java SpringBoot", "Python", ".Net", "C#");
        list.sort(String::compareTo);
        for (String str:list){
            System.out.println(str);
        }
    }
}
