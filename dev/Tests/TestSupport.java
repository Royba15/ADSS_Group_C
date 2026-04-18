import presentation.Main;
import service.RoleRegistry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestSupport {

    public static void runTest(String inputFileName, String expectedFileName) throws Exception {
        Path inputPath = Path.of("docs", "in", inputFileName);
        Path expectedPath = Path.of("docs", "out", expectedFileName);

        //read input txt file
        String input = Files.readString(inputPath, StandardCharsets.UTF_8);
        String expected = normalize(Files.readString(expectedPath, StandardCharsets.UTF_8));

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        //read output txt file
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream testOutput = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);

        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {
            System.setIn(testInput);
            System.setOut(testOutput);

            resetSystemState();
            Main.main(new String[0]);

        } catch (Exception e) {
            System.setOut(originalOut);
            System.setIn(originalIn);

            System.out.println("FAILED: " + inputFileName);
            System.out.println("Program crashed:");
            e.printStackTrace();
            return;
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        String actual = normalize(capturedOutput.toString(StandardCharsets.UTF_8));

        if (actual.equals(expected)) {
            System.out.println("PASSED: " + inputFileName);
        } else {
            System.out.println("FAILED: " + inputFileName);
            System.out.println("Expected length: " + expected.length());
            System.out.println("Actual length: " + actual.length());

            int diffIndex = firstDifference(expected, actual);
            System.out.println("First difference index: " + diffIndex);

            printDifferenceContext(expected, actual, diffIndex);
        }
    }

    private static void resetSystemState() {
        RoleRegistry.getRoles().clear();
    }

    private static String normalize(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
    }

    private static int firstDifference(String expected, String actual) {
        int minLength = Math.min(expected.length(), actual.length());

        for (int i = 0; i < minLength; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                return i;
            }
        }

        if (expected.length() != actual.length()) {
            return minLength;
        }

        return -1;
    }

    private static void printDifferenceContext(String expected, String actual, int index) {
        if (index == -1) {
            return;
        }

        int start = Math.max(0, index - 80);
        int expectedEnd = Math.min(expected.length(), index + 120);
        int actualEnd = Math.min(actual.length(), index + 120);

        System.out.println("\nExpected around difference:");
        System.out.println(expected.substring(start, expectedEnd));

        System.out.println("\nActual around difference:");
        System.out.println(actual.substring(start, actualEnd));
    }
}
