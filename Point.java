public class Point {


    // normal class point contains x and y and a boolean if the current point is walkable.

    private int x;
    private int y;
    boolean isOk;


    public Point(int x, int y, boolean isOk) {
        this.x = x;
        this.y = y;
        this.isOk = isOk;
        //neighbours = new HashMap<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public boolean getIsOk() {
        return isOk;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setIsOk(boolean isOk) {
        this.isOk = isOk;
    }


    public String toString() {
        return "PointX = " + this.x + " PointY = " + this.y + " isOk " + this.isOk;

    }


}
