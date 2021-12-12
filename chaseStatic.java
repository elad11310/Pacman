import java.util.ArrayList;

public class chaseStatic implements chaseBehaviour {


    private Point saveStart;
    private boolean isFirst = true;


    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {
        if (isFirst) {
            saveStart = start;
            isFirst = false;
        }

        Bfs b = new Bfs(start, saveStart, graph);
        return b.startBfs();
    }

    @Override
    public Point destination() {
        return null;
    }
}
