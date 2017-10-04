package Sprint_5;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by anton on 2017-10-04.
 */
public class FileCopy {

    @FunctionalInterface
    interface Mode {
        void execute(DataInputStream dis, DataOutputStream dos);
    }

    public static void main(String[] args) {
        String input, filename;
        String mode;
        Scanner scan = new Scanner(System.in);

        HashMap<String, FileCopy.Mode> modeMap = new HashMap<>();

        modeMap.put("-b", (dis, dos) -> binaryCopy(dis, dos));
        modeMap.put("-t", (dis, dos) -> textCopy(dis, dos));

        do {
            System.out.print("Enter a filename > ");
            input = scan.nextLine();
            filename = Paths.get(input).toAbsolutePath().toString();
            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename.substring(0, filename.lastIndexOf("/") + 1) + "KopiaAv" + filename.substring(filename.lastIndexOf("/") + 1, filename.length()))));

                while(true) {
                    System.out.print("Enter a mode (-b or -t) > ");
                    mode = scan.nextLine();

                    try {
                        modeMap.get(mode).execute(dis, dos);
                        break;
                    } catch(NullPointerException e) {
                        System.out.println("Invalid input!");
                    }
                    System.out.println("");
                }

            } catch(FileNotFoundException e) {
                if(!input.equals("-1"))
                    System.out.println("Invalid input!");
            }
            System.out.println("");
        } while(!input.equals("-1"));
    }

    private static void binaryCopy(DataInputStream dis, DataOutputStream dos) {
        while(true) {
            try {
                dos.write(dis.readByte());
            } catch(IOException e) {
                break;
            }
        }
        try {
            dos.flush();
            dos.close();
            dis.close();
        } catch(IOException e) {
            System.out.println("Error closing files");
        }
    }

    private static void textCopy(DataInputStream dis, DataOutputStream dos) {
        char c;
        do {
            try {
                c = dis.readChar();
                dos.write(c);
            } catch(IOException e) {
                break;
            }
        } while(c != '\u001a');
        try {
            dos.flush();
            dis.close();
            dis.close();
        } catch(IOException e) {
            System.out.println("Error closing files");
        }
    }
}
