


import java.util.*;

// TODO check why bfs doesnt finish.

public class chaseAggresive implements chaseBehaviour {

    public static int pacX, pacY;
    private IGraph graph;

    public static void setCooradinate(int x, int y) {
        pacX = x;
        pacY = y;

    }

    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {
        this.graph = graph;
        Point end = destination();
        Bfs b = new Bfs(start, end, graph);
        return b.startBfs();
    }

    @Override
    public Point destination() {
        int locX = (pacX - 7) / PacManBoard.BLOCK_SIZE;
        int locY = (pacY - 7) / PacManBoard.BLOCK_SIZE;
        Point pacPos = (Point) graph.getGraph()[locY][locX];
        return pacPos;

    }
}
