package src;

import javakara.JavaKaraProgram;
import java.util.Arrays;

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
                                "Press OK to leave as is or enter new size (e.g., 10x10)")
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
            case "Collect All Leaf":
                Project_collectAllLeaf();
                break;
            case "Collect Leaf If Tree is Left and Right":
                Project_collectLeafIfTreeLeftRight();
                break;
            default:
                tools.showMessage("No project with such a name has been registered!");
                break;
        }
    }

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
                        "Projects:\n" + projectsList + "\n" +
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
        kara.setPosition(tools.random(getSizeX() - 1), tools.random(getSizeY() - 1));
        // trycatch with while loop that stops wehn catched and places trees infinitely
        // until catched(happend when no possible coordinate for a tree is found)
        try {
            while (true) {
                int x = tools.random(getSizeX() - 1);
                int y = tools.random(getSizeY() - 1);

                if (!world.isTree(x, y)) {
                    if (amountOfTreesAround(x, y) <= 2 && amountOfTreesAround(x, y) >= 0) {
                        world.setTree(x, y, true);
                    }
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static int amountOfTreesAround(int x, int y) {
        int amount = 0;
        if (x <= 0) {
            // top side
            if (world.isTree(x + 1, y)) {
                // downwards
                amount++;
            } else if (world.isTree(x, y + 1)) {
                // rightwards
                amount++;
            } else if (world.isTree(x, y - 1)) {
                // leftwards
                amount++;
            } else if (world.isTree(x - 1, y)) {
                // upwards
                amount++;
            }
            // idea:
            // else if (world.isTree(x+1,y+1)) {
            // //diagonally downwards and rightwards
            // amount++;
            // } //and so on
        } else if (y <= 0) {
            // left side
            if (world.isTree(x + 1, y)) {
                // downwards
                amount++;
            } else if (world.isTree(x, y + 1)) {
                // rightwards
                amount++;
            } else if (world.isTree(x - 1, y)) {
                // upwards
                amount++;
            }
        } else if (x >= (world.getSizeX() - 1)) {
            // bottom side
            if (world.isTree(x, y + 1)) {
                // rightwards
                amount++;
            } else if (world.isTree(x, y - 1)) {
                // leftwards
                amount++;
            } else if (world.isTree(x - 1, y)) {
                // upwards
                amount++;
            }
        } else if (y >= (world.getSizeY() - 1)) {
            // right side
            if (world.isTree(x + 1, y)) {
                // downwards
                amount++;
            } else if (world.isTree(x, y - 1)) {
                // leftwards
                amount++;
            } else if (world.isTree(x - 1, y)) {
                // upwards
                amount++;
            }
        } else {
            // general case
            if (world.isTree(x + 1, y)) {
                // downwards
                amount++;
            } else if (world.isTree(x, y + 1)) {
                // rightwards
                amount++;
            } else if (world.isTree(x, y - 1)) {
                // leftwards
                amount++;
            } else if (world.isTree(x - 1, y)) {
                // upwards
                amount++;
            }
        }
        return amount;
    }

    public void Project_collectAllLeaf() {

        kara.setPosition(0, 1);

        boolean done = false;
        int leafAmount = 0;
        // TODO logic for incorrect inpuzt of ininput function is incomplete
        while (!done) {
            try {
                leafAmount = tools.intInput("How many leaves do you want to collect?");
                if (leafAmount <= 0) {
                    throw new Exception("Invalid amount");
                }
                done = true;
            } catch (Exception e) {
                if (e.getMessage().contains("invalid amount")) {
                    tools.showMessage("Invalid amount given");
                } else if (e instanceof NullPointerException) {
                    tools.showMessage("No amount given -> Exiting...");
                    done = true;
                    leafAmount = 0; // Exit without selecting an amount
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }

        // world generation
        for (int i = 0; i < getSizeX() - 1; i++) {
            world.setTree(i, 0, true);
            world.setTree(i, getSizeY() - 1, true);
        }
        for (int i = 0; i < tools.random(leafAmount); i++) {
            int x = tools.random(getSizeX() - 1);
            int y = tools.random(getSizeY() - 1);
            if (world.isEmpty(x, y)) {
                world.setLeaf(x, y, true);
            }
        }

        // execution
        int collectedLeafs = 0;
        while (collectedLeafs <= leafAmount) {
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            // given kara look rightwards
            kara.turnRight();
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            while (!kara.treeFront()) {
                if (kara.onLeaf()) {
                    kara.removeLeaf();
                    collectedLeafs++;
                }
                kara.move();
            }
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            kara.turnLeft();
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            kara.turnLeft();
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            while (!kara.treeFront()) {
                if (kara.onLeaf()) {
                    kara.removeLeaf();
                    collectedLeafs++;
                }
                kara.move();
            }
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
            kara.turnRight();
            if (kara.onLeaf()) {
                kara.removeLeaf();
                collectedLeafs++;
            }
        }
    }

    public void Project_collectLeafIfTreeLeftRight() {

        kara.setPosition(1, 1);

        world.setTree(2, 2, true);
        world.setTree(2, 4, true);
        world.setLeaf(2, 3, true);
    }
}
