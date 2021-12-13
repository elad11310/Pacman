import java.util.ArrayList;

public class ChaseStatic implements IChaseBehaviour {


    private Point saveStart;
    private boolean isFirst = true;


    @Override
    public ArrayList<Point> chase(Point start, IGraph graph) {

        // saving the first start point , to return to this point when pacman gets out of the  radius.
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
