package CommandLine;

/**
 * Created by Anton on 2017-08-30.
 */
public class CMDInput {

    public static void main(String[] args) {

        switch(args.length) {
            case 1:
                System.out.println("Så du säger: " + args[0]);
                break;

            case 2:
                int a = 0;
                int b = 0;
                try {
                    a = Integer.parseInt(args[1]);
                    b = Integer.parseInt(args[2]);
                } catch(NumberFormatException nfe) {
                    System.out.println("Input var inte numeriskt. Avbryter...");
                    System.exit(0);
                }
                System.out.println(a + b);
                break;

            case 3:
                System.out.println(args[2] + " " + args[1] + " " + args[0]);
                break;

            case 4:
                System.out.println("Jag orkar inte mer, jag stänger ner");
                break;

            default:
                System.out.println("Programmet vet inte vad det ska göra! Avbryter...");
                break;
        }
    }
}
