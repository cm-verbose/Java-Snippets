package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

final public class FileDisplay {
  final String fileDisplayFileName;

  public FileDisplay(String fileName) {
    fileDisplayFileName = fileName;
  }

  /** Handles program shutdown via CTRL + C */
  final private static class ShutDownMessageHandler extends Thread {
    public void run() {
      System.out.print("\033[H\033[2J");
      System.out.flush();
      System.out.println("[FileDisplay] : Terminated");
    }
  }

  /** Reads the 5 first lines of the file, */
  final public void displayHead() throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(fileDisplayFileName));
    try {
      String currentLine = reader.readLine();
      int counter = 0;
      while (currentLine != null) {
        if (counter == 5)
          break;
        display(currentLine);
        currentLine = reader.readLine();
        counter += 1;
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Couldn't read line");
    }
  }

  /** Reads the integral contents of the file */
  final public void displayContents() throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(fileDisplayFileName));
    try {
      String currentLine = reader.readLine();
      while (currentLine != null) {
        display(currentLine);
        currentLine = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Couldn't read line");
    }
  }

  /** Reads file contents, whilst enumerating the corresponding line numbers */
  final public void displayWithLineNumbers() throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(fileDisplayFileName));
    try {
      String currentLine = reader.readLine();
      int counter = 0;
      while (currentLine != null) {
        /* the text is colored to disambiguate from the actual content */
        display(String.format("\u001B[38;5;194m%d : \u001B[0m%s", counter + 1, currentLine));
        currentLine = reader.readLine();
        counter += 1;
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Couldn't read line");
    }
  }

  /**
   * 
   * @param <T> Type that contrains values to those that implement CharSequence
   * @param x   The file content that must be displayed
   */
  final public <T extends CharSequence> void display(T x) {
    System.out.println(x);
  }

  final public static void main(String args[]) {
    try {
      Runtime.getRuntime().addShutdownHook(new ShutDownMessageHandler());
      final Optional<String> filePath = getInput("FileDisplay :) pls enter file path");
      if (filePath.isEmpty()) {
        System.out.println("Failed reading input path :(");
        return;
      } else {
        String userPath = (String) filePath.get();
        Path path = Paths.get(userPath);

        if (!Files.exists(path)) {
          System.out.println(String.format("The path %s does not exist :(", path.toString()));
          return;
        }
      }
      final Path userPath = Paths.get((String) filePath.get());
      System.out.println(String.format("Reading %s...", userPath.toString()));
      FileDisplay fileDisplay = new FileDisplay(userPath.getFileName().toString());

      while (true) {
        Optional<String> userInput = getInput(
            "\n[FileDisplay] : Enter your display action :) : \n[head] \n[contents] \n[line_numbers] \n[exit]");
        if (filePath.isEmpty()) {
          System.out.println("Failed reading input action :(");
          continue;
        } else {
          String u = userInput.get().trim();
          if (!(u.equalsIgnoreCase("head") || u.equalsIgnoreCase("contents") || u.equalsIgnoreCase("line_numbers"))) {
            System.out.println("Invalid action :(");
            continue;
          }
        }
        String input = (String) userInput.get();
        try {
          switch (input) {
            case "head":
              fileDisplay.displayHead();
              break;

            case "contents":
              fileDisplay.displayContents();
              break;

            case "line_numbers":
              fileDisplay.displayWithLineNumbers();
              break;
          }
        } catch (FileNotFoundException fileNotfoundError) {
          System.err.println(String.format("File not found at the instant of the reading: %s", fileNotfoundError));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
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
