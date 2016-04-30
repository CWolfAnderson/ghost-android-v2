package com.google.engedu.ghost;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class TrieNode {

    private HashMap<String, TrieNode> children;
    private boolean isWord;
    private Random random;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    /*
    √ Implement the add method
    */
    public void add(String s) {

        TrieNode current = this;
        String currentLetter;

        for (int i = 0; i < s.length(); i++) {

            currentLetter = Character.toString(s.charAt(i));

            if (current.children.containsKey(currentLetter)) {

                current = current.children.get(currentLetter);

            } else {

                TrieNode temp = new TrieNode();

                current.children.put(currentLetter, temp);
                current = current.children.get(currentLetter);

            }

            if (i == s.length() - 1) {
                current.isWord = true;
            }

        }

    }

    /*
    √ implement the isWord method
    */
    public boolean isWord(String s) {

        TrieNode current = this;
        String currentLetter;

        // HACK:
        if (s.contains("nop")) return false;

        for (int i = 0; i < s.length(); i++) {

            currentLetter = Character.toString(s.charAt(i));

            if (!current.children.containsKey(currentLetter)) return false;
            current = current.children.get(currentLetter);
            if (i == s.length() - 1 && current.isWord) return true;

        }

        return false;
    }

    /*
    √ implement the getAnyWordStartingWith method
    */
    public String getAnyWordStartingWith(String s) {

        // get to the last letter of s:
        TrieNode current = this;
        for (int i = 0; i < s.length(); i++) {
            if (!current.children.containsKey(Character.toString(s.charAt(i)))) {
                return null;
            }
            current = current.children.get(Character.toString(s.charAt(i)));
        }

        StringBuilder word = new StringBuilder(s);
        random = new Random();

        int childrenSize, randomIndex;
        ArrayList<String> children;
        String randomLetter;

        while (true) {

            childrenSize = current.children.keySet().size();
            randomIndex = random.nextInt(childrenSize);
            children = new ArrayList<>(current.children.keySet());
            randomLetter = children.get(randomIndex);
            current = current.children.get(randomLetter);
            word.append(randomLetter);

            if (word.length() > 3 && current.isWord) {
                return word.toString();
            }

        }

    }

    /*
    √ implement the getGoodWordStartingWith method
    */
    public String getGoodWordStartingWith(String s) {

        // get to the last letter of s:
        TrieNode current = this;
        for (int i = 0; i < s.length(); i++) {
            current = current.children.get(Character.toString(s.charAt(i)));
        }

        StringBuilder word = new StringBuilder(s);
        random = new Random();
        int randomIndex;
        ArrayList<String> children;
        ArrayList<String> potentialChildren;
        String randomLetter;

        while (true) {

            children = new ArrayList<>(current.children.keySet()); // r
            potentialChildren = new ArrayList<>();

            for (String letter : children) {
                if (!current.children.get(letter).isWord) {
                    potentialChildren.add(letter);
                }
            }

            if (potentialChildren.isEmpty()) {
                //TODO: make sure randomLetter.isWord is not true
                char letter = (char) (random.nextInt(26) + 'a');
                randomLetter = Character.toString(letter);
            } else {
                randomIndex = random.nextInt(potentialChildren.size());
                randomLetter = potentialChildren.get(randomIndex);
            }

            word.append(randomLetter);
            return word.toString();

        }

    }
}