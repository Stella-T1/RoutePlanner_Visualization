package RoutePlanner;

import java.util.ArrayList;
import java.util.List;

// Central controller for route planning
public class RoutePlanner {
    private final AttractionManager attractionMgr;
    private final RoadNetwork roadNetwork;

    public RoutePlanner(AttractionManager attractionMgr, RoadNetwork roadNetwork) {
        this.attractionMgr = attractionMgr;
        this.roadNetwork = roadNetwork;
    }

    // Load data from files
    public void loadData(String attractionsFile, String roadsFile) {
        attractionMgr.loadFromCSV(attractionsFile);
        roadNetwork.loadFromCSV(roadsFile);
    }

    // Plan a route
    public PathResults planRoute(String startCity, String endCity, List<String> attractions, ShortestPath algorithm) {
        // Get cities corresponding to the selected attractions
        List<String> requiredCities = new ArrayList<>();   // These are the cities mapped from attractions
        for (String attraction : attractions) {
            String city = attractionMgr.getCityFor(attraction);  // Encapsulation in action: map attraction to city
            if (city != null && !requiredCities.contains(city)) {
                requiredCities.add(city);
            }
        }
        requiredCities.add(startCity); // Include the start city
        requiredCities.add(endCity);   // Include the end city
        // Next: pass these cities to the algorithm component

        // Ask the algorithm to compute a full path that includes all attraction cities
        List<String> path = algorithm.route(startCity, endCity, requiredCities);  // (start, end, required cities)

        // Calculate total distance of the computed path
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            totalDistance += roadNetwork.getDistanceBetweenCities(from, to);  // Use method to get distance
        }

        // Return results packaged in a PathResults object
        return new PathResults(totalDistance, path);
    }

    // Provide access to AttractionManager
    public AttractionManager getAttractionMgr() {
        return attractionMgr;
    }
}
