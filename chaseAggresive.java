


import java.util.*;

// TODO check why bfs doesnt finish.

public class chaseAggresive implements chaseBehaviour {

    int offset=7;

    public static int pacX, pacY;
    private IGraph graph;

    public static void setCooradinate(int x, int y) {
        // this function will return the pacman coordinates.
        pacX = x;
        pacY = y;

    }

    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {
        this.graph = graph;
        // checking the destination (in this case it's the pacman location)
        Point end = destination();
        Bfs b = new Bfs(start, end, graph);
        return b.startBfs();
    }

    @Override
    public Point destination() {
        // getting pacman coordinates on the screen and returns the proper point on the matrix.
        int locX = (pacX - offset) / PacManBoard.BLOCK_SIZE;
        int locY = (pacY - offset) / PacManBoard.BLOCK_SIZE;
        Point pacPos = (Point) graph.getGraph()[locY][locX];
        return pacPos;

    }
}
