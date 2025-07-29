package RoutePlanner;
// Encapsulate the shortest path
import java.util.List;

public class PathResults {
    private final int distance;
    private final List<String> path;

    public PathResults(int distance, List<String> path) {
        this.distance = distance;
        this.path = path;
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getPath() {
        return path;
    }
}