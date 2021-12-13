import java.util.ArrayList;

public class ChasePatrol implements IChaseBehaviour {


    private IGraph graph;
    private boolean isFirst = false;
    private Point end;
    private Point saveStart;


    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {
        this.graph = graph;
        // check if it's the first time , if so generate random point to start patrolling
        if (!isFirst) {
            end = destination();
            isFirst = true;
            // saving the start point
            saveStart = start;
        }

        // if we reached the destination , we want to come back to the start point.
        if (start == end) {
            end = saveStart;
            saveStart = start;
        }

        Bfs b = new Bfs(start, end, graph);
        return b.startBfs();
    }

    @Override
    public Point destination() {
        // this function generates random point and return it.
        Point[][] points = (Point[][]) graph.getGraph();
        int startX, endX;
        do {
            endX = (int) (Math.random() * PacManBoard.N_BLOCKS);
            startX = (int) (Math.random() * PacManBoard.N_BLOCKS);
        } while (!points[startX][endX].getIsOk());


        Point end = (Point) graph.getGraph()[startX][endX];
        return end;
    }

}
