package CommandLine;

/**
 * Created by anton on 2017-08-30.
 */
public class CMDInput {

    public static void main(String[] args) {

        switch(args.length) {
            case 1:
                System.out.println("Så du säger: " + args[0]);
                break;

            case 2:
                System.out.println();
                break;

            case 3:
                System.out.println();
                break;

            case 4:
                System.out.println();
                break;

            default:
                System.out.println("Programmet vet inte vad det ska göra! Avbryter...");
                break;
        }
    }
}
