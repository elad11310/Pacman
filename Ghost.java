import java.util.ArrayList;
import java.util.HashMap;

public class Ghost implements Runnable {
    private IChaseBehaviour chaseBehaviour;
    public int ghostX, ghostY;
    public ArrayList<Point> path;
    public IGraph graph;
    Point[][] points;
    private int delay;
    private int locX, locY;
    private int offset = 7;
    private HashMap<String, IChaseBehaviour> states;
    public static int pacX, pacY;


    public void setX(int x) {
        this.ghostX = x;
    }

    public void setY(int y) {
        this.ghostY = y;
    }

    public int getX() {
        return ghostX;
    }

    public int getY() {
        return ghostY;
    }

    public Ghost(IChaseBehaviour chaseBehaviour, int x, int y, IGraph graph, int delay, String behaviour) {
        setY(y);
        setX(x);
        this.chaseBehaviour = chaseBehaviour;
        this.graph = graph;
        // this path is for the points path to reach the destination.
        path = new ArrayList<>();
        this.delay = delay;
        states = new HashMap<>();
        states.put(behaviour, chaseBehaviour);

        // getting the points matrix.
        points = (Point[][]) graph.getGraph();


    }

    public boolean inRadius(Point ghost, Point pac, int offset) {

        // this function gets two points on the matrix and checks if one is in the second's radius
        int i, j;


        for (i = ghost.getX() - offset; i <= ghost.getX() + offset; i++) {
            for (j = ghost.getY() - offset; j <= ghost.getY() + offset; j++) {
                if (i == pac.getX() && j == pac.getY()) {
                    return true;


                }

            }


        }


        return false;

    }


    @Override
    public void run() {


        try {

            while (PacManBoard.getInGame()) {

                // getting ghost position

                Point ghostPos = getPoint(ghostX, ghostY);
                // getting pacman position
                Point pacPos = getPoint(pacX, pacY);
                //checking if the current ghost is in static mode and if pacman in it's radius.
                //if so change the behaviour to aggressive and chase.
                if (states.containsKey("static") && inRadius(ghostPos, pacPos, 4)) {
                    if (states.containsKey("chase")) {
                        this.chaseBehaviour = states.get("chase");
                    } else {
                        states.put("chase", new ChaseAggressive());
                        this.chaseBehaviour = states.get("chase");
                    }
                }
                // if it's not in radius and the current ghost has static mode, so return it to static mode.
                if (!inRadius(ghostPos, pacPos, 4) && states.containsKey("static")) {
                    this.chaseBehaviour = states.get("static");
                }

                // getting the path for the current behaviour
                path = this.chaseBehaviour.chase(ghostPos, graph);
                move();


            }
        } catch (
                Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

    }


    public void move() {

        // getting the current path list and move.

        for (Point p : path) {
            ghostX = p.getY() * PacManBoard.BLOCK_SIZE + offset;
            ghostY = p.getX() * PacManBoard.BLOCK_SIZE + offset;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        path.clear();


    }

    public static void setCooradinate(int x, int y) {

        // update the pac x and y to know where it is at any time.
        pacX = x;
        pacY = y;

    }

    public static int getPacCoordinateX() {
        return pacX;
    }

    public static int getPacCoordinateY() {
        return pacY;
    }

    private Point getPoint(int x, int y) {
        // this function gets x and y on the screen and return the proper cell in the point matrix.
        locX = (x - offset) / PacManBoard.BLOCK_SIZE;
        locY = (y - offset) / PacManBoard.BLOCK_SIZE;

        return points[locY][locX];
    }
}
