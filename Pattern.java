package evilhangman.evilhangman;

import java.util.ArrayList;

import sun.reflect.annotation.ExceptionProxy;

/**
 * Created by Ryan on 9/22/2017.
 */

public class Pattern {

    private ArrayList<Character> pattern;
    int letters;
    ArrayList<Integer> rightmost_letter_indeces;

    Pattern(){
        letters = 0;
        pattern = new ArrayList<>();
        rightmost_letter_indeces = new ArrayList<>();
    }

    Pattern(int length){
        pattern = new ArrayList<>();
        for(int i = 0; i<length; i++){
            pattern.add('-');
        }
        letters = 0;
        rightmost_letter_indeces = new ArrayList<Integer>(); //will be ordered in terms of how far to the right a particular occurence is
    }

    Pattern(String word, char guess){
        letters = 0;
        pattern = new ArrayList<>();
        for(int i = 0; i < word.length(); i++)
            pattern.add(i,'-');
        rightmost_letter_indeces = new ArrayList<Integer>();
        for(int i = 0; i < word.length(); i++){
            char currentchar = word.charAt(i);
            if(currentchar == guess){
                letters++;
                pattern.set(i,guess);
                rightmost_letter_indeces.add(0,i); //the rightmost occurrence's index will occur first
            }else{
                pattern.set(i,'-');
            }
        }
    }

    public int getLetters(){
        return letters;
    }

    public int getNumberOfOccurrences(){ return rightmost_letter_indeces.size(); }

    public int getRightmostLetterIndex(int i){
        if(i >= rightmost_letter_indeces.size())
            return -1; // WARNING this might be weird
        return rightmost_letter_indeces.get(i);
    }

    public int getSize(){ return pattern.size(); }

    public char getCharacter(int index){
        return pattern.get(index);
    }

    public String combinePatterns(Pattern p) throws Exception{
        if(p.getSize() != pattern.size()){
            throw new Exception(String.format("Error: Tried to combine patterns of different sizes %s and input: %s",toString(),p.toString()));
        }

        for(int i = 0; i < pattern.size(); i++){
            char input_char = p.getCharacter(i);

            if( Character.isAlphabetic(input_char)) {
                if( pattern.get(i) == '-') {
                    letters++;
                    pattern.set(i, input_char);
                }else{
                    throw new Exception(String.format("Error: Tried to combine conflicting patterns %s and input: %s",toString(),p.toString()));
                }
            }
        }

        return toString();
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for( char c : pattern)
            s.append(c);
        return s.toString();
    }
}
