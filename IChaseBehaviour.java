import java.util.ArrayList;

public interface IChaseBehaviour {
    ArrayList<Point> chase(Point start, IGraph graph);

    Point destination();


}
