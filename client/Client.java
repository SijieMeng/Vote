import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;

public class Client {
    private Socket client;
    private ClientImple imple;
    private KeyPair clientKeys;

    public Client(String serAcpt, int portNum) {
        try {
            this.client = new Socket(serAcpt, portNum);
            this.imple = new ClientImple();
            this.clientKeys = this.imple.cliKeyReceive();
        } catch (UnknownHostException ex) {
            ClientImple.handleException(ex, "Here is an error with unknown host!");
        } catch (IOException ex) {
            ClientImple.handleException(ex, "Here is an I/O error!");
        } catch (IllegalArgumentException ex) {
            ClientImple.handleException(ex, "Here is an illegal argument!");
        }
    }

    public void Cli_Execution() {
        try {

            String votername = this.imple.Cli_Name();
            String voternumber = this.imple.Cli_Num();
            String voteridentity = votername + ' ' + voternumber;

            ObjectOutputStream cli_output = new ObjectOutputStream(this.client.getOutputStream());
            ObjectInputStream cli_input = new ObjectInputStream(this.client.getInputStream());

            PublicKey keyVoter = (PublicKey) cli_input.readObject();

            cli_output = new ObjectOutputStream(this.client.getOutputStream());

            cli_input = new ObjectInputStream(this.client.getInputStream());
            PublicKey keyCli=this.clientKeys.getPublic();


            cli_output.writeObject(this.clientKeys.getPublic());
            PublicKey keySer = keyVoter;

            Signature signatureVoter = Signature.getInstance("SHA256withRSA");
            signatureVoter.initSign(this.clientKeys.getPrivate());
            signatureVoter.update(votername.getBytes());

            cli_output.writeObject(imple.EncryptKeys(keySer, voteridentity));
            cli_output.flush();
            cli_output.write(signatureVoter.sign());
            cli_output.flush();



            cli_output = new ObjectOutputStream(this.client.getOutputStream());
            cli_input = new ObjectInputStream(this.client.getInputStream());
            /*short result = 1;
                    result=(short)cli_input.readUnsignedShort();*/
            short result = (short)cli_input.readObject();
            System.out.println(result);
            if (result != 1) {
                System.out.println("Invalid votername or registration number");

            } else {
                String option;
                short votoption;
                do {

                    option = this.imple.Cli_Menu(votername);
                    if (option.equals("1")) {

                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        votoption=1;
                        cli_output.writeObject(votoption);

                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        result = (short)cli_input.readObject();
                        if(result!=0) {
                            option =this.imple.Vot_Menu();

                            if (option.equals("01")) {
                                System.out.println("<--you voted Tim-->");
                                cli_output = new ObjectOutputStream(this.client.getOutputStream());
                                cli_input = new ObjectInputStream(this.client.getInputStream());
                                cli_output.writeObject(imple.EncryptKeys(keySer, option));
                                System.out.println("---record your selection---");
                            }else if (option.equals("02")) {
                                System.out.println("<--you voted Linda-->");
                                cli_output = new ObjectOutputStream(this.client.getOutputStream());
                                cli_input = new ObjectInputStream(this.client.getInputStream());
                                cli_output.writeObject(imple.EncryptKeys(keySer, option));
                            }
                        }
                        else {
                            System.out.println("Sorry. You have voted successfully.");
                        }

                    }

                    if (option.equals("2")) {
                        System.out.println("---client is requesting to read history---");

                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        votoption=2;
                        cli_output.writeObject(votoption);

                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        String readRecord;
                        readRecord = (String) cli_input.readObject();
                        System.out.println(readRecord);

                    }
                    if (option.equals("3")) {
                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        votoption=3;
                        cli_output.writeObject(votoption);
                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        String showResult;
                        showResult = (String) cli_input.readObject();
                        if(showResult.equals("0"))
                            System.out.println("<--the result is not available-->");
                        else
                            System.out.println(showResult);
                    }
                    if (option.equals("4")) {
                        cli_output = new ObjectOutputStream(this.client.getOutputStream());
                        cli_input = new ObjectInputStream(this.client.getInputStream());
                        votoption=4;
                        cli_output.writeObject(votoption);
                        System.out.println("---client have quited---");
                    }
                } while (!option.equals("4"));
            }
        } catch (IOException ex) {
            ClientImple.handleException(ex, " client Here is an I/O error!");
        } catch (ClassNotFoundException ex) {
            System.exit(1);
        }catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException ex) {
            ClientImple.handleException(ex, "Here is an I/O error!");
        }
    }


}
