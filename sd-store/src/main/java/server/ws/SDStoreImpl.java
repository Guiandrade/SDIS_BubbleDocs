package server.ws;

import java.util.*;
import javax.jws.*;
import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded; 
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists; 
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair; 
import pt.ulisboa.tecnico.sdis.store.ws.SDStore; 
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.Store;
import server.handler.RelayServerHandler;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.annotation.Resource;


@WebService(
	endpointInterface="pt.ulisboa.tecnico.sdis.store.ws.SDStore",
	wsdlLocation="SD-Store.1_1.wsdl",
	name="SDStore",
	portName="SDStoreImplPort",
	targetNamespace="urn:pt:ulisboa:tecnico:sdis:store:ws",
	serviceName="SDStore"
	)
@HandlerChain(file="/handler-chain.xml")

public class SDStoreImpl implements SDStore{


	private HashMap<String,Repository> _repos = new HashMap<String,Repository>(); // Guarda nome user e respectivo repositorio
    private HashMap<String,Tag> _tagsDocs = new HashMap<String,Tag>(); // Guarda tags de docs
     
    @Resource
    private WebServiceContext webServiceContext;


    public void usersPreDefinidos() throws DocAlreadyExists_Exception, CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception{
        /* PRE-DEFINICOES PARTE 1 DO PROJ
       
        String docId = "readme";
        String content = "User pré definido";
        byte[] contents = content.getBytes();
        List<String> usernames = Arrays.asList("alice","bruno","carla","duarte","eduardo");
        for(String username : usernames){
            DocUserPair doc = new DocUserPair();
            doc.setUserId(username);
            doc.setDocumentId(docId);
            createDoc(doc);
            store(doc,contents);
        }
            */
        
        /*  MUDAR ISTO PQ NAO PODE PASSAR NO CREATE DOC
        createDoc(doc);
        store(doc,"AAAAAAAAAA".getBytes());
        doc.setDocumentId("a2");
        createDoc(doc);
        store(doc,"aaaaaaaaaa".getBytes());
        doc.setUserId("bruno");
        doc.setDocumentId("b1");
        createDoc(doc);
        store(doc,"BBBBBBBBBBBBBBBBBBBB".getBytes()); */
    }


