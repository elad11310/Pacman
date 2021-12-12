import java.util.ArrayList;
import java.util.HashMap;

public class Ghost implements Runnable {
    private chaseBehaviour chaseBehaviour;
    public int ghostX, ghostY;
    public ArrayList<Point> path;
    public IGraph graph;
    Point[][] points;
    private int delay;
    private int locX, locY;
    private HashMap<String, chaseBehaviour> states;
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

    public Ghost(chaseBehaviour chaseBehaviour, int x, int y, IGraph graph, int delay , String behaviour) {
        setY(y);
        setX(x);
        this.chaseBehaviour = chaseBehaviour;
        this.graph = graph;
        path = new ArrayList<>();
        this.delay = delay;
        states = new HashMap<>();
        states.put(behaviour,chaseBehaviour);

        // try to change it in the interface.
        points = (Point[][]) graph.getGraph();



    }

    public boolean inRadious(Point ghost, Point pac, int offset) {
        int i, j;


        for (i = ghost.getX() - offset; i <= ghost.getX() + offset; i++) {
            for (j = ghost.getY() - offset; j <= ghost.getY() + offset; j++) {
                if(i == pac.getX() && j == pac.getY()){
                    return true;


                }

            }


        }


        return false;

    }


    @Override
    public void run() {


        try {

            while (PacManBoard.getInGame() ) {

                // getting ghost position
               // if(PacManBoard.ghostInAction) {
                    Point ghostPos = getPoint(ghostX, ghostY);
                    Point pacPos = getPoint(pacX, pacY);

                    if (states.containsKey("static") && inRadious(ghostPos, pacPos, 4)) {
                        if (states.containsKey("chase")) {
                            this.chaseBehaviour = states.get("chase");
                        } else {
                            states.put("chase", new chaseAggresive());
                            this.chaseBehaviour = states.get("chase");
                        }
                    }
                    if (!inRadious(ghostPos, pacPos, 4) && states.containsKey("static")) {
                        this.chaseBehaviour = states.get("static");
                    }


                    path = this.chaseBehaviour.chase(ghostPos, graph);
                    move();
               // }
                // Thread.sleep(400);

            }
        } catch (
                Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

    }


    public void move() {

        for (Point p : path) {
            ghostX = p.getY() * PacManBoard.BLOCK_SIZE + 7;
                ghostY = p.getX() * PacManBoard.BLOCK_SIZE + 7;
                try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        path.clear();


    }

    public static void setCooradinate(int x, int y) {
        pacX = x;
        pacY = y;

    }

    private Point getPoint(int x, int y) {
        locX = (x - 7) / PacManBoard.BLOCK_SIZE;
        locY = (y - 7) / PacManBoard.BLOCK_SIZE;

        return points[locY][locX];
    }
}
