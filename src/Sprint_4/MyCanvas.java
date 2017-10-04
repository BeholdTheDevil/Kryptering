package Sprint_4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Anton Degerfeldt on 2017-09-20.
 */

public class MyCanvas extends JPanel implements Runnable {

    private final int numberOfBalls = 1;

    ArrayList<Circle> circles = new ArrayList<>();
    BufferedImage drawImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
    BufferedImage placeholderImage;
    private final int GWIDTH = 700;
    private final int GHEIGHT = 500;
    private final int CSIZE = 50;
    private final Paddle playerOne = new Paddle(50, GHEIGHT/2 - 50, 5);
    private final Paddle playerTwo = new Paddle(GWIDTH-75, GHEIGHT/2 - 50, 5);

    @FunctionalInterface
    interface KeyAction {
        void doAction(int n);
    }

    MyCanvas() {
        this.setPreferredSize(new Dimension(GWIDTH, GHEIGHT));
        this.setBackground(new Color(51, 51, 51));
        this.setDoubleBuffered(true);
        this.setIgnoreRepaint(true);
        this.setFocusable(true);

        HashMap<Integer, KeyAction> keyDispatcher = new HashMap<>();

        keyDispatcher.put(KeyEvent.VK_W, ((n) -> {playerOne.move(-n);}));
        keyDispatcher.put(KeyEvent.VK_S, (n) -> playerOne.move(n));
        keyDispatcher.put(KeyEvent.VK_UP, (n) -> playerTwo.move(-n));
        keyDispatcher.put(KeyEvent.VK_DOWN, (n) -> playerTwo.move(n));
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                try {
                    keyDispatcher.get(keyEvent.getKeyCode()).doAction(3);
                } catch(NullPointerException e) {}
            }

            public void keyReleased(KeyEvent keyEvent) {
                try {
                    keyDispatcher.get(keyEvent.getKeyCode()).doAction(0);
                } catch (NullPointerException e) {}
            }
        });

        placeholderImage = new BufferedImage(GWIDTH, GHEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)placeholderImage.getGraphics();
        g.setColor(new Color(51, 51, 51));
        g.fillRect(0, 0, GWIDTH, GHEIGHT);

        Random rand = new Random();
        for (int i = 0; i < numberOfBalls; i++) {
            circles.add(new Circle(new Color(rand.nextInt(150), rand.nextInt(175), rand.nextInt(150)), rand.nextInt(GWIDTH - CSIZE), rand.nextInt(GHEIGHT - CSIZE), CSIZE));
            rand = new Random();
        }
        for (Circle c : circles) {
            //c.setXspeed(rand.nextInt(3) + 2);
            //c.setYspeed(rand.nextInt(3) + 2);
            c.setXspeed(3.0);
            c.setYspeed(0.0);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawImage = new BufferedImage(GWIDTH, GHEIGHT, BufferedImage.TYPE_INT_ARGB);
        WritableRaster wr = placeholderImage.copyData(null);
        drawImage.setData(wr);

        int[] writeArray = drawImage.getRGB(0, 0, this.getWidth(), this.getHeight(), null, 0, this.getWidth());
        for (Circle c : circles) {
            writeArray = loadCircleImage(writeArray, c.getX() + c.getY() * this.getWidth(), c);
        }

        drawImage.setRGB(0, 0, this.getWidth(), this.getHeight(), writeArray, 0, this.getWidth());

        g2d.drawImage(drawImage, 0, 0, null);
        g2d.setColor(Color.white);
        g2d.fillRect(playerOne.getX(), playerOne.getY(), playerOne.getXsize(), playerOne.getYsize());
        g2d.fillRect(playerTwo.getX(), playerTwo.getY(), playerTwo.getXsize(), playerTwo.getYsize());
        g2d.dispose();
    }

    private int[] loadCircleImage(int[] temp, int start, Circle c) {
        int[] data = c.getImg().getRGB(0, 0, c.getImg().getWidth(), c.getImg().getHeight(), null, 0, c.getImg().getWidth());
        int index = start;
        for (int i = 0; i < data.length; i++) {
            temp[index + i] = temp[index + i] | data[i];
            if ((i + 1) % c.getSize() == 0) index += GWIDTH - CSIZE;
        }
        return temp;
    }

    @Override
    public void run() {

        while (true) {

            for(Circle c : circles) {
                if(c.getX() + c.getSize() + c.getXspeed() >= GWIDTH || c.getX() + c.getXspeed() <= 0) {
                    int newspeed = (int)Math.round(c.getXspeed() * -1f);
                    c.setXspeed(newspeed > 15 ? 15 : newspeed);
                }

                if(playerOne.checkCollision(c)) {
                    playerOne.bounce(c);
                }
                if(playerTwo.checkCollision(c)) {
                    playerTwo.checkCollision(c);
                }

                if(c.getY() + c.getSize() + c.getYspeed() >= GHEIGHT || c.getY() + c.getYspeed() <= 0) {
                    int newspeed = (int)Math.round(c.getYspeed() * -1f);
                    c.setYspeed(newspeed > 15 ? 15 : newspeed);
                }

                c.addX(c.getXspeed());
                c.addY(c.getYspeed());
            }

            if(playerOne.getY() + playerOne.getYsize() + playerOne.getVelocity() < GHEIGHT && playerOne.getY() + playerOne.getVelocity() > 0) {
                playerOne.setY(playerOne.getY() + playerOne.getVelocity());
            }

            if(playerTwo.getY() + playerTwo.getYsize() + playerTwo.getVelocity() < GHEIGHT && playerTwo.getY() + playerTwo.getVelocity() > 0) {
                playerTwo.setY(playerTwo.getY() + playerTwo.getVelocity());
            }

            validate();
            repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        }
    }
}