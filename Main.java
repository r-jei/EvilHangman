package evilhangman.evilhangman;

import java.io.File;

/**
 * Created by Ryan on 9/23/2017.
 */

public class Main {

    public static void main(String args[]){

        File dictionary = new File(args[0]);
        int wordlength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);

        UserInterface ui = new UserInterface();
        ui.runGame(dictionary,wordlength,guesses);
    }

}
