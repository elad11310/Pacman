import java.util.HashMap;

public interface IGraph<T,V> {

     HashMap <T,V> neighbors(V node);

     V[][] getGraph();

}
