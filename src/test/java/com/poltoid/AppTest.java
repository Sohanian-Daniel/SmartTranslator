package com.poltoid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poltoid.SmartTranslator.Definition;
import com.poltoid.SmartTranslator.SmartTranslator;
import com.poltoid.SmartTranslator.Word;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class AppTest 
{
    @Test
    public void initialiseTest() {
        // check if it initialises at all
        System.out.println("------INITIALISATION------");
        SmartTranslator smartTranslator = new SmartTranslator();
        for(Set<Word> dictWords:smartTranslator.dictionaries.values()) {
            for (Word word : dictWords) {
                System.out.println(word.toString());
            }
        }
        assertFalse(smartTranslator.dictionaries.isEmpty());
        System.out.println();
    }

    @Test
    public void addWordTest() {
        // reads certain json files, parses them into objects and adds them to dictionaries
        // adds 2 new words and tries to add an existing word, check src/test/in for exact json files used
        System.out.println("------ADD WORD TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        File dirDicts = new File("src/test/in/addWord");
        File[] dicts = dirDicts.listFiles((dir, filename) -> filename.endsWith(".json"));
        assert dicts != null;
        for(File file:dicts) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Word word = new Gson().fromJson(br, new TypeToken<Word>() {}.getType());
                boolean testResult = smartTranslator.addWord(word, "ro");
                System.out.println("ADDING WORD " + word.word + ":\t" + testResult);
                assertTrue(testResult);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }
    @Test
    public void addWordEdgeTest() {
        // edge case where the word already exists
        System.out.println("------ADD WORD EDGE CASE TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        File dirDicts = new File("src/test/in/addWordEdge");
        File[] dicts = dirDicts.listFiles((dir, filename) -> filename.endsWith(".json"));
        assert dicts != null;
        for(File file:dicts) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Word word = new Gson().fromJson(br, new TypeToken<Word>() {}.getType());
                boolean testResult = smartTranslator.addWord(word, "ro");
                System.out.println("ADDING WORD " + word.word + ":\t" + testResult);
                assertFalse(testResult);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    @Test
    public void removeWordTest() {
        // first two words exist while the third one doesnt
        System.out.println("------REMOVE WORD TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();

        boolean testResult = smartTranslator.removeWord("chat", "fr");
        System.out.println("REMOVE \"chat\" FROM \"fr\":\t" + testResult);
        assertTrue(testResult);

        testResult = smartTranslator.removeWord("câine", "ro");
        System.out.println("REMOVE \"câine\" FROM \"ro\":\t" + testResult);
        assertTrue(testResult);

        System.out.println("\n---REMOVE WORD EDGE CASE TEST---");
        testResult = smartTranslator.removeWord("chat", "ro");
        System.out.println("REMOVE \"chat\" FROM \"ro\":\t" + testResult);
        assertFalse(testResult);

        System.out.println();
    }

    @Test
    public void addDefinitionForWordTest() {
        // reads .json files converts them to objects and tries adding them to different words
        System.out.println("------ADD DEFINITION FOR WORD TEST------");
        int count = 0;
        String[] wordList = {"pisică", "câine"};
        SmartTranslator smartTranslator = new SmartTranslator();
        File dirDicts = new File("src/test/in/addDefinitionForWord");
        File[] dicts = dirDicts.listFiles((dir, filename) -> filename.endsWith(".json"));
        assert dicts != null;
        for(File file:dicts) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Definition definition = new Gson().fromJson(br, new TypeToken<Definition>() {}.getType());
                boolean testResult = smartTranslator.addDefinitionForWord(wordList[count], "ro", definition);
                System.out.println("ADDING DEFINITION FOR " + wordList[count] + ":\t" + testResult);
                assertTrue(testResult);
                count++;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    @Test
    public void addDefinitionForWordEdgeTest() {
        // edge case where we add a definition for a word that doesnt exist
        System.out.println("------ADD DEFINITION FOR WORD EDGE CASE TEST------");
        int count = 0;
        String[] wordList = {"joc"};
        SmartTranslator smartTranslator = new SmartTranslator();
        File dirDicts = new File("src/test/in/addDefinitionForWordEdge");
        File[] dicts = dirDicts.listFiles((dir, filename) -> filename.endsWith(".json"));
        assert dicts != null;
        for(File file:dicts) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Definition definition = new Gson().fromJson(br, new TypeToken<Definition>() {}.getType());
                boolean testResult = smartTranslator.addDefinitionForWord(wordList[count], "ro", definition);
                System.out.println("ADDING DEFINITION FOR " + wordList[count] + ":\t" + testResult);
                assertFalse(testResult);
                count++;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    @Test
    public void removeDefinitionTest() {
        // remove definitions from words that exist
        System.out.println("------REMOVE DEFINITION TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();

        boolean testResult = smartTranslator.removeDefinition("pisică", "ro", "Dicționar de sinonime");
        System.out.println("REMOVE \"Dicționar de sinonime\" FROM \"pisică\":\t" + testResult);
        assertTrue(testResult);

        testResult = smartTranslator.removeDefinition("chat", "fr", "Larousse");
        System.out.println("REMOVE \"Larousse\" FROM \"chat\":\t" + testResult);
        assertTrue(testResult);

        // edge cases removes a definition that doesnt exist and from word that doesnt exist
        System.out.println("\n---REMOVE WORD EDGE CASE TEST---");
        testResult = smartTranslator.removeDefinition("pisică", "ro", "Larousse");
        System.out.println("REMOVE \"Larousse\" FROM \"pisică\":\t" + testResult);
        assertFalse(testResult);

        testResult = smartTranslator.removeDefinition("joc", "ro", "Larousse");
        System.out.println("REMOVE \"Larousse\" FROM \"joc\":\t" + testResult);
        assertFalse(testResult);

        System.out.println();
    }

    @Test
    public void translateWordTest() {
        // translates 2 words that exist and a word that exists in one language but not the other
        System.out.println("------TRANSLATE WORD TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        boolean testResult;

        String result = smartTranslator.translateWord("pisică", "ro", "fr");
        testResult = result.compareTo("chat") == 0;
        System.out.println("TRANSLATING \"pisică\" RESULT: " + result);
        assertTrue(testResult);

        result = smartTranslator.translateWord("mânca", "ro", "fr");
        testResult = result.compareTo("manger") == 0;
        System.out.println("TRANSLATING \"mânca\" RESULT: " + result);
        assertTrue(testResult);

        System.out.println("\n------TRANSLATE WORD EDGE CASE TEST------");
        result = smartTranslator.translateWord("joc", "ro", "fr");
        testResult = result.compareTo("jeu") == 0;
        System.out.println("TRANSLATING \"joc\" RESULT: " + result);
        assertFalse(testResult);

        result = smartTranslator.translateWord("jeu", "fr", "ro");
        testResult = result.compareTo("joc") == 0;
        System.out.println("TRANSLATING \"jeu\" RESULT: " + result);
        assertFalse(testResult);

        System.out.println();
    }

    @Test
    public void translateSentenceTest() {
        // translates sentences both with words in dictionary and without
        // edge case tests for translating to a language that doesnt exist
        // as well for no words in either dictionary
        System.out.println("------TRANSLATE SENTENCE TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        boolean testResult;

        String result = smartTranslator.translateSentence("pisică mânca joc", "ro", "fr");
        testResult = result.compareTo("chat manger joc") == 0;
        System.out.println("TRANSLATING \"pisică mânca joc\" FROM \"ro\" TO \"fr\" RESULT: " + result);
        assertTrue(testResult);

        result = smartTranslator.translateSentence("chien manger jeu", "fr", "ro");
        testResult = result.compareTo("chien mânca jeu") == 0;
        System.out.println("TRANSLATING \"jeu manger chien\" FROM \"fr\" TO \"ro\" RESULT: " + result);
        assertTrue(testResult);

        System.out.println("\n------TRANSLATE SENTENCE EDGE CASE TEST------");
        result = smartTranslator.translateWord("pisică mânca joc", "ro", "en");
        testResult = result.compareTo("pisică mânca joc") == 0;
        System.out.println("TRANSLATING \"pisică mânca joc\" FROM \"ro\" TO \"en\" RESULT: " + result);
        assertTrue(testResult);

        result = smartTranslator.translateWord("test test2 test3", "ro", "fr");
        testResult = result.compareTo("test test2 test3") == 0;
        System.out.println("TRANSLATING \"test test2 test3\" FROM \"ro\" TO \"en\" RESULT: " + result);
        assertTrue(testResult);

        System.out.println();
    }

    @Test
    public void translateSentencesTest() {
        // first two tests are normal
        // edge case tests for first words not having an entry
        System.out.println("------TRANSLATE SENTENCES TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        boolean testResult = true;

        ArrayList<String> results = smartTranslator.translateSentences("pisică mânca joc", "ro", "fr");
        System.out.println("TRANSLATING \"pisică mânca joc\" FROM \"ro\" TO \"fr\" RESULT: " + results);
        int count = 0;
        String[] expectedResults = {"chat manger joc", "greffier manger joc", "mistigri manger joc"};
        for(String string:results) {
            if(string.compareTo(expectedResults[count]) != 0) {
                testResult = false;
            }
            count++;
        }
        assertTrue(testResult);

        results = smartTranslator.translateSentences("pisică mânca joc", "ro", "en");
        System.out.println("TRANSLATING \"pisică mânca joc\" FROM \"ro\" TO \"en\" RESULT: " + results);
        count = 0;
        expectedResults = new String[]{"pisică mânca joc"};
        for(String string:results) {
            if(string.compareTo(expectedResults[count]) != 0) {
                testResult = false;
            }
            count++;
        }
        assertTrue(testResult);

        System.out.println("\n------TRANSLATE SENTENCE EDGE CASE TEST------");
        results = smartTranslator.translateSentences("chien manger jeu", "fr", "ro");
        System.out.println("TRANSLATING \"chien manger jeu\" FROM \"fr\" TO \"ro\" RESULT: " + results);

        count = 0;
        expectedResults = new String[]{"chien mânca jeu", "chien mesteca jeu", "chien alimenta jeu"};
        for(String string:results) {
            if(string.compareTo(expectedResults[count]) != 0) {
                testResult = false;
            }
            count++;
        }
        assertTrue(testResult);
        System.out.println();
    }

    @Test
    public void getDefinitionsForWordTest() {
        // tests if definitions are sorted and if function returns properly
        // first two tests are normal cases
        // edge case is for a word that doesnt exist
        System.out.println("------GET DEFINITIONS FOR WORD TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        boolean testResult = true;

        ArrayList<Definition> results = smartTranslator.getDefinitionsForWord("chat", "fr");
        System.out.println("GETTING DEFINITIONS OF \"chat\" RESULT: " + results);
        int count = 0;
        Integer[] expectedResults = {2000, 2000};
        for(Definition result:results) {
            if(result.year.compareTo(expectedResults[count]) != 0) {
                testResult = false;
            }
            count++;
        }
        assertTrue(testResult);
        assertEquals(2, count);

        results = smartTranslator.getDefinitionsForWord("merge", "ro");
        System.out.println("GETTING DEFINITIONS OF \"merge\" RESULT: " + results);
        count = 0;
        expectedResults = new Integer[]{2009, 2010};
        for(Definition result:results) {
            if(result.year.compareTo(expectedResults[count]) != 0) {
                testResult = false;
            }
            count++;
        }
        assertTrue(testResult);
        assertEquals(2, count);


        System.out.println("\n------GET DEFINITIONS FOR WORD EDGE CASE TEST------");
        results = smartTranslator.getDefinitionsForWord("joc", "ro");
        System.out.println("GETTING DEFINITIONS OF \"joc\" RESULT: " + results);
        assertNull(results);


        System.out.println();
    }

    @Test
    public void exportDictionaryTest() throws IOException {
        // exports normal dictionaries, tries exporting a fake one as well
        System.out.println("------EXPORT DICTIONARY TEST------");
        SmartTranslator smartTranslator = new SmartTranslator();
        smartTranslator.exportDictionary("fr");
        long result = Files.mismatch(Path.of("exports/fr_dict.json"), Path.of("src/test/ref/fr_dict.json"));
        System.out.println("EXPORTING \"fr\" RESULT: " + result);
        assertEquals(-1, result);

        smartTranslator.exportDictionary("ro");
        result = Files.mismatch(Path.of("exports/ro_dict.json"), Path.of("src/test/ref/ro_dict.json"));
        System.out.println("EXPORTING \"ro\" RESULT: " + result);
        assertEquals(-1, result);

        System.out.println("------EXPORT DICTIONARY EDGE CASE TEST------");
        smartTranslator.exportDictionary("en");
        File fakeDict = new File("exports/en_dict.json");
        boolean edgeResult = fakeDict.exists();
        System.out.println("EXPORTING \"en\" RESULT: " + edgeResult);
        assertFalse(edgeResult);


        System.out.println();
    }
}
