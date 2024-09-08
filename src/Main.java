package src;

import javakara.JavaKaraProgram;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Main extends JavaKaraProgram {

    public void myProgram() {
        setWorldSize();
        accessWorlds();
    }

    public void setWorldSize() {
        int WorldX = world.getSizeX();
        int WorldY = world.getSizeY();

        boolean done = false;
        while (!done) {
            try {
                String input = tools.stringInput(
                        "Current World Size: " + WorldX + "x" + WorldY + "\n" +
                                "Max: 99x99, Min: 1x1" + "\n" +
                                "Press OK to leave as is, Abbrechen to reset(9x9)\nOr enter new size (e.g., 9x9)")
                        .trim();

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

    public void accessWorlds() {
        world.clearAll();

        String chosenProject = chooseProject();

        switch (chosenProject) {
            case "Maze":
                Project_maze();
                break;
            case "1":
                Project_maze();
                break;
            case "Collect All Leaf":
                Project_collectAllLeaf();
                break;
            case "2":
                Project_collectAllLeaf();
                break;
            case "Collect Leaf If Tree is Left and Right":
                Project_collectLeafIfTreeLeftRight();
                break;
            case "3":
                Project_collectLeafIfTreeLeftRight();
                break;
            default:
                // tools.showMessage("No project with such a name has been registered!");
                break;
        }
    }

    public String chooseProject() {
        String[] ProjectsArray = {
                "1",
                "Maze",
                "2",
                "Collect All Leaf",
                "3",
                "Collect Leaf If Tree is Left and Right"
        };
        String projectsList = String.join("\n", ProjectsArray);

        boolean done = false;
        String chosenProject = null;
        while (!done) {
            try {
                chosenProject = tools.stringInput(
                        "Projects:\n\n" + projectsList + "\n" + "\n" +
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

    public void Project_maze() {
        for (int x = 0; x < world.getSizeX(); x++) {
            for (int y = 0; y < world.getSizeY(); y++) {
                world.setTree(x, y, true);
            }
        }
        boolean loop = true;
        int startX = 1;
        int startY = 1;
        while (loop) {
            startX = tools.random(world.getSizeX() - 1);
            startY = tools.random(world.getSizeY() - 1);
            if ((startX == 1 || startX == world.getSizeX() - 2) && (startY == 1 || startY == world.getSizeY() - 2)) {
                loop = false;
            }
        }

        generateMaze(startX, startY);

        kara.setPosition(checkAllCellsForX(), checkAllCellsForY());
        searchMaze();
    }

    public void generateMaze(int x, int y) {
        world.setTree(x, y, false);

        // the possible directions(left, top, right, bottom)
        int[][] directions = {
                { -2, 0 }, // left
                { 2, 0 }, // right
                { 0, -2 }, // top
                { 0, 2 } // bottom
        };

        randomizeDirections(directions);

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidCell(newX, newY)) {
                int midX = (x + newX) / 2;
                int midY = (y + newY) / 2;

                if (world.isTree(midX, midY) && world.isTree(newX, newY) && amountOfFreeSpacesAround(newX, newY) == 0) {
                    world.setTree(midX, midY, false);

                    int rnd = tools.random(10);
                    if (rnd < 5) {
                        world.setLeaf(midX, midY, true);
                    }
                    generateMaze(newX, newY);
                }
            }
        }
    }

    public void randomizeDirections(int[][] directions) {
        // Shuffle the array of directions using the Fisher-Yates algorithm
        for (int i = directions.length - 1; i > 0; i--) {
            int j = tools.random(i); // Get a random index between 0 and i, inclusive
            // Swap directions[i] and directions[j]
            int[] temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }
    }

    public boolean isValidCell(int x, int y) {
        return x > 0 && x < world.getSizeX() - 1 && y > 0 && y < world.getSizeY() - 1;
    }

    public int amountOfFreeSpacesAround(int x, int y) {
        int amount = 0;

        int[][] directions = {
                { -1, 0 }, // left
                { 1, 0 }, // right
                { 0, -1 }, // top
                { 0, 1 } // bottom
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

    public int checkLeafAmount() {
        int leafAmount = 0;
        for (int y = 0; y < world.getSizeY(); y++) {
            for (int x = 0; x < world.getSizeX(); x++) {
                if (world.isLeaf(x, y)) {
                    leafAmount++;
                }
            }
        }
        return leafAmount;
    }

    public void searchMaze() {
        int leafAmount = checkLeafAmount();
        int collectedLeafs = 0;
        while (collectedLeafs < leafAmount) {
            while (!kara.onLeaf()) {

                if (kara.treeRight() && !kara.treeFront()) {
                    kara.move();
                } else if (!kara.treeRight()) {
                    kara.turnRight();
                    if (!kara.treeFront()) {
                        kara.move();
                    }
                } else if (kara.treeFront()) {
                    kara.turnLeft();
                }
            }
            kara.removeLeaf();
            collectedLeafs++;
        }
        tools.showMessage("Collected All (" + collectedLeafs + ") leafs");
    }

    public java.awt.Point[] checkAllCellsForEmptyCells() {
        List<java.awt.Point> emptyCellsList = new ArrayList<>();
        for (int y = 0; y < world.getSizeY(); y++) {
            for (int x = 0; x < world.getSizeX(); x++) {
                if (world.isEmpty(x, y)) {
                    java.awt.Point point = new java.awt.Point(x, y);
                    emptyCellsList.add(point);
                }
            }
        }
        java.awt.Point[] emptyCells = new java.awt.Point[emptyCellsList.size()];
        emptyCellsList.toArray(emptyCells);
        return emptyCells;
    }

    public int checkAllCellsForX() {
        java.awt.Point[] emptyCells = checkAllCellsForEmptyCells();
        int x = emptyCells[tools.random(emptyCells.length)].x;
        return x;
    }

    public int checkAllCellsForY() {
        java.awt.Point[] emptyCells = checkAllCellsForEmptyCells();
        int y = emptyCells[tools.random(emptyCells.length)].y;
        return y;
    }

    public void Project_collectAllLeaf() {

        kara.setPosition(0, 1);

        boolean done = false;
        int leafAmount = 0;
        while (!done) {
            try {
                String input = tools.stringInput("How many leaves do you want to collect?\nDefault: 1").trim();

                leafAmount = Integer.parseInt(input);

                if (leafAmount <= 0 || (leafAmount > (world.getSizeX() * (world.getSizeY() - 2)))) {
                    throw new Exception("Invalid amount or input");
                }

                done = true;
            } catch (Exception e) {
                tools.showMessage(e.toString()); // debug
                if (e.getMessage().contains("Invalid amount or input")) {
                    tools.showMessage("Invalid amount given, please input a valid amount");
                } else if (e instanceof NumberFormatException) {
                    leafAmount = 1;
                    tools.showMessage("Using default amount: 1");
                    done = true;
                } else if (e instanceof NullPointerException) {
                    tools.showMessage("No amount given -> Exiting...");
                    done = true;
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }

        // world generation
        for (int i = 0; i < world.getSizeX(); i++) {
            world.setTree(i, 0, true);
            world.setTree(i, world.getSizeY() - 1, true);
        }

        int placedLeaf = 0;
        do {
            int x = tools.random(world.getSizeX() - 1);
            int y = tools.random(world.getSizeY() - 1);
            if (world.isEmpty(x, y)) {
                world.setLeaf(x, y, true);
                placedLeaf++;
            }
        } while (placedLeaf < leafAmount);

        // execution
        int collectedLeafs = 0;
        while (collectedLeafs < leafAmount) {
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            // given kara look rightwards
            kara.turnRight(); // turn downwards
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            while (!kara.treeFront()) {
                if (kara.onLeaf()) {
                    kara.removeLeaf();
                    collectedLeafs++;
                }
                if (collectedLeafs == leafAmount) {
                    tools.showMessage("Finished Collection");
                    break;
                }
                kara.move(); // moves until tree
            }
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            kara.turnLeft(); // turn rightwards
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            kara.move(); // moves 1 step
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            kara.turnLeft(); // turn upwards
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            while (!kara.treeFront()) {
                if (kara.onLeaf()) {
                    kara.removeLeaf();
                    collectedLeafs++;
                }
                if (collectedLeafs == leafAmount) {
                    tools.showMessage("Finished Collection");
                    break;
                }
                kara.move(); // moves until tree
            }
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            kara.turnRight(); // turn right
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
            kara.move(); // moves 1 step
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            if (collectedLeafs == leafAmount) {
                tools.showMessage("Finished Collection");
                break;
            }
        }
    }

    public void Project_collectLeafIfTreeLeftRight() {
        int StartY = Math.round(world.getSizeY() / 2);
        kara.setPosition(0, StartY);
        // World Generation
        int topY = StartY - 1;
        int bottomY = StartY + 1;

        for (int i = 0; i < world.getSizeX(); i++) {
            int rnd = tools.random(10);
            if (rnd < 8) {
                world.setTree(i, topY, true);
            }
        }

        for (int j = 0; j < world.getSizeX(); j++) {
            int rnd = tools.random(10);
            if (rnd < 8) {
                world.setTree(j, bottomY, true);
            }
        }
        int leafAmount = 0;
        for (int l = 0; l < world.getSizeX(); l++) {
            int rnd = tools.random(10);
            if (rnd < 6) {
                world.setLeaf(l, StartY, true);
                leafAmount++;
            }
        }
        // Execution
        int collectedLeafs = 0;
        do {
            if (kara.onLeaf() && kara.treeRight() && kara.treeLeft()) {
                kara.removeLeaf();
                collectedLeafs++;
            } else {
                kara.move();
            }
        } while (collectedLeafs < leafAmount);
    }
}
