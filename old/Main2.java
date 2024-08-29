import javakara.JavaKaraProgram;
import src.Worlds.Worlds;
import src.Worlds.Worlds.*;

import java.util.*;

import Varaible.*;

/* BEFEHLE:  kara.
 *   move()  turnRight()  turnLeft()
 *   putLeaf()  removeLeaf()
 *
 * SENSOREN: kara.
 *   treeFront()  treeLeft()  treeRight()
 *   mushroomFront()  onLeaf()
 */
public class Main2 extends JavaKaraProgram {

    public void myProgram() {
        main();
    }

    public void main() {
        setup();
    }

    public void setup() {
        String[] projects = { "1. maze", "2. collect all leaf" };
        int project;
        int[] position = new int[2];

        world.clearAll();

        do {
            tools.showMessage(Arrays.toString(projects));
            project = tools.intInput("Project Number");
            construct(project);
        } while (position == null);

        kara.setPosition(position[0], position[1]);
        tools.showMessage(kara.getPosition().toString());
    }

    public int[] construct(int project) {
        int x = 0;
        int y = 0;
        Worlds Project = new Worlds();
        switch (project) {
            case 1:
                x = 1; // exmaple number
                y = 1; // ""
                Project.maze(x, y);
                break;
            case 2:
                x = 1; // Example
                y = 1; // ""
                Project.collectAllLeaf(x, y);
                break;
            default:
                tools.showMessage("No Projct with such a Name has been registered!");
                return null;
        }
        return new int[] { x, y };
    }

}
