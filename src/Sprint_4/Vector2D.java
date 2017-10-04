package Sprint_4;

/**
 * Created by anton on 2017-09-28.
 */
public class Vector2D {

    private double x, y;

    public Vector2D() {
        x = y = 0;
    }

    public Vector2D(double x_ , double y_) {
        x = x_;
        y = y_;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D sub(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    public void mult(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public void normalize() {
        double length = this.length();
        if(length != 0) {
            x /= length;
            y /= length;
        }
    }

    public double dot(Vector2D v) {
        return x*v.x + y*v.y;
    }

    public double dist(Vector2D v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public double getX() {return x;}

    public double getY() {return y;}
}
