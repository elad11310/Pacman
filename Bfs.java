import java.util.*;

public class Bfs {

    private Point start;
    private Point end;
    private IGraph graph;

    Queue<Point> q;
    HashSet<Point> blackend;
    HashMap<Point, Point> previous;
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

            // reached the desintation
            if (current == end) {
                path.add(end);

                while (previous.containsKey(current)) {
                    current = previous.get(current);
                    path.add(current);
                }

                Collections.reverse(path);
                break;
            } else {
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
