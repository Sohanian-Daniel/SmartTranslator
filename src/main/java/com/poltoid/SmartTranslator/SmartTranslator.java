package com.poltoid.SmartTranslator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

public class SmartTranslator
{
    // holds all the dictionaries, easy to search
    public HashMap<String, Set<Word>> dictionaries;

    // constructor reads all the .json files in dictionaries
    public SmartTranslator() {
        dictionaries = new HashMap<>();

        // open the "dictionaries" directory and read all files that end with .json
        File dirDicts = new File("dictionaries");
        File[] dicts = dirDicts.listFiles((dir, filename) -> filename.endsWith(".json"));
        assert dicts != null;
        for(File file:dicts) {
            // get language name from filename
            String language = file.getName().split("_")[0];

            // read the file and parse the JSON using gson
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Set<Word> dictWords = new Gson().fromJson(br, new TypeToken<HashSet<Word>>() {}.getType());

                /*
                forcefully save the Set as a TreeSet, couldn't figure out how to make Gson save them
                as a TreeSet with a specific Comparator
                */
                TreeSet<Word> sortedWords = new TreeSet<>(Comparator.comparing(o -> o.word));
                sortedWords.addAll(dictWords);
                dictWords = sortedWords;

                // sort the definitions by year, comparable defined in Definition class
                for(Word word:dictWords) {
                    Collections.sort(word.definitions);
                }

                // add the language dictionary to the collection
                dictionaries.put(language, dictWords);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // searches for the Word object in the language dictionary
    Word getWord(String word, String language) {
        for(Word iteratorWord:dictionaries.get(language)) {
            if(iteratorWord.word.compareTo(word) == 0) {
                return iteratorWord;
            }
        }
        return null;
    }

    // find language dictionary and add the word
    public boolean addWord(Word word, String language) {
        return dictionaries.get(language).add(word);
    }

    // find word in language dictionary, if found, remove it
    public boolean removeWord(String word, String language) {
        Word wordObject = getWord(word, language);
        if(wordObject == null) return false;
        return dictionaries.get(language).remove(wordObject);
    }

    // find word in language dictionary, if found, add definition
    public boolean addDefinitionForWord(String word, String language, Definition definition) {
        Word wordObject = getWord(word, language);
        if(wordObject == null) return false;

        // save add result and sort the collection since its not sorted on insert
        boolean result = wordObject.definitions.add(definition);
        Collections.sort(wordObject.definitions);
        return result;
    }

    // find word in language dictionary, find the dictionary in the definition, delete if found
    public boolean removeDefinition(String word, String language, String dictionary) {
        Word wordObject = getWord(word, language);
        if(wordObject == null) return false;
        for(Definition definition:wordObject.definitions) {
            if(definition.dict.compareTo(dictionary) == 0) {
                return wordObject.definitions.remove(definition);
            }
        }
        return false;
    }

    public String translateWord(String word, String fromLanguage, String toLanguage) {
        // get Word from "fromlanguage" dictionary
        Word fromLanguageWord = getWord(word, fromLanguage);

        if(fromLanguageWord != null) {
            // thought about adding a special EN clause, decided not to
            // search for the same word in toLanguage based on word_en
            for (Word iteratorWord : dictionaries.get(toLanguage)) {
                if (iteratorWord.word_en.compareTo(fromLanguageWord.word_en) == 0) {
                    return iteratorWord.word;
                }
            }
        }
        return word;
    }

    public String translateSentence(String sentence, String fromLanguage, String toLanguage) {
        // split sentence into words
        String[] words = sentence.split(" ");
        // copy result, replace each word with translated word
        String result = sentence;
        for(String word:words) {
            String translatedWord = translateWord(word, fromLanguage, toLanguage);
            result = result.replace(word, translatedWord);
        }
        return result;
    }

    // gets an array of synonyms of given word
    String[] getWordOptions(Word word) {
        ArrayList<String> options = new ArrayList<>();
        for(Definition definition : word.definitions) {
            if(definition.dictType.compareTo("synonyms") == 0) {
                options.addAll(definition.text);
            }
        }

        return options.toArray(new String[0]);
    }

    public ArrayList<String> translateSentences(String sentence, String fromLanguage, String toLanguage) {
        int count = 0;
        ArrayList<String> translations = new ArrayList<>();

        // if one of the languages doesnt exist, return the sentence only
        if(dictionaries.get(fromLanguage) == null || dictionaries.get(toLanguage) == null) {
            translations.add(sentence);
            return translations;
        }

        // initial translation counts as one of the options
        String initialTranslation = translateSentence(sentence, fromLanguage, toLanguage);
        translations.add(initialTranslation);
        count++;

        // split the translation into its words
        String[] translatedWords = initialTranslation.split(" ");
        // go through every word and find all its options
        for (String translatedWord : translatedWords) {
            // get all word options for the current word, if word doesnt exist, move on to next word
            if (getWord(translatedWord, toLanguage) == null) continue;
            String[] options = getWordOptions(getWord(translatedWord, toLanguage));

            // get all sentences created with those options for the word
            for (String option : options) {
                String copy = initialTranslation;
                copy = copy.replace(translatedWord, option);
                translations.add(copy);
                count++;
                if (count == 3) return translations;
            }
        }
        return translations;
    }

    // Find the word and return its definitions, getter with extra steps
    public ArrayList<Definition> getDefinitionsForWord(String word, String language) {
        Word wordObject = getWord(word, language);
        if(wordObject == null) return null;
        return wordObject.definitions;
    }

    // Export using gson's toJson method
    public void exportDictionary(String language) {
        // check if language exists
        if(dictionaries.get(language) == null) return;

        // Pretty printing and fixing unicode stuff to support accents
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        // write to /exports/ directory
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("exports/" + language + "_dict.json"));
            writer.write(gson.toJson(dictionaries.get(language)));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
