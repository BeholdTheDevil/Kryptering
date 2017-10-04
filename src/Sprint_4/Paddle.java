package Sprint_4;

/**
 * Created by anton on 2017-09-27.
 */
public class Paddle {

    private int xsize = 25;
    private int ysize = 100;
    private int x, y, maxspeed, velocity;

    Paddle(int x_, int y_, int maxspeed_) {
        x = x_;
        y = y_;
        maxspeed = maxspeed_;
    }

    public boolean checkCollision(Circle c) {
        /*a = (x + xsize/2, y)
         *b = (x + xsize/2, y + ysize)
         *p = (c.getX() + c.getSize()/2, c.getY() + c.getSize()/2)
         */

        //Scalarprojection

        Vector2D a = new Vector2D(x + xsize/2, y);
        Vector2D b = new Vector2D(x + xsize/2, y + ysize);
        Vector2D p = new Vector2D(c.getX() + c.getSize()/2, c.getY() + c.getSize()/2);
        Vector2D ap = p.sub(a);
        Vector2D ab = b.sub(a);
        ab.normalize();
        ab.mult(ap.dot(ab));
        Vector2D normalpoint = a.add(ab);

        if(normalpoint.dist(a) > a.dist(b) || normalpoint.dist(b) > a.dist(b)) return false;

        return normalpoint.dist(p) < c.getSize()/2 + xsize/2;

        //return c.getY() + c.getSize()/2 > y && c.getY() + c.getSize()/2 < y + ysize + c.getSize() && Math.abs((x + xsize/2) - (c.getX() + c.getSize()/2)) < xsize/2 + c.getSize()/2;
    }

    public void bounce(Circle c) {
        System.out.println("Collision");
        Vector2D pos = new Vector2D(x, y + ysize/2);
        Vector2D cpos = new Vector2D(c.getX() + c.getSize()/2, c.getY() + c.getSize()/2);

        Vector2D diff = pos.sub(cpos);
        diff.normalize();
        System.out.println(diff.getX() + " , " + diff.getY());
        double speed = Math.sqrt(c.getXspeed()*c.getXspeed() + c.getYspeed()*c.getYspeed());
        c.setXspeed((c.getXspeed() / speed + diff.getX()) * speed * -1);
        c.setYspeed((c.getYspeed() / speed + diff.getY()) * speed * -1);
    }

    public void move(int v) {
        velocity = v > maxspeed ? maxspeed : v;
    }

    public void setY(int y_) {y = y_;}

    public int getX() {return x;}
    public int getY() {return y;}

    public int getXsize() {return xsize;}
    public int getYsize() {return ysize;}

    public int getVelocity() {return velocity;}
}