	public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception{
       
        // MENSAGEM VINDA DO HANDLER
        MessageContext messageContext = webServiceContext.getMessageContext();
        String propertyValue = (String) messageContext.get(RelayServerHandler.REQUEST_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", "StoreImpl", propertyValue);

        //////////////////!!!!!!!!!!!!!!!!!!!!!!!!/////////////////////////////////////////////

         
        String username = docUserPair.getUserId();
        String docId = docUserPair.getDocumentId();
        byte[] contents = new byte[0];
        int pid = Integer.parseInt(propertyValue);
        

        if (_repos.containsKey(username)){
            Repository repoUser = _repos.get(username);
            
            if (repoUser.getHashMap().containsKey(docId)){ 
            	DocAlreadyExists fault = new DocAlreadyExists();
            	fault.setDocId(docId);
            	throw new DocAlreadyExists_Exception("Document already exists", fault);
                
            }
            
            else{
                Store newDoc = new Store();
                newDoc.setDocUserPair(docUserPair);
                newDoc.setContents(contents);
                repoUser.addDocument(newDoc);
            }

        }
        
        else{
            Repository repoNewUser = new Repository(username);
            Store newDoc = new Store();
            newDoc.setDocUserPair(docUserPair);
            newDoc.setContents(contents);
            repoNewUser.addDocument(newDoc);
            _repos.put(username,repoNewUser);

        }
        Tag tag = new Tag(1,pid);
        String docInfo = username + docId;
        _tagsDocs.put(docInfo,tag);

        //MENSAGEM PARA O HANDLER
        String newValue = Integer.toString(tag.getClientId());
        System.out.printf("%s put token '%s' on request context%n", "StoreImpl", newValue);
        messageContext.put(RelayServerHandler.RESPONSE_PROPERTY, newValue);

	}


	public List<String> listDocs(String userId) throws UserDoesNotExist_Exception{

        // MENSAGEM VINDA DO HANDLER
        MessageContext messageContext = webServiceContext.getMessageContext();
        String propertyValue = (String) messageContext.get(RelayServerHandler.REQUEST_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", "StoreImpl", propertyValue);

        //////////////////!!!!!!!!!!!!!!!!!!!!!!!!/////////////////////////////////////////////
     
        List<String> listDocuments = new ArrayList<String>();
        
        if (_repos.containsKey(userId)){
            Repository repository = _repos.get(userId);
            listDocuments = repository.listDocs();
        }
        else{
            UserDoesNotExist fault = new UserDoesNotExist();
            fault.setUserId(userId);
            throw new UserDoesNotExist_Exception("User does not exist: ", fault);

        }
        //////////////////!!!!!!!!!!!!!!!!!!!!!!!!/////////////////////////////////////////////
        //MENSAGEM PARA O HANDLER
        String newValue = "LIST RESULT:";
        System.out.printf("%s put token '%s' on request context%n", "StoreImpl", newValue);
        messageContext.put(RelayServerHandler.RESPONSE_PROPERTY, newValue);

        return listDocuments;        

	}

	public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception{ 
        String username = docUserPair.getUserId();
        String docId = docUserPair.getDocumentId();
       

        if (_repos.containsKey(username)){
            
            Repository repoUser = _repos.get(username);
            
            if (repoUser.getHashMap().containsKey(docId)){
                
                Store doc= repoUser.getHashMap().get(docId);
                // MENSAGEM VINDA DO HANDLER
                MessageContext messageContext = webServiceContext.getMessageContext();
                String propertyValue = (String) messageContext.get(RelayServerHandler.REQUEST_PROPERTY);
                System.out.printf("%s got token '%s' from response context%n", "StoreImpl", propertyValue);
                String tag[]= propertyValue.split("-");
                int seqNum = Integer.parseInt(tag[0]);
                int pid = Integer.parseInt(tag[1]);
                Tag tagUpdated = new Tag(seqNum+1,pid);
                String docInfo = username + docId;
                _tagsDocs.put(docInfo,tagUpdated);

                System.out.println("OLHA O VALOR: "+ propertyValue);
                System.out.println("StoreImpl put token "+ propertyValue+" on request context%n");
                messageContext.put(RelayServerHandler.RESPONSE_PROPERTY, propertyValue);

                 
                if (contents==null){ 
                    byte[] a = "".getBytes(); 
                    doc.setContents(a); 
                    repoUser.addDocument(doc);
                }
                
                else if(contents.length < repoUser.getActualCapacity()){
                        doc.setContents(contents);
                        repoUser.addDocument(doc); //Actualiza


                }
               
                else{
                	CapacityExceeded fault = new CapacityExceeded();
            		fault.setCurrentSize(doc.getContents().length);
            		throw new CapacityExceeded_Exception("Capacity Exceeded", fault);
                    
                }
            }
            
            else{
            	DocDoesNotExist fault = new DocDoesNotExist();
        		fault.setDocId(docId);
            	throw new DocDoesNotExist_Exception("Document does not exist", fault);
               
            }

        }
        
        else{
        	UserDoesNotExist fault = new UserDoesNotExist();
            fault.setUserId(username);
            throw new UserDoesNotExist_Exception("User does not exist: ", fault);
            
        }

	}

	public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception{
		String username = docUserPair.getUserId();
        String docId = docUserPair.getDocumentId();
        // MENSAGEM VINDA DO HANDLER
        MessageContext messageContext = webServiceContext.getMessageContext();
        String propertyValue = (String) messageContext.get(RelayServerHandler.REQUEST_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", "StoreImpl", propertyValue);
       

        if (_repos.containsKey(username)){
            
            Repository repoUser = _repos.get(username);
            
            if (repoUser.getHashMap().containsKey(docId)){

                Store doc= repoUser.getHashMap().get(docId);
                byte[] text = doc.getContents();
                
                //MENSAGEM PARA O HANDLER
                String docInfo = username + docId;
                Tag tag =_tagsDocs.get(docInfo);

                //TRATAR APOS RESOLUCAO DE ERRO
                String seqNum = Integer.toString(tag.getSeqNum());
                String pid = Integer.toString(tag.getClientId());
                String newValue =  seqNum + "-" + pid ;
                System.out.println("OLHA O VALOR: "+ newValue);
                System.out.println("StoreImpl put token "+ newValue+" on request context%n");
                messageContext.put(RelayServerHandler.RESPONSE_PROPERTY, newValue);

                return text;   
            }
            
            else{
            	DocDoesNotExist fault = new DocDoesNotExist();
        		fault.setDocId(docId);
            	throw new DocDoesNotExist_Exception("Document does not exist", fault);
                
            }

        }
        
        else{
        	UserDoesNotExist fault = new UserDoesNotExist();
            fault.setUserId(username);
            throw new UserDoesNotExist_Exception("User does not exist: ", fault);
            
        }
    }
}