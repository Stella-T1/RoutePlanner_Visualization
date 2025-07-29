package RoutePlanner;

import java.util.*;

public abstract class ShortestPath {
    public ShortestPath(RoadNetwork roadNetwork) {
    }

    public abstract List<String> route(String startCity, String endCity, List<String> attractions);
}

// DFS Algorithm
class DFSAlgorithm extends ShortestPath {
    private final RoadNetwork roadNetwork;

    private List<String> bestPath;
    private int bestDistance;
    private List<String> currentPath;
    private int currentDistance;

    public DFSAlgorithm(RoadNetwork roadNetwork) {
        super(roadNetwork);
        this.roadNetwork = roadNetwork;
    }

    @Override
    public List<String> route(String startCity, String endCity, List<String> attractions) {
        Set<String> mustVisitCities = new HashSet<>(attractions);
        mustVisitCities.remove(startCity);
        mustVisitCities.remove(endCity);

        // initialize the search status
        bestDistance = Integer.MAX_VALUE;
        bestPath = new ArrayList<>();
        currentPath = new ArrayList<>();
        currentDistance = 0;

        // start the DFS search from the starting point
        currentPath.add(startCity);
        dfs(startCity, endCity, mustVisitCities);

        return bestPath;
    }

    private void dfs(String currentCity, String endCity, Set<String> mustVisitCities) {
        // pruning longer path
        if (currentDistance > bestDistance) return;

        if (isCompletePath(currentCity, endCity, mustVisitCities)) {
            // update the optimal solution
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(currentPath);
            }
            return;
        }

        for (RoadNetwork.Edge edge : roadNetwork.getNeighbors(currentCity)) {
            String neighbor = edge.getCity();
            int distance = edge.getDistance();

            if (currentPath.contains(neighbor)) continue; // avoid cycles

            currentPath.add(neighbor);
            currentDistance += distance;

            dfs(neighbor, endCity, mustVisitCities);

            // trace back the path and try other branches
            currentPath.remove(currentPath.size() - 1);
            currentDistance -= distance;
        }
    }

    private boolean isCompletePath(String currentCity, String endCity, Set<String> mustVisitCities) {
        if (!currentCity.equals(endCity)) return false;
        return currentPath.containsAll(mustVisitCities);
    }
}

// Dijkstra Algorithm
class DijkstraAlgorithm extends ShortestPath {
    private final RoadNetwork roadNetwork;

    public DijkstraAlgorithm(RoadNetwork roadNetwork) {
        super(roadNetwork);
        this.roadNetwork = roadNetwork;
    }

    @Override
    public List<String> route(String startCity, String endCity, List<String> attractions) {
        if (attractions.isEmpty()) {
            return findShortestPath(startCity, endCity);
        } else {
            return findOptimalRoute(startCity, endCity, attractions);
        }
    }


    // standard algorithm
    private List<String> findShortestPath(String startCity, String endCity) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        // sorted by the current shortest distance
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        distances.put(startCity, 0); // initialize
        priorityQueue.add(startCity);

        while (!priorityQueue.isEmpty()) {
            String current = priorityQueue.poll();
            if (current.equals(endCity)) {
                break;
            }

            // traverse all adjacent nodes
            for (RoadNetwork.Edge edge : roadNetwork.getNeighbors(current)) {
                String neighbor = edge.getCity();
                int newDistance = distances.get(current) + edge.getDistance();

                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    priorityQueue.add(neighbor);
                }
            }
        }

        List<String> path = new ArrayList<>();
        String current = endCity;
        while (current != null && !current.equals(startCity)) {
            path.add(0, current);
            current = previous.get(current);
        }
        if (current != null) {
            path.add(0, startCity);
        }

        return path;
    }

    // State-compressed Dijkstra
    private List<String> findOptimalRoute(String startCity, String endCity, List<String> wayPoints) {
        // initialize the waypoints
        Set<String> wayPointsSet = new HashSet<>(wayPoints);
        wayPointsSet.remove(startCity);
        wayPointsSet.remove(endCity);

        // greedy strategy
        PriorityQueue<PathNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.totalDistance));
        priorityQueue.add(new PathNode(startCity, 0, new ArrayList<>(Collections.singletonList(startCity)), new HashSet<>(wayPointsSet)));

        Map<String, Map<Set<String>, Integer>> distanceMap = new HashMap<>();
        distanceMap.put(startCity, new HashMap<>());
        distanceMap.get(startCity).put(wayPointsSet, 0);

        while (!priorityQueue.isEmpty()) {
            PathNode current = priorityQueue.poll();

            if (current.remainingCities.isEmpty() && current.currentCity.equals(endCity)) {
                return current.path;
            }

            for (RoadNetwork.Edge edge : roadNetwork.getNeighbors(current.currentCity)) {
                String neighbor = edge.getCity();
                int newDistance = current.totalDistance + edge.getDistance();
                Set<String> newRemainingCities = new HashSet<>(current.remainingCities);

                if (newRemainingCities.contains(neighbor)) {
                    newRemainingCities.remove(neighbor);
                }

                if (!distanceMap.containsKey(neighbor) || !distanceMap.get(neighbor).containsKey(newRemainingCities) || newDistance < distanceMap.get(neighbor).getOrDefault(newRemainingCities, Integer.MAX_VALUE)) {
                    distanceMap.putIfAbsent(neighbor, new HashMap<>());
                    distanceMap.get(neighbor).put(newRemainingCities, newDistance);

                    // create new path nodes and add them to the queue
                    List<String> newPath = new ArrayList<>(current.path);
                    newPath.add(neighbor);
                    priorityQueue.add(new PathNode(neighbor, newDistance, newPath, newRemainingCities));
                }
            }
        }

        return Collections.emptyList(); // cannot reach any remaining attractions
    }

    private static class PathNode {
        String currentCity;
        int totalDistance;
        List<String> path;
        Set<String> remainingCities;

        public PathNode(String currentCity, int totalDistance, List<String> path, Set<String> remainingCities) {
            this.currentCity = currentCity;
            this.totalDistance = totalDistance;
            this.path = path;
            this.remainingCities = remainingCities;
        }
    }
}