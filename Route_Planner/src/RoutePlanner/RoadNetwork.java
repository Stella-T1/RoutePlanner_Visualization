package RoutePlanner;

import java.util.*;

public class RoadNetwork {
    // Define the graph data structure using an adjacency list
    // Good choice for sparse graphs: memory-efficient and fast lookup
    private final Map<String, List<Edge>> graph;   //***********Good data structure: adjacency list***********

    public RoadNetwork() {
        this.graph = new HashMap<>();
    }

    // Load road data from a CSV file
    // Each row in the CSV should be in the format: CityA, CityB, Distance
    public void loadFromCSV(String filename) {
        List<String[]> data = ReadFile.readFromCSV(filename);
        for (String[] column : data) {
            if (column.length == 3) {
                String cityA = column[0];
                String cityB = column[1];
                int distance = Integer.parseInt(column[2]);

                // Add bidirectional edges: roads are two-way between cities
                addEdge(cityA, cityB, distance);
                addEdge(cityB, cityA, distance);

                System.out.println("Loaded road: " + cityA + " to " + cityB + " with distance " + distance);
            }
        }
    }

    // Add a single edge from start to destination with a given distance
    private void addEdge(String start, String destination, int distance) {
        graph.putIfAbsent(start, new ArrayList<>()); // Initialize the adjacency list if not present
        graph.get(start).add(new Edge(destination, distance)); // Add the edge to the list
    }

    // Get all neighboring cities (edges) from a given city
    public List<Edge> getNeighbors(String city) {
        return graph.getOrDefault(city, Collections.emptyList()); // Return empty list if city not found
    }

    // Get all cities in the network
    public Set<String> getCities() {
        return graph.keySet();
    }

    // Get the direct distance between two cities
    // Returns -1 if the cities are not directly connected
    public int getDistanceBetweenCities(String start, String destination) {
        for (Edge edge : graph.getOrDefault(start, Collections.emptyList())) {
            if (edge.city.equals(destination)) {
                return edge.distance;
            }
        }
        return -1; // Indicates no direct connection
    }

    // Inner static class representing an edge in the graph
    public static class Edge {
        public String city;       // Destination city
        public int distance;      // Distance to the destination

        public Edge(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }

        public String getCity() {
            return city;
        }

        public int getDistance() {
            return distance;
        }
    }
}
