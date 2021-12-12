import java.util.HashMap;

public class PacManGraph implements  IGraph<String, Point> {


    Point[][] matrix;

    public PacManGraph(Point[][] matrix) {
        this.matrix = matrix;

    }


    @Override
    public HashMap<String, Point> neighbors(Point node) {
        HashMap<String, Point> map = new HashMap<>();

        // check neighbors

        // check up
        if (node.getX() - 1 >= 0 && matrix[node.getX() - 1][node.getY()].isOk) {
            map.put("UP", matrix[node.getX() - 1][node.getY()]);
        }
        //check down
        if (node.getX() + 1 < matrix[0].length && matrix[node.getX() + 1][node.getY()].isOk) {
            map.put("DOWN", matrix[node.getX() + 1][node.getY()]);
        }

        // check left

        if (node.getY() - 1 >= 0 && matrix[node.getX()][node.getY() - 1].isOk) {
            map.put("LEFT", matrix[node.getX()][node.getY() - 1]);
        }

        // check right
        if (node.getY() + 1 < matrix.length && matrix[node.getX()][node.getY() + 1].isOk) {
            map.put("RIGHT", matrix[node.getX()][node.getY() + 1]);
        }

        return map;
    }

    @Override
    public Point[][] getGraph() {
        return matrix;
    }
}