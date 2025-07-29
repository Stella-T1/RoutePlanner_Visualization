package Sorting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SortingBenchmark {
    private static final SortingAlgorithm[] algorithms = {
            new InsertionSort(),
            new QuickSort(),
            new MergeSort()
    };

    public static void main(String[] args) {
        String[] datasets = {
                "1000places_sorted.csv",
                "1000places_random.csv",
                "10000places_sorted.csv",
                "10000places_random.csv"
        };

        System.out.println("Dataset\tInsertion (ns)\tQuick (ns)\tMerge (ns)");
        System.out.println("------------------------------------------------");

        for (String dataset : datasets) {
            System.out.print(dataset + "\t");
            String[] data = loadData(dataset);

            for (SortingAlgorithm algorithm : algorithms) {
                String[] dataCopy = data.clone();
                long time = measureSortingTime(algorithm, dataCopy);
                System.out.print(time + "\t");
            }
            System.out.println();
        }
    }

    private static String[] loadData(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        return lines.toArray(new String[0]);
    }

    private static long measureSortingTime(SortingAlgorithm algorithm, String[] data) {
        for (int i = 0; i < 20; i++) {
            algorithm.sort(data.clone());
        }

        long totalTime = 0;
        int runs = 50;
        for (int i = 0; i < runs; i++) {
            String[] copy = data.clone();
            long startTime = System.nanoTime();
            algorithm.sort(copy);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);

            if (!isSorted(copy)) {
                throw new RuntimeException("Sorting failed for " + algorithm.getName());
            }
        }
        return totalTime / runs;
    }

    private static boolean isSorted(String[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].compareTo(arr[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }


}