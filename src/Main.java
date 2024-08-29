package src;

import javakara.JavaKaraProgram;
import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main extends JavaKaraProgram {

    private JSONObject worldSettings;

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
        try {
            String content = new String(Files.readAllBytes(Paths.get("worlds.json")));
            worldSettings = new JSONObject(content);
        } catch (Exception e) {
            tools.showMessage("Error loading world settings: " + e.getMessage());
        }
    }

    private void setupWorld(JSONObject worldData) {
        // Set Kara's position
        JSONObject karaPosition = worldData.getJSONObject("kara");
        kara.setPosition(karaPosition.getInt("x"), karaPosition.getInt("y"));

        // Set trees
        JSONArray trees = worldData.optJSONArray("tree");
        if (trees != null) {
            for (int i = 0; i < trees.length(); i++) {
                JSONObject tree = trees.getJSONObject(i);
                world.setTree(tree.getInt("x"), tree.getInt("y"), true);
            }
        }

        // Set leaves
        JSONArray leaves = worldData.optJSONArray("leaf");
        if (leaves != null) {
            for (int i = 0; i < leaves.length(); i++) {
                JSONObject leaf = leaves.getJSONObject(i);
                world.setLeaf(leaf.getInt("x"), leaf.getInt("y"), true);
            }
        }

        // Set mushrooms
        JSONArray mushrooms = worldData.optJSONArray("mushroom");
        if (mushrooms != null) {
            for (int i = 0; i < mushrooms.length(); i++) {
                JSONObject mushroom = mushrooms.getJSONObject(i);
                world.setMushroom(mushroom.getInt("x"), mushroom.getInt("y"), true);
            }
        }
    }

    public void Project_maze() {
        JSONObject mazeWorld = getWorldByProjectName("maze");
        if (mazeWorld != null) {
            setupWorld(mazeWorld.getJSONObject("positions"));
            // Implement additional logic for the maze project here
        } else {
            tools.showMessage("Maze project world settings not found!");
        }
    }

    public void Project_collectAllLeaf() {
        JSONObject collectAllLeafWorld = getWorldByProjectName("collect all leaf");
        if (collectAllLeafWorld != null) {
            setupWorld(collectAllLeafWorld.getJSONObject("positions"));
            // Implement additional logic for collecting all leaves here
        } else {
            tools.showMessage("Collect All Leaf project world settings not found!");
        }
    }

    public void Project_collectLeafIfTreeLeftRight() {
        JSONObject collectLeafIfTreeLeftRightWorld = getWorldByProjectName("collect leaf if tree is left and right");
        if (collectLeafIfTreeLeftRightWorld != null) {
            setupWorld(collectLeafIfTreeLeftRightWorld.getJSONObject("positions"));
            // Implement additional logic for the specific leaf collection here
        } else {
            tools.showMessage("Collect Leaf If Tree is Left and Right project world settings not found!");
        }
    }

    private JSONObject getWorldByProjectName(String projectName) {
        JSONArray worlds = worldSettings.getJSONArray("worlds");
        for (int i = 0; i < worlds.length(); i++) {
            JSONObject world = worlds.getJSONObject(i);
            if (world.getString("name").equalsIgnoreCase(projectName)) {
                return world;
            }
        }
        return null;
    }

    public void EditProjects() {

    }
}
