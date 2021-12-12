import java.util.ArrayList;

public class chasePatrol implements chaseBehaviour {


    private IGraph graph;
    private boolean isFirst = false;
    private Point end;
    private Point saveStart;


    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {
        this.graph = graph;
        if (!isFirst) {
            end = destination();
            isFirst = true;
            saveStart = start;
        }

        if (start == end) {
            end = saveStart;
            saveStart = start;
        }

        Bfs b = new Bfs(start, end, graph);
        return b.startBfs();
    }

    @Override
    public Point destination() {
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
