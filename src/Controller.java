import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by anton on 2017-09-06.
 */
public class Controller {

    private GUI view;
    private EncryptionAlgorithm model;
    private File inputFile;
    private String password;
    private String outputFilepath;


    Controller() {
        view = new GUI();
        model = new EncryptionAlgorithm();

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
            processFile(true);
        });

        view.addDecryptActionListener(actionEvent -> {
            processFile(false);
        });
    }

    private boolean validateInputs() {
        return (inputFile != null && password != null && !password.equals("") && !view.getOutputText().equals("Output filepath...") && !view.getOutputText().equals(""));
    }

    private byte[] fixFileForEncoding(String output, String input, byte[] data) {
        String extension = input.substring(input.lastIndexOf('.'));
        if(output.contains(".")) output = output.substring(0, output.lastIndexOf('.'));
        output += ".crt";
        outputFilepath = output;
        return model.setExtension(data, extension);
    }

    private String fixFileForDecoding(String output, byte[] data) {
        String temp = new String(output);
        if(temp.contains(".")) {
            int outputLastIndex = temp.lastIndexOf('.');
            temp = temp.substring(0, outputLastIndex);
        }
        temp += model.getExtension(data, model.getExtensionLength(data));
        return temp;
    }

    private void processFile(boolean encode) {
        String pass = new String(view.getPassword());
        password = pass.equals("Password...") ? "" : pass;
        if(validateInputs()) {
            byte[] fileData = model.loadFile(inputFile);
            if(encode) fileData = fixFileForEncoding(view.getOutputText(), inputFile.getPath(), fileData);

            System.out.println("\n\n");

            byte[] encodedFile = model.encodeFile(fileData, password);          //Actual processing of encrypted file here
            if(!encode) {
                outputFilepath = fixFileForDecoding(view.getOutputText(), encodedFile);
                encodedFile = model.removeLastXBytes(encodedFile, model.getExtensionLength(encodedFile)+4);
            }

            File f = null;
            try {
                f = new File(outputFilepath);
                int returnval;

                if(f.exists() && !f.isDirectory()) {
                    returnval = view.createPrompt("Overwrite file?");
                    if(returnval == JOptionPane.YES_OPTION) {
                        model.writeToFile(encodedFile, outputFilepath);
                    }
                } else {
                    returnval = view.createPrompt("Create new file?");
                    if(returnval == JOptionPane.YES_OPTION) {
                        model.writeToFile(encodedFile, outputFilepath);
                    }
                }
            } catch(IOException e) {
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
        view.setPasswordText("Password...");
        view.setPasswordEchoChar((char)0);
    }
}
