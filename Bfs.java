import java.util.*;

public class Bfs {

    private Point start;
    private Point end;
    private IGraph graph;

    // queue for holding the points we need to visit
    Queue<Point> q;
    // blacked set to save the points we visited and to know we can't visit again
    HashSet<Point> blackend;
    // previous map to save the current's point father.
    HashMap<Point, Point> previous;
    // path to destination to return
    ArrayList<Point> path;


    public Bfs(Point start , Point end , IGraph graph){
        this.start=start;
        this.end = end;
        this.graph = graph;

        q = new LinkedList<>();
        blackend = new HashSet<>();
        previous = new HashMap<>();
        path = new ArrayList<>();


    }

    public ArrayList<Point> startBfs() {

        q.add(start);
        blackend.add(start);
        this.start = start;
        while (!q.isEmpty()) {

            Point current = q.poll();

            // reached the destination , start creating the path from back to start and reverse it.
            if (current == end) {
                path.add(end);

                while (previous.containsKey(current)) {
                    current = previous.get(current);
                    path.add(current);
                }

                Collections.reverse(path);
                break;
            } else {
                // getting the current point's neighbours.
                HashMap<String, Point> neighbors = graph.neighbors(current);
                for (Point p : neighbors.values()) {
                    if (blackend.contains(p)) {
                        continue;
                    }
                    q.add(p);
                    blackend.add(p);
                    previous.put(p, current);
                }

            }


        }


        return path;



    }
}
