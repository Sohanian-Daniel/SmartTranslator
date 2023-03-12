package com.poltoid.SmartTranslator;

import java.util.*;

public class Word {
    public String word;
    public String word_en;
    public String type;
    public String[] singular;
    public String[] plural;
    // Was initially a TreeSet, but it didn't allow for multiple of the same element (ordered by year)
    // Then a PriorityQueue, but it doesn't guarantee sorting, in the end I ended up using an ArrayList and sorting it
    // whenever I needed.
    public ArrayList<Definition> definitions;

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", word_en='" + word_en + '\'' +
                ", type='" + type + '\'' +
                ", singular=" + Arrays.toString(singular) +
                ", plural=" + Arrays.toString(plural) +
                ", definitions=" + definitions +
                '}';
    }
}
