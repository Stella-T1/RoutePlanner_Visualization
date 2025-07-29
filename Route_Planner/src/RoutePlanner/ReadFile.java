package RoutePlanner;

import java.io.*;
import java.util.*;

public class ReadFile {

    // Read data from CSV file and automatically skip the first header row
    public static List<String[]> readFromCSV(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                // Determine file type based on filename
                if (filename.contains("attractions")) {
                    if (parts.length == 2) {
                        parts = Arrays.stream(parts)
                                .map(String::trim)
                                .toArray(String[]::new);
                        data.add(parts);
                    } else {
                        System.err.println("Skipped invalid row in attractions file: " + line);
                    }
                } else {
                    if (parts.length == 3) {
                        parts = Arrays.stream(parts)
                                .map(String::trim)
                                .toArray(String[]::new);
                        data.add(parts);
                    } else {
                        System.err.println("Skipped invalid row in roads file: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return data;
    }

    // Write data to CSV file, allowing the program to repeatedly read and update data
    // without manually modifying the CSV file. The design scenario is:
    // If a user adds new attractions or the program calculates new relationships
    // between attractions and cities, this method can write the new mappings to the CSV file.
    public static void writeToCSV(String filename, List<String[]> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String[] line : data) {
                writer.write(String.join(",", line));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    // Save the modified mappings of cities and attractions to a CSV file
    public static void saveAttractionsToCSV(String filename, Map<String, String> attractionMap) {
        List<String[]> data = new ArrayList<>();
        for (Map.Entry<String, String> entry : attractionMap.entrySet()) {
            data.add(new String[]{entry.getKey(), entry.getValue()});
        }
        writeToCSV(filename, data);
    }
}
