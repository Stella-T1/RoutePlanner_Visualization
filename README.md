# RoutePlanner_Visualization
A Java implementation of pathfinding algorithms (DFS, Dijkstra's) for optimal route planning with mandatory attractions, featuring JavaFX visualization and sorting algorithm benchmarks.

## 🧭 Project Abstract

This project presents a route planning visualization system implemented in Java, aiming to support the analysis and comparison of classic graph search algorithms, including Depth-First Search (DFS), Dijkstra's Algorithm. Through an interactive GUI interface, users can manually select origin and destination nodes within a simulated road network and observe the algorithm's execution process in real-time. The system is designed as an educational tool to enhance algorithmic understanding and support decision-making in navigation contexts.

---

## 📌 Motivation and Problem Statement

Graph search algorithms are foundational in various computer science domains, particularly in transportation systems, robotics, and AI planning. However, their abstract nature often makes them challenging for students to comprehend without visual support. While many theoretical resources exist, few offer interactive visualizations that simultaneously demonstrate the structural behavior of search algorithms and their computational outcomes.

This project addresses the gap by providing an intuitive visualization of graph traversal and pathfinding logic, allowing users to explore the behavior of different algorithms under identical conditions. The system promotes algorithmic literacy through direct interaction and visual feedback.

---

## 🎯 Objectives

- To implement and visualize multiple graph-based pathfinding algorithms within a unified system.
- To support user-driven comparison of algorithm efficiency and route optimality.
- To design an intuitive and minimal graphical interface suitable for educational and demonstrative use.
- To provide a modular codebase that can be extended with new datasets or algorithms.

---

## ⚙️ System Architecture

The system is developed in Java and structured using object-oriented principles. The architecture consists of the following core modules:

- **`RoadNetwork.java`**: Constructs the graph using an adjacency list representation of cities and roads.
- **`AttractionManager.java`**: Manages metadata for cities/attractions.
- **`DFSPathFinder.java`**, **`DijkstraPathFinder.java`**, **`AStarPathFinder.java`**: Individual classes implementing the respective search algorithms.
- **`Visualization.java`**: GUI implementation using JavaFX.
- **`PathResults.java`**: Encapsulates the final path and distance computed.
- **`Main.java`**: Entry point for launching the system.

Each algorithm operates over the same abstract graph structure, enabling side-by-side analysis with consistent data.

---

## 🖥️ Visualization Features

- **Interactive City Selection**: Choose start and end nodes via dropdown interface.
- **Algorithm Switching**: Toggle between DFS, Dijkstra for comparative evaluation.
- **Route Highlighting**: Display of visited nodes, shortest path, and total distance.
- **Graphical Interface**: Node-edge structure displayed in a custom 2D layout.

---

## 🔧 Installation and Execution

- Java Development Kit (JDK 8 or later)
- Java-compatible IDE (e.g., IntelliJ IDEA or Eclipse)
