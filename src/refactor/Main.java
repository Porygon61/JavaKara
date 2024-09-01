//last updated 01.09.24 20:00

package src.refactor;

import javakara.JavaKaraProgram;

import java.util.Arrays;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Main extends JavaKaraProgram {

    // Main program entry point
    public void myProgram() {
        setWorldSize();
        accessWorlds();
    }

    // Handles setting the world size based on user input
    public void setWorldSize() {
        int[] currentSize = getCurrentWorldSize();
        boolean done = false;

        while (!done) {
            String input = getWorldSizeInput(currentSize);
            if (input.isEmpty()) {
                done = true;
                continue;
            }
            int[] newSize = parseWorldSizeInput(input);
            if (newSize != null) {
                updateWorldSize(newSize);
                done = true;
            }
        }
    }

    // Returns the current world size as an array
    private int[] getCurrentWorldSize() {
        return new int[] { world.getSizeX(), world.getSizeY() };
    }

    // Retrieves user input for the desired world size
    private String getWorldSizeInput(int[] currentSize) {
        return tools.stringInput(
                "Current World Size: " + currentSize[0] + "x" + currentSize[1] + "\n" +
                        "Max: 99x99, Min: 1x1" + "\n" +
                        "Press OK to leave as is, Abbrechen to reset(9x9)\nOr enter new size (e.g., 9x9)")
                .trim();
    }

    // Parses the user input and validates the new world size
    private int[] parseWorldSizeInput(String input) {
        try {
            String[] WorldSize = input.split("x");
            int newX = Integer.parseInt(WorldSize[0]);
            int newY = Integer.parseInt(WorldSize[1]);
            if (isValidWorldSize(newX, newY)) {
                return new int[] { newX, newY };
            } else {
                throw new Exception("Invalid size");
            }
        } catch (Exception e) {
            handleSizeInputError(e);
            return null;
        }
    }

    // Checks if the provided world size is within valid limits
    private boolean isValidWorldSize(int x, int y) {
        return x >= 1 && x <= 99 && y >= 1 && y <= 99;
    }

    // Updates the world size with new dimensions
    private void updateWorldSize(int[] newSize) {
        world.setSize(newSize[0], newSize[1]);
    }

    // Handles errors during size input parsing
    private void handleSizeInputError(Exception e) {
        if (e instanceof NumberFormatException || e.getMessage().contains("Invalid size")) {
            tools.showMessage("Please input a valid world size (e.g., '5x5'). Max: 99x99, Min: 1x1.");
        } else if (e instanceof NullPointerException) {
            world.setSize(9, 9);
        } else {
            tools.showMessage("Unexpected error: " + e.getMessage());
        }
    }

    // Clears the world and accesses the selected project
    public void accessWorlds() {
        world.clearAll();
        String chosenProject = chooseProject();
        executeProject(chosenProject);
    }

    // Executes the selected project based on user input
    private void executeProject(String chosenProject) {
        switch (chosenProject) {
            case "Maze":
                Project_maze();
                break;
            case "Collect All Leaf":
                Project_collectAllLeaf();
                break;
            case "Collect Leaf If Tree is Left and Right":
                Project_collectLeafIfTreeLeftRight();
                break;
            default:
                // tools.showMessage("No project selected!");
                break;
        }
    }

    // Presents the list of available projects to the user and returns their choice
    public String chooseProject() {
        String[] ProjectsArray = {
                "Maze",
                "Collect All Leaf",
                "Collect Leaf If Tree is Left and Right"
        };
        String projectsList = String.join("\n", ProjectsArray);

        boolean done = false;
        String chosenProject = null;
        while (!done) {
            try {
                chosenProject = tools.stringInput(
                        "Projects:\n" + projectsList + "\n" + "\n" +
                                "Enter Project name to select:")
                        .trim();

                if (!Arrays.asList(ProjectsArray).contains(chosenProject)) {
                    throw new Exception("Invalid project selection");
                }

                done = true;

            } catch (Exception e) {
                if (e.getMessage().contains("Invalid project selection")) {
                    tools.showMessage("Please select a valid project from the list.");
                } else if (e instanceof NullPointerException) {
                    tools.showMessage("No project selected -> Exiting...");
                    done = true;
                    chosenProject = ""; // Exit without selecting a project
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }
        return chosenProject;
    }

    // Generates a maze in the world and places Kara at the starting position
    public void Project_maze() {
        fillWorldWithTrees();
        int[] start = getRandomStartPosition();
        generateMaze(start[0], start[1]);
        kara.setPosition(start[0], start[1]);
        searchMaze();
    }

    // Fills the world entirely with trees
    private void fillWorldWithTrees() {
        for (int x = 0; x < world.getSizeX(); x++) {
            for (int y = 0; y < world.getSizeY(); y++) {
                world.setTree(x, y, true);
            }
        }
    }

    // Returns a random valid starting position within the world
    private int[] getRandomStartPosition() {
        int startX = tools.random(world.getSizeX() - 1);
        int startY = tools.random(world.getSizeY() - 1);
        world.setTree(startX, startY, false);
        return new int[] { startX, startY };
    }

    // Recursively generates a maze from the given position
    public void generateMaze(int x, int y) {
        world.setTree(x, y, false);
        int[][] directions = getRandomizedDirections();
        for (int[] dir : directions) {
            if (canCarve(x, y, dir)) {
                carvePath(x, y, dir);
                generateMaze(x + dir[0], y + dir[1]);
            }
        }
    }

    // Returns a randomized array of possible movement directions
    private int[][] getRandomizedDirections() {
        int[][] directions = {
                { -2, 0 }, { 2, 0 }, { 0, -2 }, { 0, 2 }
        };
        randomizeDirections(directions);
        return directions;
    }

    // Determines if the maze can carve a path in the given direction
    private boolean canCarve(int x, int y, int[] dir) {
        int newX = x + dir[0];
        int newY = y + dir[1];
        return isValidCell(newX, newY) && world.isTree(newX, newY) && amountOfFreeSpacesAround(newX, newY) == 0;
    }

    // Carves a path in the maze between the current position and the target
    // position
    private void carvePath(int x, int y, int[] dir) {
        int midX = (x + dir[0] + x) / 2;
        int midY = (y + dir[1] + y) / 2;
        world.setTree(midX, midY, false);
    }

    // Randomizes the order of directions using the Fisher-Yates algorithm
    public void randomizeDirections(int[][] directions) {
        for (int i = directions.length - 1; i > 0; i--) {
            int j = tools.random(i);
            int[] temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }
    }

    // Checks if the given cell is within the valid bounds of the world
    public boolean isValidCell(int x, int y) {
        return x > 0 && x < world.getSizeX() - 1 && y > 0 && y < world.getSizeY() - 1;
    }

    // Counts the number of free spaces around a given cell
    public int amountOfFreeSpacesAround(int x, int y) {
        int amount = 0;
        int[][] directions = {
                { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < world.getSizeX() && newY >= 0 && newY < world.getSizeY()) {
                if (!world.isTree(newX, newY)) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public void searchMaze() {
        // TODO Maze search logic + leaf placement logic
    }

    // Starts the "Collect All Leaf" project
    public void Project_collectAllLeaf() {
        kara.setPosition(1, 1);
        int leafAmount = getLeafAmountFromUser();
        if (leafAmount > 0) {
            setupWorldWithBorders();
            placeLeavesRandomly(leafAmount);
            collectLeaves(leafAmount);
        }
    }

    // Gets the number of leaves to collect from the user
    private int getLeafAmountFromUser() {
        boolean done = false;
        int leafAmount = 1; // Default value
        while (!done) {
            try {
                String input = tools.stringInput("How many leaves do you want to collect?\nDefault: 1").trim();
                if (input.isEmpty()) {
                    leafAmount = 1;
                    done = true;
                } else {
                    leafAmount = Integer.parseInt(input);
                    if (isValidLeafAmount(leafAmount)) {
                        done = true;
                    } else {
                        throw new Exception("Invalid amount or input");
                    }
                }
            } catch (Exception e) {
                handleLeafAmountInputError(e);
            }
        }
        return leafAmount;
    }

    // Validates the leaf amount input by the user
    private boolean isValidLeafAmount(int leafAmount) {
        return leafAmount > 0 && leafAmount <= (world.getSizeX() * (world.getSizeY() - 2));
    }

    // Handles errors during leaf amount input parsing
    private void handleLeafAmountInputError(Exception e) {
        if (e.getMessage().contains("Invalid amount or input")) {
            tools.showMessage("Invalid amount given, please input a valid amount");
        } else if (e instanceof NumberFormatException) {
            tools.showMessage("Using default amount: 1");
        } else if (e instanceof NullPointerException) {
            tools.showMessage("No amount given -> Exiting...");
        } else {
            tools.showMessage("Unexpected error: " + e.getMessage());
        }
    }

    // Sets up the world borders with trees
    private void setupWorldWithBorders() {
        for (int i = 0; i < world.getSizeX(); i++) {
            world.setTree(i, 0, true);
            world.setTree(i, world.getSizeY() - 1, true);
        }
        for (int j = 0; j < world.getSizeY(); j++) {
            world.setTree(0, j, true);
            world.setTree(world.getSizeX() - 1, j, true);
        }
    }

    // Places leaves randomly in the world
    private void placeLeavesRandomly(int leafAmount) {
        int placedLeaf = 0;
        while (placedLeaf < leafAmount) {
            int x = tools.random(world.getSizeX() - 1);
            int y = tools.random(world.getSizeY() - 1);
            if (world.isEmpty(x, y)) {
                world.setLeaf(x, y, true);
                placedLeaf++;
            }
        }
    }

    // Collects leaves based on the specified amount
    private void collectLeaves(int leafAmount) {
        int collectedLeaves = 0;
        while (collectedLeaves < leafAmount) {
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeaves++;
                tools.showMessage("Collected a leaf! Total collected: " + collectedLeaves);
            }
            if (collectedLeaves >= leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            moveKaraToNextLeaf();
        }
    }

    // Moves Kara to the next leaf location
    private void moveKaraToNextLeaf() {
        // Example movement logic; customize as needed
        if (!kara.treeFront()) {
            kara.move();
        } else {
            kara.turnRight();
            if (!kara.treeFront()) {
                kara.move();
            } else {
                kara.turnLeft();
            }
        }
    }

    // Starts the "Collect Leaf If Tree is Left and Right" project
    public void Project_collectLeafIfTreeLeftRight() {
        // TODO: Implement the "Collect Leaf If Tree is Left and Right" project
        tools.showMessage("Project_collectLeafIfTreeLeftRight is not yet implemented.");
    }
}
