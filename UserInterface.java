package evilhangman.evilhangman;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Ryan on 9/23/2017.
 */

public class UserInterface {

    private EvilHangmanGame game;

    UserInterface(){
        game = new EvilHangmanGame();
    }

    public static String formatCharacters(char[] chararray){
        StringBuilder s = new StringBuilder("");
        Arrays.sort(chararray);
        for(int i=0; i<chararray.length; i++){
            s.append(chararray[i]);
            if( (i++) != chararray.length )
                s.append(' ');
        }

        return s.toString();
    }

    public void runGame(File dictionary, int wordlength, int guesses){

        game.startGame(dictionary,wordlength);
        if(game.getWordsetSize() < 1){
            System.out.println("There are no words in the dictionary of the specified length.");
            return;
        }
        Scanner user_input = new Scanner(System.in);


        while(guesses > 0){
            System.out.println(String.format("You have %d guesses left",guesses));
            String guessed = game.getGuessedLetters();
            System.out.println(String.format("Used letters: %s", guessed));
            System.out.println(String.format("Word: %s",game.getWord().toString()));


            boolean valid_input = false;
            char guess_char = 0x0000;
            int old_word_letters = game.getWord().getLetters();
            int new_word_letters = -1;
            do {
                System.out.print("Enter guess: ");
                String guess = user_input.nextLine();
                if(guess.length() == 1){
                    guess_char = guess.toCharArray()[0];
                    if(Character.isAlphabetic(guess_char)){
                        valid_input = true;
                        guess_char = Character.toLowerCase(guess_char);
                        try {
                            game.makeGuess(guess_char);
                            new_word_letters = game.getWord().getLetters();
                        }catch(IEvilHangmanGame.GuessAlreadyMadeException e){
                            valid_input = false;
                            System.out.println("You've already guessed that!");
                        }
                    }
                }
            }while(!valid_input);

            if(old_word_letters == new_word_letters){
                System.out.println(String.format("Sorry, there are no %c's",guess_char));

            }else if(new_word_letters > old_word_letters){
                if(new_word_letters - old_word_letters == 1){
                    System.out.println(String.format("Yes, there is 1 %c",guess_char));

                }else{
                    System.out.println(String.format("Yes, there are %d %c's",new_word_letters-old_word_letters,guess_char));

                }
                if(game.getWord().getLetters() == game.getWord().getSize()){
                    System.out.println("You win!");
                    System.out.println(String.format("The word was: %s",game.getWord().toString()));
                    return;
                }
                guesses++;
            }

            if(guesses-1 == 0) {
                try {
                    System.out.println("You lose!");
                    System.out.println(String.format("The word was: %s", game.getRandomWord()));
                    return;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println();
            guesses--;
        }


    }


}
