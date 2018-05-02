import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by anton on 2017-09-06.
 */
public class Controller {

    private final int port = 63036;
    private File inputFile;
    private String outputFilepath;

    //Note: ADD COMMANDLINE SUPPORT
    /*
    Controller(String[] args) {

    }
    */

    Controller() {
        GUI view = new GUI();
        NetworkProtocol ntp = new NetworkProtocol(port);
        new Thread(ntp).start();
        EncryptionAlgorithm model = new EncryptionAlgorithm();
        addListeners(view, model, ntp);
    }

    /**
     * Adds FocusAdapters and ActionListeners to the GUI object.
     * Adds FocusAdapters to the textfields for removing and replacing placeholder text.
     * Also adds ActionListeners to buttons.
     * @param view GUI access.
     * @param model EncryptionAlgorithm access.
     */
    private void addListeners(GUI view, EncryptionAlgorithm model, NetworkProtocol ntp) {

        view.addOutputFilepathFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (view.getOutputText().equals("Output filepath...")) {
                    view.setOutputText("");
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (view.getOutputText().equals("")) {
                    view.setOutputText("Output filepath...");
                }
            }
        });

        view.addConnectAddressFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (view.getConnectionAddress().equals("localhost:63036")) {
                    view.setConnectionAddress("");
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (view.getConnectionAddress().equals("")) {
                    view.setConnectionAddress("localhost:63036");
                }
            }
        });

        view.addConnectListener(actionEvent -> {

        });

        view.addPasswordFieldFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (new String(view.getPassword()).equals("Password...")) {
                    view.setPasswordText("");
                }
                view.setPasswordEchoChar('*');
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (new String(view.getPassword()).equals("")) {
                    view.setPasswordText("Password...");
                    view.setPasswordEchoChar((char) 0);
                }
            }
        });

        view.addBrowseActionListener(actionEvent -> {

            JFileChooser fc = new JFileChooser();
            try {
                fc.setCurrentDirectory(new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
            } catch(URISyntaxException e) {
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            }
            int returnval = fc.showOpenDialog(null);
            if(returnval == JFileChooser.APPROVE_OPTION) {
                inputFile = fc.getSelectedFile();
                view.setInputFilepath(inputFile.getPath());
                view.setOutputText(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf('/') + 1));
            }
        });

        view.addEncryptActionListener(actionEvent ->  {
            processFile(view, model, true);
        });

        view.addDecryptActionListener(actionEvent -> {
            processFile(view, model, false);
        });
    }

    /**
     * Validates all inputfields.
     * @param view GUI access.
     * @param password the password as a String.
     * @param inputFile the File that is to be encrypted.
     * @return true for valid inputs and False for invalid inputs.
     */
    private boolean validateInputs(GUI view, String password, File inputFile) {
        return (inputFile != null && password != null && !password.equals("") && !view.getOutputText().equals("Output filepath...") && !view.getOutputText().equals(""));
    }

    /**
     * Verifies and retrieves the inputfile extension and sets the outputfile extension
     * to *.crt when encrypting. If no outputfile is given, defaults to 'inputfile'.crt.
     * @param model EncryptionAlgorithm access.
     * @param output String representing the path to the output.
     * @param input String representing the path to the input.
     * @param data byte array containing the data that is later encrypted.
     * @return a byte array of the inputfile with the inputfile extension appended to the end of the array.
     */
    private byte[] fixFileForEncoding(EncryptionAlgorithm model, String output, String input, byte[] data) {
        String extension = input.substring(input.lastIndexOf('.'));

        if(output.endsWith("/")) {
            output += input.substring(input.lastIndexOf("/")+1, input.length());
        }

        if(output.contains(".")) {
            output = output.substring(0, output.lastIndexOf('.'));
        }

        output += ".crt";
        outputFilepath = output;

        return model.setExtension(data, extension);
    }

    /**
     * Validates output filename for decoding.
     * Validates output filename for decoding and changes extension to the extension
     * hidden within the encrypted file. Uses EncryptionAlgorithm.getExtension
     * and EncryptionAlgorithm.getExtensionLength to retrieve the extension.
     * @param model EncryptionAlgorithm access.
     * @param output String representing the path to the output.
     * @param data byte array containing the data that is later decrypted.
     * @return the output filename with a correct extension.
     */
    private String fixFileForDecoding(EncryptionAlgorithm model, String output, byte[] data) {
        String temp = new String(output);

        if(temp.contains(".")) {
            int outputLastIndex = temp.lastIndexOf('.');
            temp = temp.substring(0, outputLastIndex);
        }
        temp += model.getExtension(data, model.getExtensionLength(data));
        return temp;
    }

    /**
     * File processing, either encrypts or decrypts.
     * @param view GUI access.
     * @param model EncryptionAlgorithm access.
     * @param encode to encrypt or not to encrypt. #Shakespeare
     */
    private void processFile(GUI view, EncryptionAlgorithm model, boolean encode) {

        String pass = new String(view.getPassword());
        String password = pass.equals("Password...") ? "" : pass;

        if(validateInputs(view, password, inputFile)) {
            /*Load file and fix extension of outputfile if the 'encode' flag is true*/
            byte[] fileData = model.loadFile(inputFile);
            if(encode) fileData = fixFileForEncoding(model, view.getOutputText(), inputFile.getPath(), fileData);

            /*Passes filedata, together with password, to the actual processing of file.*/
            byte[] encodedFile = model.encodeFile(fileData, password);

            if(!encode) {
                outputFilepath = fixFileForDecoding(model, view.getOutputText(), encodedFile);
                /*Removes the file extension bytes from the byte array before it is written out.*/
                encodedFile = model.removeLastXBytes(encodedFile, model.getExtensionLength(encodedFile)+4);
            }

            File f = null;
            try {
                f = new File(outputFilepath);
                createOrOverwriteFile(view, model, f, encodedFile);
            } catch(IOException e) {
                createDirectories(view, model, f, encodedFile);
            }
        }

        view.setPasswordText("Password...");
        view.setPasswordEchoChar((char)0);
    }

    /**
     * File creation or overwriting if necessary.
     * @param view GUI access.
     * @param model EncryptionAlgorithm access.
     * @param f File to write.
     * @param encodedFile byte array containing the encrypted data.
     * @throws IOException
     */
    private void createOrOverwriteFile(GUI view, EncryptionAlgorithm model, File f, byte[] encodedFile) throws IOException {
        int response;

        if(f.exists() && !f.isDirectory()) {
            response = view.createPrompt("Overwrite file?");

            if(response == JOptionPane.YES_OPTION) {
                model.writeToFile(encodedFile, outputFilepath);
            }
        } else {
            response = view.createPrompt("Create new file?");

            if(response == JOptionPane.YES_OPTION) {
                model.writeToFile(encodedFile, outputFilepath);
            }
        }
    }

    /**
     * Create new directories if necessary.
     * @param view GUI access.
     * @param model EncryptionAlgorithm access.
     * @param f File to write.
     * @param encodedFile byte array containing the encrypted data.
     */
    private void createDirectories(GUI view, EncryptionAlgorithm model, File f, byte[] encodedFile) {

        int returnval = view.createPrompt("Directory not found.", "Create directories?");

        if(f != null && returnval == JOptionPane.YES_OPTION) {
            f.getParentFile().mkdirs();

            try {
                model.writeToFile(encodedFile, outputFilepath);
            } catch(IOException ie) {
                ie.printStackTrace();
            }
        }
    }
}
