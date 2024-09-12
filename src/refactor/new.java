package src;

import javakara.JavaKaraProgram;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Main extends JavaKaraProgram {

    // Main entry function
    public void myProgram() {
        setWorldSize();
        accessWorlds();
    }

    // Allows user to set or reset the world size
    public void setWorldSize() {
        int WorldX = world.getSizeX();
        int WorldY = world.getSizeY();
        boolean done = false;

        while (!done) {
            try {
                String input = tools.stringInput(
                        "Current World Size: " + WorldX + "x" + WorldY + "\n" +
                        "Max: 99x99, Min: 1x1" + "\n" +
                        "Press OK to leave as is, Cancel to reset (9x9)\nOr enter new size (e.g., 9x9)"
                ).trim();

                if (input.isEmpty()) {
                    done = true;
                    continue;
                }

                String[] WorldSize = input.split("x");
                int newX = Integer.parseInt(WorldSize[0]);
                int newY = Integer.parseInt(WorldSize[1]);

                if (newX > 99 || newY > 99 || newX < 1 || newY < 1) {
                    throw new Exception("Invalid size");
                }

                world.setSize(newX, newY);
                done = true;

            } catch (Exception e) {
                if (e instanceof NumberFormatException || e.getMessage().contains("Invalid size")) {
                    tools.showMessage("Please input a valid world size (e.g., '5x5'). Max: 99x99, Min: 1x1.");
                } else if (e instanceof NullPointerException) {
                    world.setSize(9, 9);
                    done = true;
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }
    }

    // Access various project scenarios based on user input
    public void accessWorlds() {
        world.clearAll();
        String chosenProject = chooseProject();

        switch (chosenProject) {
            case "Maze":
            case "1":
                Project_maze();
                break;
            case "Collect All Leaf":
            case "2":
                Project_collectAllLeaf();
                break;
            case "Collect Leaf If Tree is Left and Right":
            case "3":
                Project_collectLeafIfTreeLeftRight();
                break;
            case "Pacman":
            case "4":
                Project_pacman();
                break;
            case "Invert":
            case "5":
                Project_invert();
                break;
            default:
                tools.showMessage("No valid project selected. Exiting...");
                break;
        }
    }

    // Choose project from the list
    public String chooseProject() {
        String[] ProjectsArray = {
            "1", "Maze", 
            "2", "Collect All Leaf", 
            "3", "Collect Leaf If Tree is Left and Right", 
            "4", "Pacman", 
            "5", "Invert"
        };

        boolean done = false;
        String chosenProject = null;

        while (!done) {
            try {
                chosenProject = tools.stringInput(
                    "Projects:\n\n" + String.join("\n", ProjectsArray) + "\n" +
                    "Enter project name to select:"
                ).trim();

                if (!Arrays.asList(ProjectsArray).contains(chosenProject)) {
                    throw new Exception("Invalid project selection");
                }
                done = true;

            } catch (Exception e) {
                if (e.getMessage().contains("Invalid project selection")) {
                    tools.showMessage("Please select a valid project.");
                } else if (e instanceof NullPointerException) {
                    tools.showMessage("No project selected -> Exiting...");
                    done = true;
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }
        return chosenProject;
    }

    // Maze Project
    public void Project_maze() {
        generateFullMaze();
        kara.setPosition(findEmptyCellX(), findEmptyCellY());
        searchMaze();
    }

    // Generates a maze by filling trees and clearing paths
    public void generateFullMaze() {
        for (int x = 0; x < world.getSizeX(); x++) {
            for (int y = 0; y < world.getSizeY(); y++) {
                world.setTree(x, y, true);
            }
        }

        int startX = getValidStartPosition();
        int startY = getValidStartPosition();
        generateMaze(startX, startY);
    }

    // Randomizes and recursively generates the maze
    public void generateMaze(int x, int y) {
        world.setTree(x, y, false);
        int[][] directions = { { -2, 0 }, { 2, 0 }, { 0, -2 }, { 0, 2 } };
        randomizeDirections(directions);

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidCell(newX, newY)) {
                int midX = (x + newX) / 2;
                int midY = (y + newY) / 2;

                if (world.isTree(midX, midY) && world.isTree(newX, newY) && amountOfFreeSpacesAround(newX, newY) == 0) {
                    world.setTree(midX, midY, false);
                    world.setLeaf(midX, midY, tools.random(10) < 5);
                    generateMaze(newX, newY);
                }
            }
        }
    }

    // Pacman Project: Set up and generate a path for Kara to follow
    public void Project_pacman() {
        generateFullMaze();  // Reuse maze generation logic for simplicity
        int pathLength = Integer.parseInt(tools.stringInput("Path Length: "));
        generatePath(findEmptyCellX(), findEmptyCellY(), pathLength);
        followPath();  // Implement path-following logic later
    }

    // Generates a path in the world for Pacman-like movement
    public void generatePath(int x, int y, int length) {
        for (int i = 0; i < length; i++) {
            if (isValidCell(x, y) && !world.isTree(x, y)) {
                world.setLeaf(x, y, true);
            }
            x += tools.random(3) - 1;  // Random horizontal movement
            y += tools.random(3) - 1;  // Random vertical movement
        }
    }

    // Have Kara follow a path to collect leaves
    public void followPath() {
        // This method will have Kara follow the generated path to collect leaves
        // Implementation to be done
    }

    // Utility methods (Randomize directions, validate cell positions, etc.)
    public void randomizeDirections(int[][] directions) {
        List<int[]> directionList = Arrays.asList(directions);
        java.util.Collections.shuffle(directionList);
        directionList.toArray(directions);
    }

    public boolean isValidCell(int x, int y) {
        return x >= 0 && y >= 0 && x < world.getSizeX() && y < world.getSizeY();
    }

    public int amountOfFreeSpacesAround(int x, int y) {
        int count = 0;
        int[][] neighbors = { { x + 1, y }, { x - 1, y }, { x, y + 1 }, { x, y - 1 } };

        for (int[] cell : neighbors) {
            if (isValidCell(cell[0], cell[1]) && !world.isTree(cell[0], cell[1])) {
                count++;
            }
        }
        return count;
    }

    // Find a valid start position within the world
    public int getValidStartPosition() {
        return tools.random(world.getSizeX() - 1);
    }

    // Find empty cells in the world
    public int findEmptyCellX() {
        // Implementation to find a valid empty X position for Kara
        return 0;
    }

    public int findEmptyCellY() {
        // Implementation to find a valid empty Y position for Kara
        return 0;
    }

    // Project to collect all leaves in the world
    public void Project_collectAllLeaf() {
        collectAllLeaves();
    }

    public void collectAllLeaves() {
        while (true) {
            if (kara.onLeaf()) {
                kara.removeLeaf();
            }
            if (frontIsClear()) {
                kara.move();
            } else if (kara.treeLeft()) {
                kara.turnRight();
            } else {
                kara.turnLeft();
            }
        }
    }

    // Project to collect leaf if there are trees on the left and right
    public void Project_collectLeafIfTreeLeftRight() {
        collectLeafIfTreeLeftRight();
    }

    public void collectLeafIfTreeLeftRight() {
        while (true) {
            if (kara.onLeaf() && kara.treeLeft() && kara.treeRight()) {
                kara.removeLeaf();
            }
            if (frontIsClear()) {
                kara.move();
            } else if (kara.treeLeft()) {
                kara.turnRight();
            } else {
                kara.turnLeft();
            }
        }
    }

    // Invert all leaves in the world
    public void Project_invert() {
        invertAllLeaves();
    }

    public void invertAllLeaves() {
        for (int x = 0; x < world.getSizeX(); x++) {
            for (int y = 0; y < world.getSizeY(); y++) {
                if (world.isLeaf(x, y)) {
                    world.setLeaf(x, y, false);
                } else {
                    world.setLeaf(x, y, true);
                }
            }
        }
    }

    // Search the maze (method to be implemented for the Maze project)
    public void searchMaze() {
        // Maze search logic to be implemented
    }
}
