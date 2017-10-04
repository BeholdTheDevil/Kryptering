package Sprint_4;

import javax.swing.*;

/**
 * Created by anton on 2017-09-20.
 */
public class OverlappingCircles extends JFrame {

    public OverlappingCircles() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFocusable(false);
        MyCanvas c = new MyCanvas();
        this.add(c);
        this.getContentPane().setPreferredSize(c.getPreferredSize());
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        new Thread(c).start();
    }
}
