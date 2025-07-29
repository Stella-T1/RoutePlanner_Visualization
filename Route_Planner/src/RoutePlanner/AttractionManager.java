package RoutePlanner; // Stores attraction information and provides validation methods
import java.util.*;

public class AttractionManager {
    private final Map<String, String> attractionMap;   //***********Generics, Encapsulation*************

    public AttractionManager() {
        attractionMap = new HashMap<>();
    }

    // Load data from a CSV file
    public void loadFromCSV(String filename) {
        List<String[]> data = ReadFile.readFromCSV(filename);
        for (String[] column : data) {
            String attraction = column[0];
            String city = column[1];
            attractionMap.put(attraction, city);
        }
    }

    // Save the mapping of attractions to cities as a CSV file
    public void saveAttractionsToCSV(String filename) {
        ReadFile.saveAttractionsToCSV(filename, attractionMap);
    }

    // When the user inputs a single attraction: return the city where the attraction is located
    public String getCityFor(String attractionName) {
        return attractionMap.get(attractionName);
    }

    // When the user inputs multiple attractions: return a list of cities for each attraction
    public List<String> getCitiesFor(List<String> attractions) {
        List<String> cities = new ArrayList<>();
        for (String attraction : attractions) {
            String city = attractionMap.get(attraction);
            if (city != null) {
                cities.add(city);
            }
        }
        return cities;
    }

    // Provide a read-only copy of the mapping to prevent external modification
    public Map<String, String> getAttractions() {
        // Return an unmodifiable copy of the map
        return Collections.unmodifiableMap(new HashMap<>(attractionMap));  //********Encapsulation***********
    }
}
