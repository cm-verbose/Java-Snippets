package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

final public class NumberGuessing {
  static int max = 100;
  static int min = 0;

  public NumberGuessing() {
    ini();
  }

  /**
   * For catching shutdowns, via CTRL + C or via other program termination methods
   */
  final private static class ShutDownMessageHandler extends Thread {
    final public void run() {
      System.out.println("\u001B[38;5;151m[NumberGuessing]\u001B[0m: Program terminated");
    }
  }

  /**
   * wraps the terminal program within a loop that informs of the shutdown of the
   * program
   */
  final public static void main(String[] args) {
    try {
      Runtime.getRuntime().addShutdownHook(new ShutDownMessageHandler());
      new NumberGuessing();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** initialises the game */
  final private static void ini() {
    System.out.println("\n\n\n\u001B[38;5;151mNumber guessing game\u001B[0m\n\n\n");
    mainLoop: while (true) {
      String startIndication = String.format("A random number has been generated (between %d and %d)", min, max);
      System.out.println(startIndication);
      int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

      gameloop: while (true) {
        Optional<String> guess = getInput("Enter your guess :");
        if (guess.isEmpty()) {
          System.out.println("Unable to read user input");
        } else {
          try {
            int userGuess = Integer.parseInt(guess.get());
            if (userGuess > max) {
              System.out.println(String.format(
                  "The number \u001B[38;5;217m%d\u001B[0m is greater than the upper bound of \u001B[38;5;151%d\u001B[0m",
                  userGuess, max));
              continue gameloop;
            } else if (userGuess < min) {
              System.out.println(String.format(
                  "The number \u001B[38;5;217m%d\u001B[0m is smaller than the lower bound of \u001B[38;5;151%d\u001B[0m",
                  userGuess, min));
              continue gameloop;
            }
          } catch (NumberFormatException e) {
            System.out
                .println(String.format("Couldn't format input to an integer value: \u001B[38;5;217m%s\u001B[0m", e));
            continue gameloop;
          }
        }
        int guessedNumber = Integer.parseInt(guess.get());

        if (guessedNumber == randomNumber) {
          System.out.println(String.format("You guessed the number (%d) !", randomNumber));
          break gameloop;
        } else if (guessedNumber < randomNumber) {
          System.out.println(String.format("%d is too \u001B[38;5;217mlow\u001B[0m !", guessedNumber));
          continue gameloop;
        } else if (guessedNumber > randomNumber) {
          System.out.println(String.format("%d is too \u001B[38;5;151mhigh\u001B[0m !", guessedNumber));
          continue gameloop;
        }
      }

      restartloop: while (true) {
        Optional<String> playAgain = getInput(
            "Do you want to play again ? \u001B[38;5;151myes [y]\u001B[0m / \u001B[38;5;217mno [n]\u001B[0m");
        if (playAgain.isEmpty()) {
          System.out.println("Unable to read user input");
          continue restartloop;
        } else {
          String option = playAgain.get().trim();
          if (option.equalsIgnoreCase("yes") || option.equalsIgnoreCase("y")) {
            break restartloop;
          } else if (option.equalsIgnoreCase("no") || option.equalsIgnoreCase("n")) {
            break mainLoop;
          } else {
            System.out.println("Invalid invalid option inputed");
            continue restartloop;
          }
        }
      }
    }
  }

  final private static Optional<String> getInput(String prompt) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println(prompt);
      String content = reader.readLine();
      return Optional.of(content);
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
