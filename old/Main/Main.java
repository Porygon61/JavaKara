package src;

import javakara.JavaKaraProgram;
import other_Classes.WorldsDB;
import java.util.*;

public class Main extends JavaKaraProgram {

    public void myProgram() {
        main();
    }

    public void main() {
        setWorldVariables();
    }

    public void setWorldVariables() {
        setWorldSize();
        chooseWorld();

    }

    public void setWorldSize() {
        int WorldX = world.getSizeX();
        int WorldY = world.getSizeY();

        boolean done = false;
        while (!done) {
            try {
                String[] WorldSize = new String[2];
                WorldSize = tools
                        .stringInput(
                                "Current World Size: " + WorldX + "x" + WorldY + "\n" + "Max: 99x99, Min: 1x1" + "\n"
                                        + "Press OK to leave as is |or| Press Abbrechen to reset")
                        .trim()
                        .split("x");
                if (Integer.parseInt(WorldSize[0]) > 99 || Integer.parseInt(WorldSize[1]) > 99) {
                    throw new Exception();
                }

                done = true;
                try {
                    world.setSize(Integer.parseInt(WorldSize[0]), Integer.parseInt(WorldSize[1]));
                } catch (Exception e) {
                    tools.showMessage("Error while setting World Size");
                }
            } catch (Exception e) {
                if (e.toString().contains("java.lang.NumberFormatException")) {
                    done = true;
                }
                if (e.toString().contains("java.lang.NullPointerException")) {
                    world.setSize(9, 9);
                    done = true;
                } else {
                    tools.showMessage("Please Input a Valid World Size\nMax: 99x99, Min: 1x1\ne.g. '5x5'");
                    tools.showMessage(e.toString());
                }

            }
        }
    }


    public void chooseWorld() {
    
        WorldsDB World = new WorldsDB();
           
        int project = 0;
        int[] position = new int[2];

        world.clearAll();

        World.edit();

        int x; // only here to cleanly compile
        int y; // only here to cleanly compile

        switch (project) {
            case 1:
                World.maze(x, y);
                break;
            case 2:
                World.collectAllLeaf(x, y);
                break;
            default:
                tools.showMessage("No Projct with such a Name has been registered!");
        }
    }
}
