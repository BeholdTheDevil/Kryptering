import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by anton on 2017-09-06.
 */
public class EncryptionAlgorithm {

    /**
     * Extends the key so that it can properly encrypt the inputfile.
     * While the key length is less than the file length (in bytes) this function
     * repeatedly adds the hash of the current key to the key using SHA-256 and
     * then rehashes the key again. Uses the addAllBytes function to concat the
     * byte arrays of the key and the hash of the key.
     * @param key input key that gets extended.
     * @param length the length of the file.
     * @return a byte array containing the extended key.
     */
    private byte[] createKey(byte[] key, int length) {
        byte[] output = new byte[0];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            output = md.digest(key);

            while(output.length < length) {
                output = extendSingleKeyByte(key);
            }
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Error creating key: \n" + e.getMessage());
        }
        return output;
    }

    /**
     * Extends the key one cycle, used in the createKey method and to variably
     * extend the key when needed in network transmissions.
     * @param key input key that gets extended.
     * @see #createKey
     * @return the key extended by 256 bits (32 bytes).
     */
    private byte[] extendSingleKeyByte(byte[] key) {
        byte[] output = new byte[0];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            output = addAllBytes(key, md.digest(key));
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Error extending key: \n" + e.getMessage());
        }
        return output;
    }

    /**
     * Byte array concatenation function for utility
     * @param a original byte array.
     * @param b byte array that gets appended to a.
     * @return a byte array containing the values of b appended to a.
     */
    private byte[] addAllBytes(byte[] a, byte[] b) {
        byte[] temp = new byte[a.length + b.length];
        for(int i = 0; i < a.length; i++) {
            temp[i] = a[i];
        }
        for(int j = 0; j < b.length; j++) {
            temp[j + a.length] = b[j];
        }
        return temp;
    }

    /**
     * Remove the last specified bytes of an array.
     * Utility function for hiding the file extension in the encrypted file.
     * @param a the byte array to remove x bytes from.
     * @param x an int containing the amount of bytes to remove.
     * @return the byte array without the last x bytes.
     */
    public byte[] removeLastXBytes(byte[] a, int x) {
        byte[] temp = new byte[a.length-x];
        for(int i = 0; i < temp.length; i++) {
            temp[i] = a[i];
        }
        return temp;
    }

    /**
     * Utility function that prints out a byte array as binary.
     * @param a the byte array to print out.
     */
    public void printBytes(byte[] a) {
        for(byte b : a) {
            System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
    }

    /**
     * Applies the XOR operator to a byte array and a specified key.
     * Calls EncryptionAlgorithm.extendKey at start to make sure that key length is sufficient.
     * @param value the byte array to XOR.
     * @param key the byte array to XOR value with.
     * @return the byte array that is the result of applying the XOR operator to value with key.
     */
    private byte[] xorWithKey(byte[] value, byte[] key) {
        key = createKey(key, value.length);
        byte[] output = new byte[value.length];
        for(int i = 0; i < value.length; i++) {
            /*Perform XOR*/
            output[i] = (byte)(value[i] ^ key[i%key.length]);
        }
        return output;
    }

    /**
     * Public callable function for performing encryption of file
     * @param data byte array to encrypt.
     * @param key input String to be used as a key.
     * @return encrypted byte array.
     */
    public byte[] encodeFile(byte[] data, String key) {
        return xorWithKey(data, key.getBytes());
    }

    /**
     * Load file function, returns a byte array or exits if file is not found.
     * @param input File to try to load.
     * @return the loaded File as a byte array.
     */
    public byte[] loadFile(File input) {
        try {
            Path path = input.toPath();
            byte[] data = Files.readAllBytes(path);
            return data;
        } catch(IOException e) {
            System.out.println("Error parsing fileinputstream.");
            System.exit(0);
        }
        return null;
    }

    /**
     * Writes byte array to the specified path.
     * @param data byte array to write.
     * @param outpath the path to write the File to as a String.
     * @throws IOException
     */
    public void writeToFile(byte[] data, String outpath) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outpath)));
        dos.write(data);
        dos.flush();
        dos.close();
    }

    /**
     * Adds the file exetension of the input file to the end of the output file before encryption.
     * Allocates four bytes for the length of the extension plus the length of the extension in bytes.
     * See the ByteBuffer class for easy handling of byte array and reallocation of memory.
     * @param data byte array to append extension to.
     * @param extension extension to append to byte array.
     * @see ByteBuffer
     * @return byte array with the specified extension as well as the length of that extension at the end.
     */
    public byte[] setExtension(byte[] data, String extension) {
        byte[] byteExtension = ByteBuffer.allocate(4).putInt(extension.getBytes().length).array();
        byteExtension = addAllBytes(extension.getBytes(), byteExtension);
        return addAllBytes(data, byteExtension);
    }

    /**
     * Retrieves the length of the file extension from the decrypted file to make retrieval
     * of file extension possible.
     * @param data byte array to retrieve extension length from.
     * @return the length of the extension.
     */
    public int getExtensionLength(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(data.length-4);
    }

    /**
     * Retrieves the file extension based on the length previously retrieved from the decrypted file.
     * @param data byte array containing a file extension at the end.
     * @param length the length of the file extension excluding the four bytes allocated for the length itself.
     * @return a String containing the file extension.
     */
    public String getExtension(byte[] data, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        StringBuilder sb = new StringBuilder();

        for(int i = data.length-length-4; i < data.length-4; i++) {
            sb.append((char)buffer.get(i));
        }
        return sb.toString();
    }
}
