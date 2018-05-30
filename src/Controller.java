import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

/**
 * Created by anton on 2017-09-06.
 */
public class Controller {

    private final int port = 63035;
    private File inputFile;
    private String outputFilepath;

    //Note: ADD COMMANDLINE SUPPORT
    /*
    Controller(String[] args) {

    }
    */

    Controller() {
        GUI view = new GUI(port);
        NetworkProtocol ntp = new NetworkProtocol(port);
        EncryptionAlgorithm model = new EncryptionAlgorithm();
        addListeners(view, model, ntp);
        runNetworking(view, model, ntp);
    }

    private void runNetworking(GUI view, EncryptionAlgorithm model, NetworkProtocol ntp) {
        Socket socket;
        int accCon;

        while(true) {
            socket = ntp.getConnection();
            //Query user before allowing connection
            accCon = JOptionPane.showConfirmDialog(null, "Accept connection from " + socket.getInetAddress() + "?", "Connection Alert", JOptionPane.YES_NO_OPTION);
            if(accCon == JOptionPane.YES_OPTION) {
                if(socket != null) {
                    ntp.connect(socket);
                    System.out.println("Connected");

                    //Read communicationheader, header size is currently one for filesize.
                    int size = ntp.readInt(socket);
                    System.out.println(size);

                    int readBytes = 0;
                    byte[] data = new byte[size];
                    byte[] b;
                    while((b = ntp.readByte(socket, 1)) != null) {
                        data[readBytes] = b[0];
                        readBytes++;
                    }

                    for(int i = 0; i < data.length; i++) {
                        System.out.println(data[0]);
                    }
                    /*try {
                        model.writeToFile(data, outputFilepath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            } else {
                ntp.disconnect(socket);
            }
        }
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
                if (view.getConnectionAddress().equals("localhost:" + port)) {
                    view.setConnectionAddress("");
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (view.getConnectionAddress().equals("")) {
                    view.setConnectionAddress("localhost:" + port);
                }
            }
        });

        view.addConnectListener(actionEvent -> {
            String[] addr = view.getConnectionAddress().split(":");
            Socket socket;
            if(addr.length > 1)
                ntp.setPort(Integer.parseInt(addr[1]));
            try {
                socket = ntp.connectTo(addr[0]);

                String response;
                while((response = ntp.readString(socket)) != null) {
                    if(response.equals("AC")) {
                        sendFile(view, model, socket);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            processFile(view, model, true);
        });

        view.addDecryptActionListener(actionEvent -> {
            processFile(view, model, false);
        });
    }

    private void sendFile(GUI view, EncryptionAlgorithm model, Socket socket) {
        String pass = new String(view.getPassword());
        String password = pass.equals("Password...") ? "" : pass;

        if(validateInputs(view, password, inputFile)) {
            /*Load file and fix extension of outputfile if the 'encode' flag is true*/
            byte[] fileData = model.loadFile(inputFile);
            System.out.println("File loaded as byte array");
            fileData = fixFileForEncoding(model, view.getOutputText(), inputFile.getPath(), fileData);
            System.out.println("File fixed for encoding");
            /*Passes filedata, together with password, to the actual processing of file.*/
            byte[] encodedFile = model.encodeFile(fileData, password);
            System.out.println("File encoded");

            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                System.out.println("Fileoutputstream opened");
                dos.writeInt(fileData.length);
                System.out.println("Data sent, length: " + fileData.length);
                for(int i = 0; i < encodedFile.length; i++) {
                    dos.write(encodedFile[i]);
                }
                dos.flush();
                dos.close();
            } catch (IOException e) {
                System.out.println("Error sending file to destination.");
                e.printStackTrace();
            }
        }

        view.setPasswordText("Password...");
        view.setPasswordEchoChar((char)0);
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
            output += input.substring(input.lastIndexOf("/") + 1, input.length());
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
