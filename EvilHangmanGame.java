package evilhangman.evilhangman;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

/**
 * Created by Ryan on 9/22/2017.
 */

public class EvilHangmanGame implements IEvilHangmanGame{

    private Set<String> wordset = new HashSet<>();
    private Set<Character> guessed;
    private Pattern word;

    public String getGuessedLetters(){
        if( guessed.isEmpty()){
            return "";
        }
        StringBuilder s = new StringBuilder();
        for(char c : guessed) {
            s.append(c);
            s.append(" ");
        }
        return s.toString();
    }

    public Pattern getWord(){
        return word;
    }

    public String getRandomWord() throws Exception{
        if(wordset.size() < 1)
            throw new Exception("wordset has no words");
        Iterator<String> randomword = wordset.iterator();
        return randomword.next();
    }

    public int getWordsetSize(){
        return wordset.size();
    }

    public EvilHangmanGame(){

        guessed = new TreeSet<Character>();
        wordset = new HashSet<>();
        word = new Pattern(0);

    }

    public void setWordlength(int length){
        word = new Pattern(length);
    }

    private Map<Pattern,Set<String>> partitionGroups(char guess){
        Map<Pattern,Set<String>> groupmap;
        groupmap = new HashMap<>();
        for(String s : wordset){
            Pattern current_word_pattern = new Pattern(s,guess);
            boolean matching_key_exists = false;
            for(Pattern p : groupmap.keySet()){
                if( current_word_pattern.toString().equals( p.toString() ) ){
                    matching_key_exists = true;
                    current_word_pattern = p;
                    break;
                }
            }
            if( matching_key_exists ){
                (groupmap.get(current_word_pattern)).add(s);
            }else{
                groupmap.put(current_word_pattern,new HashSet<String>());
                (groupmap.get(current_word_pattern)).add(s);
            }
        }

        /*ArrayList<ArrayList<String>> wordgroups = new ArrayList<>();
        for(ArrayList<String> g : groupmap.values() ){
            wordgroups.add(g);
        }*/

        return groupmap;
    }

    private Set<Pattern> findZeroLetterPatterns(Collection<Pattern> patterns){
        Set<Pattern> zero_occurrences = new HashSet<>();

        for(Pattern p : patterns){
            if(p.getNumberOfOccurrences() == 0){
                zero_occurrences.add(p);
            }
        }

        return zero_occurrences;
    }

    private Set<Pattern> findFewestLetterPatterns(Collection<Pattern> patterns){
        int smallest_no_occurences = 2147000000;
        Set<Pattern> fewest_letter_patterns = new HashSet<>();

        for(Pattern p : patterns){
            if(p.getNumberOfOccurrences() < smallest_no_occurences){
                smallest_no_occurences = p.getNumberOfOccurrences();
                fewest_letter_patterns = new HashSet<>();
                fewest_letter_patterns.add(p);
            }else if(p.getNumberOfOccurrences() == smallest_no_occurences){
                fewest_letter_patterns.add(p);
            }
        }

        return fewest_letter_patterns;
    }

    private Pattern findRightHeavyPattern(Collection<Pattern> patterns)throws Exception{
        int rightmost_index = -1;
        int ordinal = 0;
        Set<Pattern> right_heavy_patterns = new HashSet<>();
        Pattern right_heavy_pattern = new Pattern();

        while(true){
            rightmost_index = -1;
            for(Pattern p : patterns){
                if(p.getRightmostLetterIndex(ordinal) > rightmost_index){
                    rightmost_index = p.getRightmostLetterIndex(ordinal);
                    right_heavy_patterns = new HashSet<>();
                    right_heavy_patterns.add(p);
                }else if(p.getRightmostLetterIndex(ordinal) == rightmost_index){
                    right_heavy_patterns.add(p);
                }
            }

            if(right_heavy_patterns.size() == 1){
                Iterator<Pattern> i = right_heavy_patterns.iterator();
                return i.next();
            }
            ordinal++;
            if(ordinal < 0){
                //System.out.println("hi mom");
                throw new Exception("findRightHeavyPattern is looping too much");
            }
        }
    }

    private Pattern prioritizeGroups(Map<Pattern,Set<String>> groups){

        Set<Pattern> zero_occurrences = findZeroLetterPatterns(groups.keySet());
        if(zero_occurrences.size() == 1){
            Iterator<Pattern> i = zero_occurrences.iterator();
            return i.next();
        }

        Set<Pattern> fewest_occurrence_patterns = findFewestLetterPatterns(groups.keySet());
        if(fewest_occurrence_patterns.size() == 1){
            Iterator<Pattern> i = fewest_occurrence_patterns.iterator();
            return i.next();
        }

        try {
            Pattern best_group_key = findRightHeavyPattern(groups.keySet());
            return best_group_key;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Map<Pattern,Set<String>> findLargestGroups(Map<Pattern,Set<String>> groups){
        int largestsize = 0;
        Map<Pattern,Set<String>> largestgroups = new HashMap<>();

        for(Pattern p : groups.keySet()){
            Set<String> currentgroup = groups.get(p);
            if(currentgroup.size() > largestsize){
                largestsize = currentgroup.size();
                largestgroups = new HashMap<>();
                largestgroups.put(p,currentgroup);
            }else if (currentgroup.size() == largestsize) {
                largestgroups.put(p,currentgroup);
            }
        }

        return largestgroups;
    }

    private Pattern selectBestGroup(Map<Pattern, Set<String>> groups)throws Exception{
        Map<Pattern,Set<String>> bestgroups = new HashMap<>();

        bestgroups = findLargestGroups(groups);
        if(bestgroups.keySet().size() == 1) {
            Iterator<Pattern> i = bestgroups.keySet().iterator();
            return i.next();
        }

        Pattern new_best_group = prioritizeGroups(bestgroups);
        if(new_best_group == null){
            throw new Exception("prioritizeGroups returned null value");
        }
        return new_best_group;
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if(guessed.contains(guess)){
            throw new GuessAlreadyMadeException();
        }else if(!Character.isAlphabetic(guess)){
            System.out.println("Makeguess was passed an nonalphabetic character");
        }
        Map<Pattern, Set<String>> wordgroups = partitionGroups(guess);

        try {
            Pattern best_group_key = selectBestGroup(wordgroups);
            wordset = wordgroups.get(best_group_key);
            word.combinePatterns(best_group_key);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        guessed.add(guess);
        return wordset;
    }

    @Override
    public void startGame(File dictionary, int wordLength){
        setWordlength(wordLength);
        try {
            Scanner in = new Scanner(dictionary);
            in.useDelimiter("[^A-Za-z]");
            while(in.hasNext()){
                String nextword = in.next();
                if(nextword.length() == wordLength ) {
                    nextword = nextword.toLowerCase();
                    wordset.add(nextword);
                }
            }


        }catch(IOException ioexc){
            System.out.println( String.format("IOException while reading dictionary:\n%s", ioexc.getMessage()) );
        }
    }
}
