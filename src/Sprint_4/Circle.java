package Sprint_4;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by anton on 2017-09-26.
 */
public class Circle {
    private BufferedImage img;
    private int x, y, size;
    private double xspeed, yspeed;

    Circle(Color color, int x_, int y_, int size_) {
        size = size_;
        x = x_;
        y = y_;
        xspeed = 0;
        yspeed = 0;
        img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(0, 0, size, size);
    }

    double getXspeed() {return xspeed;}
    double getYspeed() {return yspeed;}

    void setXspeed(double n) {xspeed = n;}
    void setYspeed(double n) {yspeed = n;}

    int getX() {return x;}
    int getY() {return y;}

    void addX(int x_) {x += x_;}
    void addY(int y_) {y += y_;}
    void addX(double x_) {x += x_;}
    void addY(double y_) {y += y_;}

    void setX(int x_) {x = x_;}
    void setY(int y_) {y = y_;}

    int getSize() {return size;}

    BufferedImage getImg() {return img;}
}
