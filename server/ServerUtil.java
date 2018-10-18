import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

class ServerUtil {
   // private Cipher cipher;
   // private KeyPairGenerator keyGenerator;
    //private int votenum=0;

    ServerUtil() {
        /*try {
            this.cipher = Cipher.getInstance("RSA");
            this.keyGenerator = KeyPairGenerator.getInstance("RSA");
            this.keyGenerator.initialize(2048);
        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm");
        } catch (NoSuchPaddingException ex) {
            handleException(ex, "No such padding");
        } catch (InvalidParameterException ex) {
            handleException(ex, "Incorrect key size, wrong or not supported");
        }*/
    }

    private KeyPair genKeySer() {
        try {
            KeyPairGenerator genkey = KeyPairGenerator.getInstance("RSA");
            genkey.initialize(2048);
            KeyPair createkey = genkey.generateKeyPair();

            FileOutputStream keyPub = new FileOutputStream("server_public.key");
            keyPub.write(createkey.getPublic().getEncoded());
            keyPub.close();

            FileOutputStream keyPriv = new FileOutputStream("server_private.key");
            keyPriv.write(createkey.getPrivate().getEncoded());
            keyPriv.close();

            return createkey;
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
    static void handleException(Exception error, String caution) {
        System.err.println(error.getMessage());
        System.err.println(caution);
        error.printStackTrace();
        System.exit(1);
    }

    /*KeyPair genKeyPair() {
        return this.keyGenerator.genKeyPair();
    }*/

    Object DecryptKey(Key Ser_Key, SealedObject encrypt_key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, Ser_Key);
            return encrypt_key.getObject(cipher);
        } catch (InvalidKeyException ex) {
            handleException(ex, "Here is an error with cipher key!");
        } catch (IllegalBlockSizeException ex) {
            handleException(ex, "Illegal block size!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (ClassNotFoundException ex) {
            handleException(ex, "Class not found!");
        } catch (BadPaddingException ex) {
            handleException(ex, "Bad padding!");
        }catch (NoSuchPaddingException ex) {
            handleException(ex, "No such padding!");
        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        }
        return null;
    }
    PublicKey cliKeyReceive() {
        try {
            KeyFactory proKey = KeyFactory.getInstance("RSA");
            FileInputStream keyPublic = new FileInputStream("client_public.key");
            byte[] keyPubSize = new byte[keyPublic.available()];
            keyPublic.read(keyPubSize);
            keyPublic.close();
            X509EncodedKeySpec keyPubspec = new X509EncodedKeySpec(keyPubSize);
            return proKey.generatePublic(keyPubspec);
        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        } catch (FileNotFoundException ex) {
            handleException(ex, "File not found!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (InvalidKeySpecException ex) {
            handleException(ex, "Invalid key!");
        }
        return null;
    }

    KeyPair serKeyReceive() {
        try {
            KeyFactory proKey = KeyFactory.getInstance("RSA");

            FileInputStream keyPublic = new FileInputStream("server_public.key");
            byte[] keyPubSize = new byte[keyPublic.available()];
            keyPublic.read(keyPubSize);
            keyPublic.close();
            X509EncodedKeySpec keyPubSpec = new X509EncodedKeySpec(keyPubSize);
            PublicKey keyPub = proKey.generatePublic(keyPubSpec);

            FileInputStream keyPrivate = new FileInputStream("server_private.key");
            byte[] keyPriSize = new byte[keyPrivate.available()];
            keyPrivate.read(keyPriSize);
            keyPrivate.close();
            PKCS8EncodedKeySpec keyPriSpec = new PKCS8EncodedKeySpec(keyPriSize);
            PrivateKey keyPri = proKey.generatePrivate(keyPriSpec);

            return new KeyPair(keyPub, keyPri);
        } catch (NoSuchAlgorithmException ex) {
            handleException(ex, "No such algorithm!");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            return this.genKeySer();
        } catch (IOException ex) {
            handleException(ex, "I/O error!");
        } catch (InvalidKeySpecException ex) {
            handleException(ex, "Invalid key spec!");
        }
        return null;
    }
    ArrayList<Voter> voterPart() {
        ArrayList<Voter> vot = new ArrayList<Voter>();

        String message;
        try {
            BufferedReader readVoterinfo = new BufferedReader(new FileReader("voterinfo"));
            while ((message = readVoterinfo.readLine()) != null && !message.trim().isEmpty()) {
                vot.add(new Voter(message));
            }
            readVoterinfo.close();
        } catch (FileNotFoundException ex) {
            handleException(ex, "File not found!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }

        try {
            BufferedReader readHistory = new BufferedReader(new FileReader("history"));
            String[] hisRecord;
            while ((message = readHistory.readLine()) != null && !message.trim().isEmpty()) {
                hisRecord = message.split(" ");
                for (Voter voter : vot) {
                    if (voter.readVoterName().contains(hisRecord[0])) {
                        voter.VotedStatus();
                        voter.writeTime(hisRecord[1]);
                        break;
                    }
                }
            }
            readHistory.close();
        } catch (FileNotFoundException ex) {
            this.writeHistory();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            handleException(ex, "Here is an error with history file!");
        }

        return vot;
    }

    ArrayList<String> candidatePart() {
        ArrayList<String> candidate = new ArrayList<String>();
        String message;
        try {
            BufferedReader readcandidate = new BufferedReader(new FileReader("candidateinfo"));
            while ((message = readcandidate.readLine()) != null && !message.trim().isEmpty()) {
                candidate.add(message);
            }
            readcandidate.close();
        } catch (FileNotFoundException ex) {
            handleException(ex, "File not found!");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
        return candidate;
    }

    HashMap<String,Integer> showWinner() {
        HashMap<String,Integer> consque = new HashMap<String,Integer>();
        String message;
        String[] voteNum;
        try {
            BufferedReader resultFile = new BufferedReader(new FileReader("result"));
            while ((message = resultFile.readLine()) != null && !message.trim().isEmpty()) {
                voteNum = message.split(" ");
                consque.put(voteNum[0], Integer.parseInt(voteNum[1]));
            }
            resultFile.close();
        } catch (FileNotFoundException ex) {
            ArrayList<String> candidatelist = this.candidatePart();
            this.writeResult(candidatelist);
            for (String candidate : candidatelist) {
                consque.put(candidate, 0);
            }
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            handleException(ex, "Here is an error with history file!");
        }
        return consque;
    }

    private void writeHistory() {
        File recordHistory = new File("history");
        try {
            recordHistory.createNewFile();
            System.out.println("---write into the history file---");
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
    }

    private void writeResult(ArrayList<String> candidatename) {
        try {
            BufferedWriter recordResult = new BufferedWriter(new FileWriter("result"));
            for(String candidate : candidatename) {
                recordResult.write(candidate + " 0");
                recordResult.newLine();
                recordResult.flush();
            }
            System.out.println("---write into result file---");
            recordResult.close();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
    }
    
    boolean readHistory(Voter voterName) {
       if(voterName.readStatus())
    	   return true;
       else 
           return false;
    }

    void writeHistory(Voter voterName) {
        try {
            BufferedWriter recordHistory = new BufferedWriter(new FileWriter("history", true));
            voterName.VotedStatus();
            recordHistory.write(voterName.readVoterNum()+" "+voterName.readTime());
            recordHistory.newLine();
            recordHistory.flush();
            recordHistory.close();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
    }

    void updateResult(HashMap<String,Integer> result) {
        try {
            BufferedWriter recordResult = new BufferedWriter(new FileWriter("result"));
            for(String candidateName : result.keySet()) {
                recordResult.write(candidateName + " " + Integer.toString(result.get(candidateName)));
                System.out.println("write a new result"+" "+Integer.toString(result.get(candidateName)));
                recordResult.newLine();
                recordResult.flush();
            }
            recordResult.close();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
    }
    String historyRecords(String voterNum) {
    	String messgae;
    	String history_cont="-1";
    	String return_cons="-1";
    	boolean status = false;
        try {
            BufferedReader recordHistory = new BufferedReader(new FileReader("history"));
            while ((messgae = recordHistory.readLine()) != null && !messgae.trim().isEmpty()) {
            	String[] startHistory=messgae.split(" ");
            	if(voterNum.equals(startHistory[0])) {
            		status = true;
            		history_cont=messgae;
            		break;
            	}else {
            		status = false;
            	}
            }
            if(status==true) {
        		return_cons=history_cont;
        	}else {
        		return_cons="<--there are no history records-->";
        	}
            recordHistory.close();
        } catch (IOException ex) {
            handleException(ex, "Here is an I/O error!");
        }
        return return_cons;
    }
    
}
