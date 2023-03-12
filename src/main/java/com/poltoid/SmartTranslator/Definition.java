package com.poltoid.SmartTranslator;

import java.util.ArrayList;

public class Definition implements Comparable<Definition> {
    public String dict;
    public String dictType;
    public Integer year;
    public ArrayList<String> text;

    @Override
    public String toString() {
        return "Definition{" +
                "dict='" + dict + '\'' +
                ", dictType='" + dictType + '\'' +
                ", year=" + year +
                ", text=" + text +
                '}';
    }

    // For sorting the definitions by year
    @Override
    public int compareTo(Definition o) {
        return Integer.compare(this.year, o.year);
    }
}
