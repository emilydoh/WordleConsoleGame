import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.RandomAccessFile;
import java.util.Random;

public class WordleConsoleGame {
    static String guessesAndScoresSoFar = "";

    // for checking guesses: only allow upper or lowercase alphabetical letters and length 5
    private static Pattern p = Pattern.compile("^[a-zA-Z]{5}$");

    //ANSI codes to make letters green, yellow, and reset them to original color
    private static final String GREEN_CODE = "\u001B[32m";
    private static final String YELLOW_CODE = "\u001B[33m";
    private static final String RESET_CODE = "\u001B[0m";
    static ArrayList<Character> answerLettersArrayList = new ArrayList<Character>();

    public static void main(String[] args) {

        String answerWord = generateAnswerWord();

        // fill a hashmap with letters in answer word and their indices
        for (int i=0; i<5; i++) {
            answerLettersArrayList.add(answerWord.charAt(i));
        }

        System.out.println("Welcome to WordleConsoleGame!\n Press ENTER to start a new game.");
        Scanner in = new Scanner(System.in);
        while (true) {
            if (in.nextLine().equals("")) break;
        }

        System.out.println("A yellow letter indicates letter is in answer word, but in an incorrect position, a green letter indicates letter is in answer word and in correct position.");
        int roundNumber = 0;
        while(true) {
            System.out.println("Enter a 5-letter guess: ");
            String guess = in.nextLine().toUpperCase();

            // only accept valid guesses
            if (!p.matcher(guess).find()) {
                System.out.println("Invalid guess.");
            } else {
                String scoreForRound = "-----";
                StringBuilder colorFormattedGuess = new StringBuilder();
                // at the start of each new guess, set the remaining pool of letters to be the answerLettersArrayList
                ArrayList<Character> roundPool = new ArrayList<>(List.copyOf(answerLettersArrayList));
                ArrayList<Integer> indexToBeChecked = new ArrayList<>();

                for (int i = 0; i < 5; i++) {

                    // letter in answer letter pool
                    Character guessingLetter = guess.charAt(i);
                    if (roundPool.contains(guessingLetter)) {

                        // right position, right letter
                        if (roundPool.get(i)==guessingLetter) {
                            scoreForRound = scoreForRound.substring(0, i) + "X" + scoreForRound.substring(i+1, 5);
                            //replace letter with a non-letter value in the answer array to avoid causing future unwanted O's/yellow color for letters
                            roundPool.set(i, '.');
                        }

                        // wrong position, right letter
                        else {
                            scoreForRound = scoreForRound.substring(0, i) + "O" + scoreForRound.substring(i+1, 5);
                            indexToBeChecked.add(i);
                        }
                    }
                    // letter not in answer letter pool
                    else {
                        scoreForRound = scoreForRound.substring(0, i) + "-" + scoreForRound.substring(i+1, 5);
                    }

                }
                // finalize the scores by checking over to remove any unnecessary yellow letters
                for (int index : indexToBeChecked) {
                    // we need to change the O to -
                    if (!roundPool.contains(guess.charAt(index))) {
                        scoreForRound = scoreForRound.substring(0, index) + "-" + scoreForRound.substring(index+1, 5);
                    }
                }
                for (int w = 0; w<5; w++) {
                    char scoreLetter = scoreForRound.charAt(w);
                    if (scoreLetter=='O') colorFormattedGuess.append(YELLOW_CODE).append(guess.charAt(w)).append(RESET_CODE);
                    else if (scoreLetter=='X') colorFormattedGuess.append(GREEN_CODE).append(guess.charAt(w)).append(RESET_CODE);
                    else colorFormattedGuess.append(guess.charAt(w));
                }

                guessesAndScoresSoFar += colorFormattedGuess + "\n";

                System.out.println(guessesAndScoresSoFar);

                // once all the letters have been scored, need to check if game is won (or if max rounds of guesses [6] have been exceeded)
                if (scoreForRound.equals("XXXXX")) {
                    System.out.println("You won!\nIn "+ (roundNumber + 1) + " tries.");
                    break;
                }
                roundNumber++;
                if (roundNumber == 6) {
                    System.out.println("Game over, you lost.\nWord was:\n" + answerWord.toUpperCase());
                    break;
                }
            }
        }
    }

    public static String generateAnswerWord() {
        String randomWord = "WORDS";
        try {
            RandomAccessFile reader = new RandomAccessFile("src/words.txt", "r");

            Random rand = new Random();
            // set pointer to random position from 0 up to reader.length - 15
            reader.seek(rand.nextLong(reader.length()-15));

            //discard the first line, as it could start from the center of a word, choose the second
            reader.readLine();
            randomWord=reader.readLine().toUpperCase();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return randomWord;
    }
}