import java.util.HashMap;

public interface IGraph<T,V> {

     // this interface includes two functions
     // neighbours returns the current point neighbour
     // getGraph return the current graph.
     HashMap <T,V> neighbors(V node);

     V[][] getGraph();

}
