package client.cli;

import java.util.Scanner;
import java.util.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;

// provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import javax.crypto.*;

public class SDStoreMenu {

	private Scanner input = new Scanner(System.in);
	private SDStoreFrontEnd _port;
	private Map<String, byte[]> users = new LinkedHashMap<String, byte[]>();
	
	public SDStoreMenu(SDStoreFrontEnd port){
		this.setPort(port);
	}
	
	public void setPort(SDStoreFrontEnd port){
		_port = port;
	}

	public SDStoreFrontEnd getPort(){
		return _port;
	}


	public void display() throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException{
		System.out.println("------------SDStore------------");
		System.out.println(
			"Select an option: \n" +
			"	1) Create Document\n" +
			"	2) List User Documents\n" +
			"	3) Write On Document\n" +
			"	4) Read Document\n" +
			"	0) Exit SDStore\n"+
			"-------------------------------"
		);
		while (input.hasNextInt()==false){
			System.out.println("Error: Write a number from 1 to 4");
			input.nextLine();
		}
		
		int selection = input.nextInt();
		input.nextLine();


		switch (selection) {
		case 0:
			this.exit();
		
		case 1:
			
			DocUserPair doc = this.createDocument();
			String password_1 = this.getPassword();
			try {
				byte[] password_1_bytes = password_1.getBytes();
				users.put(doc.getUserId(),password_1_bytes);
				this.getPort().createDoc(doc);
  			} catch(DocAlreadyExists_Exception e){
  				System.out.println(e.getMessage()); // FAZER NAS RESTANTES	
  			}
  			break;
		
		case 2:
            String userId = this.getUsername();
            String password_2 = this.getPassword();
        	try {
            	//if a simular requestAuthentication no SDId
	            byte[] password_2_byte = users.get(userId);
	  			String password_2_str = new String(password_2_byte);
	  			if(password_2.equals(password_2_str)){
                	System.out.println(this.getPort().listDocs(userId));
                }
                else{
            		System.out.println("Password errada");
            	}
            } catch(UserDoesNotExist_Exception e){
                System.out.println(e.getMessage());
            }
        	break;
		
		case 3:
  			
  			DocUserPair docUP = this.createDocument();

			String userId2 = docUP.getUserId();
            String password_3 = this.getPassword();
            //if a simular requestAuthentication no SDId
            byte[] password_3_byte = users.get(userId2);
  			String password_3_str = new String(password_3_byte);
  			if(password_3.equals(password_3_str)){
	  			try{
	  				String content = this.getContent();
					byte[] docBytes = content.getBytes();
					String key_1 = password_3 + userId2 + "677245";

					byte[] bytePass = key_1.getBytes();
			        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			        byte[] encodedKey = messagedigest.digest(bytePass);
			        SecretKeySpec storeKey = new SecretKeySpec(encodedKey,0, encodedKey.length , "AES");
			        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, storeKey);
		        	byte[] cipherBytes = cipher.doFinal(docBytes);

				    ////////////////////////// POSSIVEL SOLUÇÃO COM MACS

		        	// este seria o esboço do código do cliente:

		        	// String key_mac_send = password_3;
					// byte[] bytePass2 = key_mac_send.getBytes();
			        // MessageDigest messagedigest2 = MessageDigest.getInstance("MD5");
			        // byte[] encodedKey2 = messagedigest2.digest(bytePass2);
			        // SecretKeySpec macKey = new SecretKeySpec(encodedKey2,0, encodedKey2.length , "AES"); // gerar key a partir da pass do user
		        	// byte[] macTag = makeMAC(cipherBytes, macKey); 

		        	// esta macTag seria enviada ao servidor



		        	// este seria o esboço do código no servidor:

		        	// String key_mac_recieved = users.get(userId); // usar a pass guardada no map users associada ao user para gerar a key igual ao cliente
					// byte[] bytePass3 = key_mac_recieve.getBytes();
			        // MessageDigest messagedigest3 = MessageDigest.getInstance("MD5");
			        // byte[] encodedKey3 = messagedigest3.digest(bytePass3);
			        // SecretKeySpec macKey = new SecretKeySpec(encodedKey3,0, encodedKey3.length , "AES");
		        	// Key macKey = users.get(UserId()); 
	        		// boolean result = verifyMAC(macTag, textoBytes, macKey);

	        		// depois disto seria feita a verificação se o conteudo teria sido alterado ou não

		        	///////////////////////////////////////


	  				this.getPort().store(docUP,cipherBytes);
	  			} catch(DocDoesNotExist_Exception e){
	  				System.out.println(e.getMessage());
	  				
	  			} catch(CapacityExceeded_Exception e1){
	  				System.out.println(e1.getMessage());
	  				
	  			} catch(UserDoesNotExist_Exception e2){	
	  				System.out.println(e2.getMessage());
	  			}
	  			break;}
	  		else{
	  			System.out.println("Password errada");
            	break;
	  		}

		case 4:
  			
  			DocUserPair docLoad = this.createDocument();

  			String userId3 = docLoad.getUserId();
            String password_4 = this.getPassword();
            //if a simular requestAuthentication no SDId
            byte[] password_4_byte = users.get(userId3);
  			String password_4_str = new String(password_4_byte);
  			if(password_4.equals(password_4_str)){
				try{
					String key_2 = password_4 + userId3 + "677245";
					byte[] textoBytes = this.getPort().load(docLoad);

					byte[] bytePass1 = key_2.getBytes();
			        MessageDigest messagedigest1 = MessageDigest.getInstance("MD5");
			        byte[] encodedKey1 = messagedigest1.digest(bytePass1);
			        SecretKeySpec loadKey = new SecretKeySpec(encodedKey1,0, encodedKey1.length , "AES");
			        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	  				cipher.init(Cipher.DECRYPT_MODE, loadKey);
	        		byte[] newTextoBytes = cipher.doFinal(textoBytes);
	        		
	  				String texto = new String(newTextoBytes);
	  				System.out.println("Document's content : \n" + texto);

	  			} catch(UserDoesNotExist_Exception e){
	  				System.out.println(e.getMessage());
	  				
	  			} catch(DocDoesNotExist_Exception e1){
	  				System.out.println(e1.getMessage());
	  			}
				break;}
			else{
				System.out.println("Password errada");
			}
		
		default:
  			System.out.println("Invalid selection!");
  			break;
		}
	}

	private void exit(){
		System.out.println("Exiting..");
		System.exit(1);
	}

	private String getUsername(){
		System.out.println("Write your username: ");
		while((input.hasNext()==false)){
		}
		String username = input.nextLine().toString();
		return username;
	}

	private String getDocName(){
		System.out.println("Write your document's name: ");
		while((input.hasNext()==false)){
		
		}
		String docName = input.nextLine().toString();
		return docName;
	}

	private String getPassword(){
		System.out.println("Write your password: ");
		while((input.hasNext()==false)){
		
		}
		String password = input.nextLine().toString();
		return password;
	}

	private String getContent(){
		System.out.println("Write your document's new content: ");
		while((input.hasNext()==false)){
		
		}
		String content = input.nextLine().toString();
		return content;
	}

	private DocUserPair createDocument(){
		String username = this.getUsername();
		String docName = this.getDocName();

		DocUserPair docUserPair = new DocUserPair();
	  	docUserPair.setDocumentId(docName);
	  	docUserPair.setUserId(username);

	  	return docUserPair;
  	}
	

	}
