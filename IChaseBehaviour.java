import java.util.ArrayList;

public interface IChaseBehaviour {


    // this interface is used to describe the behaviour of each ghost
    // each ghost implements the chase function which determines the manner of chase to this ghost
    // in addition destination function is implemented to return the destination point.
    ArrayList<Point> chase(Point start, IGraph graph);

    Point destination();


}
