import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by anton on 2017-08-30.
 */
public class GUI extends JFrame {

    private Container pane;
    private JPanel panel;
    private JButton encrypt, decrypt, browse, connect;
    private JTextField inputFilepath, outputFilepath, connectionAddress;
    private JPasswordField passwordField;

    //Note: Add network compatability

    GUI(int port) {
        this.setTitle("Kryptering");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        pane = this.getContentPane();

        createComponents(port);

        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initalizes components.
     */
    private void createComponents(int port) {
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(61, 61, 61));

        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 10;
        c.ipady = 10;
        pane.add(panel, c);


        // Connection address field
        c = new GridBagConstraints();
        connectionAddress = new JTextField();
        connectionAddress.setPreferredSize(new Dimension(400, 25));
        connectionAddress.setBorder(null);
        connectionAddress.setText("localhost:" + port);
        connectionAddress.setForeground(new Color(130, 130, 130));
        connectionAddress.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 6;
        c.gridheight = 1;
        c.insets = new Insets(0, 10, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(connectionAddress, c);


        // Connect button
        c = new GridBagConstraints();
        connect = new JButton("Connect");
        c.gridx = 6;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(10, 0, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(connect, c);


        // Input filepath field
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


        // Output filepath
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
        c.insets = new Insets(0, 10, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(outputFilepath, c);


        // Browse button
        c = new GridBagConstraints();
        browse = new JButton("Browse");
        c.gridx = 6;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(browse, c);


        // Password field
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
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, c);

        c = new GridBagConstraints();
        decrypt = new JButton("Decrypt");
        c.gridx = 6;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(decrypt, c);

        c = new GridBagConstraints();
        encrypt = new JButton("Encrypt");
        c.gridx = 6;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(encrypt, c);
    }

    /**
     * Adds the ActionListener created in Controller.addListeners to the encrypt button.
     * @param a ActionListener created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     */
    public void addEncryptActionListener(ActionListener a) {
        encrypt.addActionListener(a);
    }

    /**
     * Adds the ActionListener created in Controller.addListeners to the connect button.
     * @param a ActionListener created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     */
    public void addConnectListener(ActionListener a) {
        connect.addActionListener(a);
    }

    /**
     * Adds the FocusAdapter created in Controller.addListeners to the connection address field.
     * @param f FocusAdapter created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     * @see FocusAdapter
     */
    public void addConnectAddressFocusListener(FocusAdapter f) {
        connectionAddress.addFocusListener(f);
    }

    /**
     * Adds the ActionListener created in Controller.addListeners to the decrypt button.
     * @param a ActionListener created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     * @see ActionListener
     */
    public void addDecryptActionListener(ActionListener a) {
        decrypt.addActionListener(a);
    }

    /**
     * Adds the ActionListener created in Controller.addListeners to the browse button.
     * @param a ActionListener created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     * @see ActionListener
     */
    public void addBrowseActionListener(ActionListener a) {
        browse.addActionListener(a);
    }

    /**
     * Adds the FocusAdapter created in Controller.addListeners to the outputpath field.
     * @param f FocusAdapter created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     * @see FocusAdapter
     */
    public void addOutputFilepathFocusListener(FocusListener f) {
        outputFilepath.addFocusListener(f);
    }

    /**
     * Adds the FocusAdapter created in Controller.addListeners to the password field.
     * @param f FocusAdapter created in Controller.addListeners.
     * @see Controller#addListeners(GUI, EncryptionAlgorithm, NetworkProtocol)
     * @see FocusAdapter
     */
    public void addPasswordFieldFocusListener(FocusListener f) {
        passwordField.addFocusListener(f);
    }

    /**
     * Retrieves the content of the password field.
     * @return the content of the password field as a char array.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Sets the content of the inputfile field to the specified String.
     * @param s the specified String.
     */
    public void setInputFilepath(String s) {
        inputFilepath.setText(s);
    }

    /**
     * Sets the content of the password field to the specified String.
     * @param s the specified String.
     */
    public void setPasswordText(String s) {
        passwordField.setText(s);
    }

    /**
     * Password obfuscation.
     * Sets the echochar of the password field to the specified char
     * instead of the actual chars typed in by the user.
     * @param c the char to be shown instead of the actual char.
     */
    public void setPasswordEchoChar(char c) {
        passwordField.setEchoChar(c);
    }

    /**
     * Sets the content of the outputfile field to the specified String.
     * @param s the specified String.
     */
    public void setOutputText(String s) {
        outputFilepath.setText(s);
    }

    /**
     * Retrieves the content of the outputfile field.
     * @return the content of the outputfile field as a String.
     */
    public String getOutputText() {
        return outputFilepath.getText();
    }

    /**
     * Sets the content of the connection address field to the specified String.
     * @param s the specified String.
     */
    public void setConnectionAddress(String s) {
        connectionAddress.setText(s);
    }

    /**
     * Retrieves the content of the connection address field.
     * @return the content of the connection address field as a String.
     */
    public String getConnectionAddress() {
        return connectionAddress.getText();
    }

    /**
     * Creates a Yes or No prompt with the specified text and title.
     * Uses the JOptionPane.showConfirmDialog to create a Yes or No
     * prompt with the specified text and returns the value of that dialog.
     * @param s the question or statement String.
     * @see JOptionPane
     * @return the int value returned by pressing Yes or No in the prompt.
     */
    public int createPrompt(String s) {
        return JOptionPane.showConfirmDialog(null, s, s, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Creates a Yes or No prompt with the specified text and title.
     * Uses the JOptionPane.showConfirmDialog to create a Yes or No
     * prompt with the specified text (s) and title (s1) and returns the value
     * of that dialog.
     * @param s the question or statement String.
     * @param s1 the title String.
     * @see JOptionPane
     * @return the int value returned by pressing Yes or No in the prompt.
     */
    public int createPrompt(String s, String s1) {
        return JOptionPane.showConfirmDialog(null, s1, s, JOptionPane.YES_NO_OPTION);
    }
}
