package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random rand = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    /*
    √ If prefix is empty return a randomly selected word from the words ArrayList
    √ Otherwise, perform a binary search over the words ArrayList until you find a word that starts with the given prefix and return it.
    √ If no such word exists, return null
     */
    @Override
    public String getAnyWordStartingWith(String prefix) {

        if (prefix.length() == 0) {
            String word =  words.get(rand.nextInt(words.size()));
            Log.d("test", "sending " + word);
            return word;
        }

        int low = 0, high = words.size()-1, mid;


        while (low <= high) {
            mid = (low + high) / 2;
            if (words.get(mid).startsWith(prefix)) {
                Log.d("test", words.get(mid) + "starts with " + prefix);
                Log.d("test", "returning " + words.get(mid));
                return words.get(mid);
            } else if (words.get(mid).compareTo(prefix) > 0) { // target is lower than middle (positive number means target comes before)
                // Log.d("test", prefix + " comes before " + words.get(mid));
                high = mid - 1;
            } else { // target is greater than middle (negative number means target comes after)
                // Log.d("test", prefix + " comes after " + words.get(mid));
                low = mid + 1;
            }
        }

        return null;

    }

    @Override
    public String getGoodWordStartingWith(String prefix) {

        return null;
    }
}