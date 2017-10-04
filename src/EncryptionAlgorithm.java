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

    private byte[] extendKey(byte[] key, int length) {
        byte[] output;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            output = md.digest(key);
            while(output.length < length) {
                output = addAllBytes(output, md.digest(output));
            }
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    private byte[] addAllBytes(byte[] a, byte[] b) {
        byte[] temp = new byte[a.length + b.length];
        for(int i = 0; i < a.length; i++) {
            temp[i] = a[i];
        }
        for(int j = 0; j < b.length; j++) {
            temp[j+a.length] = b[j];
        }
        return temp;
    }

    public byte[] removeLastXBytes(byte[] a, int x) {
        byte[] temp = new byte[a.length-x];
        for(int i = 0; i < temp.length; i++) {
            temp[i] = a[i];
        }
        return temp;
    }

    public void printBytes(byte[] a) {
        for(int i = 0; i < a.length; i++) {
            System.out.print(String.format("%8s", Integer.toBinaryString(a[i] & 0xFF)).replace(' ', '0'));
        }
    }

    private byte[] xorWithKey(byte[] value, byte[] key) {
        key = extendKey(key, value.length);
        byte[] output = new byte[value.length];
        for(int i = 0; i < value.length; i++) {
            output[i] = (byte)(value[i] ^ key[i%key.length]);
        }
        return output;
    }

    //String encoding and decoding for testing
    public String encode(String value, String key) {
        return base64Encode(xorWithKey(value.getBytes(), key.getBytes()));
    }
    private String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes).replaceAll("\\s", "");
    }

    public String decode(String value, String key) {
        return new String(xorWithKey(base64Decode(value), key.getBytes()));
    }
    private byte[] base64Decode(String value) {
        return Base64.getDecoder().decode(value);
    }

    public byte[] encodeFile(byte[] data, String key) {
        return xorWithKey(data, key.getBytes());
    }

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

    public void writeToFile(byte[] data, String outpath) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outpath)));
        dos.write(data);
        dos.flush();
        dos.close();
    }

    public byte[] setExtension(byte[] data, String extension) {
        byte[] byteExtension = ByteBuffer.allocate(4).putInt(extension.getBytes().length).array();
        byteExtension = addAllBytes(extension.getBytes(), byteExtension);
        return addAllBytes(data, byteExtension);
    }

    public int getExtensionLength(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(data.length-4);
    }

    public String getExtension(byte[] data, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        StringBuilder sb = new StringBuilder();
        for(int i = data.length-length-4; i < data.length-4; i++) {
            sb.append((char)buffer.get(i));
        }
        //data = removeLastXBytes(data, length+4);
        return sb.toString();
    }
}
