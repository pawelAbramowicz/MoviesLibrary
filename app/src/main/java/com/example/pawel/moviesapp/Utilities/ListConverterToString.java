package com.example.pawel.moviesapp.Utilities;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.ProductionCountry;

/**
 * Created by Paweł on 2017-05-26.
 */

public class ListConverterToString {

    public static String genreListToString(List<Genre> genreList) {
        StringBuilder sb = new StringBuilder();
        if (genreList != null && !genreList.isEmpty()) {
            for (Genre genre : genreList) {
                sb.append(genre.getName());
                sb.append(", ");
            }
        } else
            sb.append("");

        String text = sb.toString().trim();
        if (text != null && text.length() > 0 && text.charAt(text.length() - 1) == ',') {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public static String countryListToString(List<ProductionCountry> productionCountryList) {
        StringBuilder sb = new StringBuilder();
        if (productionCountryList != null) {
            for (ProductionCountry productionCountry : productionCountryList) {
                Locale locale = new Locale("", productionCountry.getIsoCode());
                sb.append(locale.getDisplayCountry());
                sb.append(", ");
            }
        } else
            sb.append("");

        String text = sb.toString().trim();
        if (text != null && text.length() > 0 && text.charAt(text.length() - 1) == ',') {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    public static String budgetConvert(long number) {

        //cast long to String
        String budget = String.valueOf(number);
        //reverse String
        String budgetReversed = new StringBuilder(budget).reverse().toString();
        //split reversed String - length = 3
        Iterable<String> pieces = Splitter.fixedLength(3).split(budgetReversed);
        //iterate over Iterable and add it to List
        List<String> list = new ArrayList<>();
        for (String stringPart : pieces)
            list.add(stringPart);

        //iterate over reversed List
        String combinedBudget = "";
        for (String stringPart : Lists.reverse(list)) {
            //reverse stringPart
            String a = new StringBuilder(stringPart).reverse().toString();
            //add reversed String to combinedBudget
            combinedBudget += a + " ";
        }

        //trim to remove whitespaces
        return combinedBudget.trim() + "$";
    }
}

