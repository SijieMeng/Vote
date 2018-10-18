import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

class ClientImple {
    //public Cipher cipherText;
    public BufferedReader clientInput;

    String Cli_Name() {
        String cliname = new String();
        try {
            do {
                System.out.print("Please input your name: ");
                cliname = this.clientInput.readLine();
                if ((cliname.isEmpty()|| ((!(cliname.equals("Bob"))&&(!(cliname.equals("Alice")))&&(!(cliname.equals("John"))))))) {
                    System.out.println("Error. It's a wrong name!");
                }
            } while((cliname.isEmpty()|| ((!(cliname.equals("Bob"))&&(!(cliname.equals("Alice")))&&(!(cliname.equals("John")))))));

        } catch (IOException ex) {
        handleException(ex, "Here is an I/O error!");
    }
        return cliname;
    }

    String Cli_Num() {
        String cli_num = new String();
        int len = 9;
        try {
            do {
                System.out.print("Please input your number: ");
                cli_num = this.clientInput.readLine();
                if (cli_num.length()!=len) {
                    System.out.println("Error. It's a wrong number!");
                }else if (((!(cli_num.equals("113880000"))&&(!(cli_num.equals("112550000")))&&(!(cli_num.equals("114660000")))))){
                    System.out.println("Here is an error with your number!");
                }
            } while (cli_num.length()!=len||((!(cli_num.equals("113880000"))&&(!(cli_num.equals("112550000")))&&(!(cli_num.equals("114660000"))))));
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
        return cli_num;
    }

    String Cli_Menu(String votname) {
        String selection = new String();
        try {
        do {
            System.out.println("Welcome, " + votname);
            System.out.println("    Main Menu");
            System.out.println("Please enter a number (1-4)");
            System.out.println("1. Vote");
            System.out.println("2. My vote history");
            System.out.println("3. Election result");
            System.out.println("4. Quit");
            System.out.print("Please enter your selection: ");
            selection = this.clientInput.readLine();
            boolean pi = (selection.isEmpty()|| ((!(selection.equals("1"))&&(!(selection.equals("2")))&&(!(selection.equals("3")))&&(!(selection.equals("4"))))));
            if (pi) {
                System.out.println("Error. Selection number is 1, 2, 3, 4!");
            }
        } while ((selection.isEmpty()|| ((!(selection.equals("1"))&&(!(selection.equals("2")))&&(!(selection.equals("3")))&&(!(selection.equals("4")))))));
    } catch (IOException ex) {
        handleException(ex, "Here is an I/O error!");
    }
        return selection;
    }

    String Vot_Menu() {
        String selection = new String();
        do {
            System.out.println("Please enter a number (1-2)");
            System.out.println("1. Tim");
            System.out.println("2. Linda");
            System.out.print("Please enter your selection: ");
            try {
                selection = this.clientInput.readLine();
            } catch (IOException ex) {
                handleException(ex, "");
            }
            //boolean  = (selection.isEmpty()|| ((!(selection.equals("1"))&&(!(selection.equals("2"))))));
            if ((selection.isEmpty()|| ((!(selection.equals("1"))&&(!(selection.equals("2"))))))) {
                System.out.println("Error. Selection number is 1.Tim, 2. Linda!");
            }
        } while ((selection.isEmpty()|| ((!(selection.equals("1"))&&(!(selection.equals("2")))))));
        if(selection.equals("1")) {
            selection="01";
            return selection;
        }else {
            selection="02";
            return selection;
        }
    }
    private KeyPair genCliKey() {
        try {
            KeyPairGenerator genKey = KeyPairGenerator.getInstance("RSA");

            genKey.initialize(2048);
            KeyPair keyPub = genKey.generateKeyPair();

            FileOutputStream recordKey = new FileOutputStream("client_public.key");

            recordKey.write(keyPub.getPublic().getEncoded());
            recordKey.close();

            FileOutputStream keyPri = new FileOutputStream("client_private.key");

            keyPri.write(keyPub.getPrivate().getEncoded());
            keyPri.close();

            return keyPub;
        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        } catch (InvalidParameterException ex) {
            handleException(ex, "Invalid parameter!");
        } catch (FileNotFoundException ex) {
            handleException(ex, "File not found!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
        return null;
    }
    KeyPair cliKeyReceive() {
        try {
            KeyFactory proKey = KeyFactory.getInstance("RSA");

            FileInputStream keyPublic = new FileInputStream("client_public.key");
            byte[] keyPubSize = new byte[keyPublic.available()];

            keyPublic.read(keyPubSize);
            keyPublic.close();
            X509EncodedKeySpec keyPubpec = new X509EncodedKeySpec(keyPubSize);
            PublicKey keyPub = proKey.generatePublic(keyPubpec);

            FileInputStream keyPrivate = new FileInputStream("client_private.key");
            byte[] keyPriSize = new byte[keyPrivate.available()];

            keyPrivate.read(keyPriSize);
            keyPrivate.close();

            PKCS8EncodedKeySpec keyPripec = new PKCS8EncodedKeySpec(keyPriSize);
            PrivateKey keyPri = proKey.generatePrivate(keyPripec);

            return new KeyPair(keyPub, keyPri);

        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            return this.genCliKey();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (InvalidKeySpecException ex) {
            handleException(ex, "Invalid key spec!");
        }
        return null;
    }

    PublicKey serKeyReceive() {
        try {
            KeyFactory proKey = KeyFactory.getInstance("RSA");
            FileInputStream keyPublic = new FileInputStream("server_public.key");
            byte[] keyPubSize = new byte[keyPublic.available()];
            keyPublic.read(keyPubSize);
            keyPublic.close();
            X509EncodedKeySpec keyPubpec = new X509EncodedKeySpec(keyPubSize);
            return proKey.generatePublic(keyPubpec);

        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        } catch (FileNotFoundException ex) {
            handleException(ex, "File not found!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (InvalidKeySpecException ex) {
            handleException(ex, "Invalid key spec!");
        }
        return null;
    }
    SealedObject EncryptKeys(Key keyPub, String mesg) {
        try {
            Cipher gencipher = Cipher.getInstance("RSA");
            gencipher.init(Cipher.ENCRYPT_MODE, keyPub);
            return new SealedObject(mesg, gencipher);
        } catch (InvalidKeyException ex) {
            handleException(ex, "Invalid key!");
        } catch (IllegalBlockSizeException ex) {
            handleException(ex, "Illegal Block Size!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        }catch (NoSuchPaddingException ex) {
            handleException(ex, "No such padding!");
        }
        return null;
    }

    static void handleException(Exception error, String caution) {
        System.err.println(error.getMessage());
        System.err.println(caution);
        error.printStackTrace();
        System.exit(1);
    }

    ClientImple() { this.clientInput = new BufferedReader(new InputStreamReader(System.in));
    }
}
//java VC 127.0.0.1 5000