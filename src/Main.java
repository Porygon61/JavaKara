package src;

import javakara.JavaKaraProgram;
import java.util.*;

public class Main extends JavaKaraProgram {

    private Map<String, WorldSettings> worldSettingsMap;

    public void myProgram() {
        loadWorldSettings();
        runProgram();
    }

    public void runProgram() {
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
        tools.checkState();

        String project = "None";
        String chosenProject = chooseProject(project);
        project = chosenProject;

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

    public String chooseProject(String currentProject) {
        String chosenProject = currentProject;

        boolean done = false;
        while (!done) {
            try {
                String[] ProjectsArray = {
                        "Maze", "Collect All Leaf", "Collect Leaf If Tree is Left and Right"
                };
                String projectsList = String.join("\n", ProjectsArray);

                chosenProject = tools.stringInput(
                        "Current Project: " + currentProject + "\n" +
                                "Projects:\n" + projectsList + "\n" +
                                "Enter project name to select, or leave empty to keep current.")
                        .trim();

                if (chosenProject.isEmpty()) {
                    chosenProject = currentProject; // Keep current if empty input
                } else if (!Arrays.asList(ProjectsArray).contains(chosenProject)) {
                    throw new Exception("Invalid project selection");
                }

                done = true;

            } catch (Exception e) {
                if (e.getMessage().contains("Invalid project selection")) {
                    tools.showMessage("Please select a valid project from the list.");
                } else if (e instanceof NullPointerException) {
                    EditProjects();
                    done = true;
                } else {
                    tools.showMessage("Unexpected error: " + e.getMessage());
                }
            }
        }
        return chosenProject;
    }

    private void loadWorldSettings() {
        worldSettingsMap = new HashMap<>();

        // Maze world settings
        WorldSettings mazeSettings = new WorldSettings();
        mazeSettings.setKaraPosition(0, 0);
        mazeSettings.addTree(1, 1);
        mazeSettings.addTree(1, 2);
        // Add more trees, leaves, mushrooms as needed
        worldSettingsMap.put("maze", mazeSettings);

        // Collect All Leaf world settings
        WorldSettings collectAllLeafSettings = new WorldSettings();
        collectAllLeafSettings.setKaraPosition(0, 0);
        collectAllLeafSettings.addLeaf(2, 2);
        // Add more trees, leaves, mushrooms as needed
        worldSettingsMap.put("collect all leaf", collectAllLeafSettings);

        // Collect Leaf If Tree is Left and Right world settings
        WorldSettings collectLeafIfTreeLeftRightSettings = new WorldSettings();
        collectLeafIfTreeLeftRightSettings.setKaraPosition(0, 0);
        collectLeafIfTreeLeftRightSettings.addTree(1, 0);
        collectLeafIfTreeLeftRightSettings.addTree(1, 2);
        // Add more trees, leaves, mushrooms as needed
        worldSettingsMap.put("collect leaf if tree is left and right", collectLeafIfTreeLeftRightSettings);
    }

    private void setupWorld(WorldSettings worldSettings) {
        // Set Kara's position
        kara.setPosition(worldSettings.getKaraX(), worldSettings.getKaraY());

        // Set trees
        for (int[] treePos : worldSettings.getTrees()) {
            world.setTree(treePos[0], treePos[1], true);
        }

        // Set leaves
        for (int[] leafPos : worldSettings.getLeaves()) {
            world.setLeaf(leafPos[0], leafPos[1], true);
        }

        // Set mushrooms
        for (int[] mushroomPos : worldSettings.getMushrooms()) {
            world.setMushroom(mushroomPos[0], mushroomPos[1], true);
        }
    }

    public void Project_maze() {
        WorldSettings mazeWorld = worldSettingsMap.get("maze");
        if (mazeWorld != null) {
            setupWorld(mazeWorld);
            // Implement additional logic for the maze project here
        } else {
            tools.showMessage("Maze project world settings not found!");
        }
    }

    public void Project_collectAllLeaf() {
        WorldSettings collectAllLeafWorld = worldSettingsMap.get("collect all leaf");
        if (collectAllLeafWorld != null) {
            setupWorld(collectAllLeafWorld);
            // Implement additional logic for collecting all leaves here
        } else {
            tools.showMessage("Collect All Leaf project world settings not found!");
        }
    }

    public void Project_collectLeafIfTreeLeftRight() {
        WorldSettings collectLeafIfTreeLeftRightWorld = worldSettingsMap.get("collect leaf if tree is left and right");
        if (collectLeafIfTreeLeftRightWorld != null) {
            setupWorld(collectLeafIfTreeLeftRightWorld);
            // Implement additional logic for the specific leaf collection here
        } else {
            tools.showMessage("Collect Leaf If Tree is Left and Right project world settings not found!");
        }
    }

    public void EditProjects() {
        // Add logic for editing projects if needed
    }
}

// Custom class to hold world settings
class WorldSettings {
    private int karaX;
    private int karaY;
    private List<int[]> trees;
    private List<int[]> leaves;
    private List<int[]> mushrooms;

    public WorldSettings() {
        trees = new ArrayList<>();
        leaves = new ArrayList<>();
        mushrooms = new ArrayList<>();
    }

    public void setKaraPosition(int x, int y) {
        this.karaX = x;
        this.karaY = y;
    }

    public int getKaraX() {
        return karaX;
    }

    public int getKaraY() {
        return karaY;
    }

    public void addTree(int x, int y) {
        trees.add(new int[] { x, y });
    }

    public void addLeaf(int x, int y) {
        leaves.add(new int[] { x, y });
    }

    public void addMushroom(int x, int y) {
        mushrooms.add(new int[] { x, y });
    }

    public List<int[]> getTrees() {
        return trees;
    }

    public List<int[]> getLeaves() {
        return leaves;
    }

    public List<int[]> getMushrooms() {
        return mushrooms;
    }
}
