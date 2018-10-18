import javax.crypto.SealedObject;
import java.io.InvalidClassException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread implements Runnable {
		private ServerSocket listen;
	    private ServerUtil util;
	   // private KeyPair keys;
	    private ArrayList<Voter> voters;
	    private HashMap<String,Integer> result;
		private KeyPair serverKeys;
		//private Socket socket;
	    
		Socket socket = null;  
	    

	    public ServerThread(Socket serSoc)  {
	        //try {
	        	//Socket serSoc = null;  
	            this.socket = serSoc;
	            
	           


	            this.util = new ServerUtil();
                this.serverKeys = this.util.serKeyReceive();
	            //this.keys = this.util.genKeyPair();
	            this.voters = this.util.voterPart();
	            this.result = this.util.showWinner();

	    }

	    public void run() {
	        while (true) {
	            try {
                    //int counter=0;

                    //Socket socket = this.listen.accept();

                    //Vf serverThread = new Vf(connect);
                    //serverThread.start();

                    //counter++;
                    //System.out.println("total counter is:"+counter);
                    //(new FileSaver(connect)).start();

                    //Thread thread = new Thread((Runnable) new Vf(portNumber));
                    //thread.start();
                    //Vf votingFacility = new Vf(portNumber);


                    System.out.println("---server executing---");

                    ObjectOutputStream serOutput = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream serInput = new ObjectInputStream(socket.getInputStream());
                    serOutput.writeObject(this.serverKeys.getPublic());

                    //serOutput = new ObjectOutputStream(socket.getOutputStream());
                    serInput = new ObjectInputStream(socket.getInputStream());
                    serOutput = new ObjectOutputStream(socket.getOutputStream());

                    PublicKey keyCliPub = (PublicKey) serInput.readObject();
                    //System.out.println(vfPublicKey);
                    PublicKey keyCli = keyCliPub;

                    SealedObject voterEncrypt = (SealedObject) serInput.readObject();
                    //System.out.println(voterEncrypt);
                    byte[] signatureSize = new byte[256];
                    serInput.readFully(signatureSize);


                    String readVoter = (String) this.util.DecryptKey(this.serverKeys.getPrivate(), voterEncrypt);

                    String[] votName = readVoter.split(" ");
                    //System.out.println(votName[0]);

                    Signature signatureName = Signature.getInstance("SHA256withRSA");
                    signatureName.initVerify(keyCli);
                    signatureName.update(votName[0].getBytes());




                    Voter nowVoter = this.voters.get(0);
                    boolean euqalVo = false;
                    if (signatureName.verify(signatureSize)) {
                    for (Voter voName : this.voters) {
                        if (voName.readVoterName().equals(readVoter)) {
                            nowVoter = voName;
                            euqalVo = true;
                            break;
                        } else {
                            euqalVo = false;
                        }
                    }
                    } else {
                    System.out.println("Error. Can't verify the digital signature!");
                    return;
                    }
                    if(!euqalVo) {
                        System.out.println("Error. Wrong with information!");
                        serOutput = new ObjectOutputStream(socket.getOutputStream());
                        serInput = new ObjectInputStream(socket.getInputStream());
                        short result=0;
                        serOutput.writeObject(result);
                        return;
                    }
	                while (euqalVo) {


                        serOutput = new ObjectOutputStream(socket.getOutputStream());
                        serInput = new ObjectInputStream(socket.getInputStream());
	                	short result=1;
                        serOutput.writeObject(result);
	                    System.out.println("<--equal to records-->");
                        System.out.println("result"+result);

	                    short option;
	                    
	                    while(true) { serOutput = new ObjectOutputStream(socket.getOutputStream());
                            serInput = new ObjectInputStream(socket.getInputStream());
	                    option =(short)serInput.readObject();
	                    System.out.println("---voter's option is "+option+ "---");
	                    
	                    
	                    if(option==1) {
	                    	
	                    	if(this.util.readHistory(nowVoter)) {
	                    		serOutput = new ObjectOutputStream(socket.getOutputStream());
                                serInput = new ObjectInputStream(socket.getInputStream());
	                    		result=0;
	                    		serOutput.writeObject(result);
	                    	}else {
	                    		serOutput = new ObjectOutputStream(socket.getOutputStream());
                                serInput = new ObjectInputStream(socket.getInputStream());
	                    		result=1;
	                    		serOutput.writeObject(result);
	                    		
	                    		 serOutput = new ObjectOutputStream(socket.getOutputStream());
                                serInput = new ObjectInputStream(socket.getInputStream());
	                          	SealedObject optionEncry = (SealedObject) serInput.readObject();
	                          	String voSelect = (String) this.util.DecryptKey(this.serverKeys.getPrivate(), optionEncry);
	                             System.out.println("---voter's selection is "+voSelect + "---");
	                         	if (voSelect.equals("01")) {
	                         		System.out.println("---voting Tim---");
	                         		String canTi="Tim";
	                         		for(String candiName : this.result.keySet()) {
	                         			if(canTi.equals(candiName)) {
	                         				this.result.put(canTi,this.result.get(canTi)+1);
	                         			}
	                         		}
	                         		this.util.updateResult(this.result);
	                         		this.util.writeHistory(nowVoter);
	                         	}
	                         	if (voSelect.equals("02")) {
	                         		System.out.println("---voting Linda---");
	                         		String canLi="Linda";
	                         		for(String candiName : this.result.keySet()) {
	                         			if(canLi.equals(candiName)) {
	                         				this.result.put(canLi,this.result.get(canLi)+1);
	                         			}
	                         		}
	                         		this.util.updateResult(this.result);
	                         		this.util.writeHistory(nowVoter);
	                         	}
	                    	}
	                    	System.out.println("<--wrote into the history successfully-->");
	                    	
	                    
	                    
	                   
	                    }
	                    
	                    if (option==2) {
	                    	 String voterNum ;
	                         voterNum= nowVoter.readVoterNum();
	                         System.out.println("voter "+voterNum + " select 2");
	                         String readHis = this.util.historyRecords(voterNum);
	                         System.out.println("read history: "+readHis);
	                         serOutput = new ObjectOutputStream(socket.getOutputStream());
	                     	 serInput = new ObjectInputStream(socket.getInputStream());
	                 		 serOutput.writeObject(readHis);
	                    	
	                    }
	                    if (option==3) {
	                    	String canTim="Tim";
	                    	String canLin="Linda";
	                    	Integer timNum,linNum,totalNum;
	                    	timNum=this.result.get(canTim);
	                    	linNum=this.result.get(canLin);
	                    	totalNum=timNum+linNum;
	                    	String winNum;
	                    	if(totalNum==3) {
	                    		if(timNum>linNum) {
	                    			winNum="Tim Win\nTim "+ timNum +"\nLinda " + linNum;
	                    			System.out.println("winner "+winNum);
	                    			serOutput = new ObjectOutputStream(socket.getOutputStream());
	                    			serInput = new ObjectInputStream(socket.getInputStream());
	                    			serOutput.writeObject(winNum);
	                    		}
	                    		if(timNum<linNum) {
	                        		winNum="Linda Win\nTim "+ timNum +"\nLinda " + linNum;
	                                System.out.println("voting result: "+winNum);
	                                serOutput = new ObjectOutputStream(socket.getOutputStream());
	                            	serInput = new ObjectInputStream(socket.getInputStream());
	                        		serOutput.writeObject(winNum);
	                        		}
	                    	}else {
	                    		winNum="0";
	                            System.out.println("voting result: "+winNum);
	                            serOutput = new ObjectOutputStream(socket.getOutputStream());
	                        	serInput = new ObjectInputStream(socket.getInputStream());
	                    		serOutput.writeObject(winNum);
	                    	}
	                    	
	                    }
	                    if(option==4) {
	                    	return;
	                    }
	                    }
	                } 


	                serOutput.close();
	                serInput.close();
	                socket.close();
	            } catch (SocketTimeoutException ex) {
	                System.out.println("---close after 10 minutes---");
	                break;
	            } catch (NullPointerException ex) {
	                ServerUtil.handleException(ex, "Error with null pointer!");
	            } catch (InvalidClassException ex) {
	                ServerUtil.handleException(ex, "Invalid class!");
	            } catch (ClassNotFoundException ex) {
	                ServerUtil.handleException(ex, "Class not found!");
	            } catch (IOException ex) {
	                ServerUtil.handleException(ex, "I/O error!");
	            }catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (InvalidKeyException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SignatureException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    }

	    public void close() {
	        try {
	            this.listen.close();
	        } catch (IOException ex) {
	            ServerUtil.handleException(ex, "I/O error!");
	        }
	    }

		

		

}
