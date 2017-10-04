import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by anton on 2017-08-30.
 */

public class GUI extends JFrame {

    private Container pane;
    private JPanel panel;
    private JButton encrypt, decrypt, browse;
    private JTextField inputFilepath, outputFilepath;
    private JPasswordField passwordField;
    private JFileChooser fc;
    private JOptionPane prompt;


    GUI() {
        this.setTitle("Kryptering");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        pane = this.getContentPane();

        createComponents();

        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createComponents() {
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(61, 61, 61));

        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 10;
        c.ipady = 10;
        pane.add(panel, c);


        c = new GridBagConstraints();
        inputFilepath = new JTextField();
        inputFilepath.setPreferredSize(new Dimension(400, 25));
        inputFilepath.setBorder(null);
        inputFilepath.setBackground(new Color(90, 90, 90));
        inputFilepath.setDisabledTextColor(new Color(50, 50, 50));
        inputFilepath.setEditable(false);
        inputFilepath.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 6;
        c.gridheight = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputFilepath, c);


        c = new GridBagConstraints();
        outputFilepath = new JTextField();
        outputFilepath.setPreferredSize(new Dimension(400, 25));
        outputFilepath.setBorder(null);
        outputFilepath.setText("Output filepath...");
        outputFilepath.setForeground(new Color(130, 130, 130));
        outputFilepath.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));


        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 6;
        c.gridheight = 1;
        c.insets = new Insets(0, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(outputFilepath, c);


        c = new GridBagConstraints();
        browse = new JButton("Browse");
        c.gridx = 6;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        panel.add(browse, c);

        c = new GridBagConstraints();
        passwordField = new JPasswordField("");
        passwordField.setPreferredSize(new Dimension(400, 25));
        passwordField.setBorder(null);
        passwordField.setText("Password...");
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(new Color(130, 130, 130));
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 6;
        c.gridheight = 1;
        c.insets = new Insets(10, 10, 10, 10);
        panel.add(passwordField, c);

        c = new GridBagConstraints();
        decrypt = new JButton("Decrypt");
        c.gridx = 6;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        panel.add(decrypt, c);

        c = new GridBagConstraints();
        encrypt = new JButton("Encrypt");
        c.gridx = 6;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        panel.add(encrypt, c);
    }

    public void addEncryptActionListener(ActionListener a) {
        encrypt.addActionListener(a);
    }

    public void addDecryptActionListener(ActionListener a) {
        decrypt.addActionListener(a);
    }

    public void addBrowseActionListener(ActionListener a) {
        browse.addActionListener(a);
    }

    public void addOutputFilepathFocusListener(FocusListener f) {
        outputFilepath.addFocusListener(f);
    }

    public void addPasswordFieldFocusListener(FocusListener f) {
        passwordField.addFocusListener(f);
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void setInputFilepath(String s) {
        inputFilepath.setText(s);
    }

    public void setPasswordText(String s) {
        passwordField.setText(s);
    }

    public void setPasswordEchoChar(char c) {
        passwordField.setEchoChar(c);
    }

    public void setOutputText(String s) {
        outputFilepath.setText(s);
    }

    public String getOutputText() {
        return outputFilepath.getText();
    }

    public int createPrompt(String s) {
        return JOptionPane.showConfirmDialog(null, s, s, JOptionPane.YES_NO_OPTION);
    }

    public int createPrompt(String s, String s1) {
        return JOptionPane.showConfirmDialog(null, s1, s, JOptionPane.YES_NO_OPTION);
    }
}
