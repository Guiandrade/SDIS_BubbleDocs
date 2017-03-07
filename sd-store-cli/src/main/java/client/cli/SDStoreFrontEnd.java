package client.cli;

import java.util.*;
import javax.jws.*;
import javax.xml.ws.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import client.ws.handler.RelayClientHandler;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.annotation.Resource;


public class SDStoreFrontEnd {

    private int numServers = 3;
    private int quorum = numServers/2;
    private String uddiURL = "http://localhost:8081";
    private String[] names = { "SDStore", "SDStore-1", "SDStore-2" };
    private SDStore[] ports = { null, null, null};
    private int clientPID = 0;

    @Resource
    private WebServiceContext webServiceContext;     
    
    // Ligar a cada um dos servers
    public SDStoreFrontEnd(String number) {
        int clientId = Integer.parseInt(number);
        this.setClientPID(clientId);
    }
    public void setup() throws Exception {
        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        
        for (int i = 0; i < numServers; i++) {
            String name = names[i];
            System.out.printf("Looking for '%s'%n", name);
            String endpointAddress = uddiNaming.lookup(name);
            
            if (endpointAddress == null) {
                System.out.println("Not found!");
                return;
            } 

            else {
                System.out.printf("Found %s%n", endpointAddress);
            }
            
            System.out.println("Creating stub ...");
            SDStore_Service service = new SDStore_Service();
            SDStore port = service.getSDStoreImplPort();
            System.out.println("Setting endpoint address ...");
            
            BindingProvider bindingProvider = (BindingProvider) port;
            Map < String, Object > requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
            
            ports[i] = port;
        }
    }
    public void setClientPID(int num) {
        clientPID = num;
    }
    public int getClientPID() {
        return clientPID;
    }
    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        String pid = Integer.toString(this.getClientPID());
        
        for (int i = 0; i < numServers; i++) {
            BindingProvider bindingProvider = (BindingProvider) ports[i];
            Map < String, Object > requestContext = bindingProvider.getRequestContext();
            requestContext.put(RelayClientHandler.REQUEST_PROPERTY, pid);
            SDStore port= ports[i]; // port do Servidor onde vamos chamar o metodo
            port.createDoc(docUserPair); 
        }
    }
    
    public List < String > listDocs(String userId) throws UserDoesNotExist_Exception {
        List < String > listDocuments = new ArrayList < String > ();
        List < String > listNew = new ArrayList < String > ();
        
        for (int i = 0; i < numServers; i++) {
            
            BindingProvider bindingProvider = (BindingProvider) ports[i];
            Map < String, Object > requestContext = bindingProvider.getRequestContext();
            requestContext.put(RelayClientHandler.REQUEST_PROPERTY, "LISTDOCS");            
            
            if (i > 0) {
                listNew = ports[i].listDocs(userId);
                
                if (listDocuments.equals(listNew)) {} else {
                    
                    for (String s: listNew){
                        
                        if(!listDocuments.contains(s)){
                            listDocuments.add(s);
                        }
                    }
                }
            }
            
            if (i==0){
                listDocuments = ports[i].listDocs(userId);
            }
                
        }
        return listDocuments;
    }
    
    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        // Como obter tag mais recente, somar 1 ao respectivo seqNum, e fazer Store do content + tagNova????????
        List <String> respostas = new ArrayList<String>();
        int arrayTags[][] = new int[][] {{0,0},{0,0},{0,0}};
        List <byte[]> content = new ArrayList<byte[]>();

        while (content.size()<quorum){    
            for (int i = 0; i < numServers; i++) {
            
                try{
                
                // MENSAGEM PARA O HANDLER
                BindingProvider bindingProvider = (BindingProvider) ports[i];
                Map < String, Object > requestContext = bindingProvider.getRequestContext();
                requestContext.put(RelayClientHandler.REQUEST_PROPERTY, "LOAD");
                byte[] loadRes= ports[i].load(docUserPair);
                content.add(loadRes);
     
                // MENSAGEM VINDA DO HANDLER
                Map < String, Object >  messageContext = bindingProvider.getResponseContext();
                String propertyValue = (String) messageContext.get(RelayClientHandler.RESPONSE_PROPERTY);
                System.out.printf("%s got token '%s' from response context%n", "SDStoreFrontEnd", propertyValue);

                //Tratar tags
                String tag[]= propertyValue.split("-");
                int seqNum = Integer.parseInt(tag[0]);
                int pid = Integer.parseInt(tag[1]);
                arrayTags[i][0] = seqNum;
                arrayTags[i][1] = pid;

                }   catch (Exception e){
                System.out.println("Erro num Servidor.");
                }
            }
        }
        String maxTag = this.getTagMax(arrayTags)+"-"+clientPID;
        
        while (respostas.size()<quorum){

            for (int j = 0; j < numServers; j++) {
                try{
                BindingProvider bindingProvider = (BindingProvider) ports[j];
                Map < String, Object > requestContext = bindingProvider.getRequestContext(); 
                requestContext.put(RelayClientHandler.REQUEST_PROPERTY, maxTag);
                ports[j].store(docUserPair,contents);
                
                // MENSAGEM VINDA DO HANDLER
                Map < String, Object >  messageContext = bindingProvider.getResponseContext();
                String propertyValue = (String) messageContext.get(RelayClientHandler.RESPONSE_PROPERTY);
                System.out.printf("%s got token '%s' from response context%n", "SDStoreFrontEnd", propertyValue);
                respostas.add("ack");    
                }   catch (Exception e){
                System.out.println("Erro num Servidor.");
                }            
            }
    }
    }
    
    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        byte[] resposta = new byte[0];
        int arrayTags[][] = new int[][] {{0,0},{0,0},{0,0}};
        List <byte[]> contents = new ArrayList<byte[]>();
        
        while (contents.size()<quorum){    
            for (int i = 0; i < numServers; i++) {
            
                try{
                
                // MENSAGEM PARA O HANDLER
                BindingProvider bindingProvider = (BindingProvider) ports[i];
                Map < String, Object > requestContext = bindingProvider.getRequestContext();
                requestContext.put(RelayClientHandler.REQUEST_PROPERTY, "LOAD");
                byte[] loadRes= ports[i].load(docUserPair);
                contents.add(loadRes);
     
                // MENSAGEM VINDA DO HANDLER
                Map < String, Object >  messageContext = bindingProvider.getResponseContext();
                String propertyValue = (String) messageContext.get(RelayClientHandler.RESPONSE_PROPERTY);
                System.out.printf("%s got token '%s' from response context%n", "SDStoreFrontEnd", propertyValue);

                //Tratar tags
                String tag[]= propertyValue.split("-");
                int seqNum = Integer.parseInt(tag[0]);
                int pid = Integer.parseInt(tag[1]);
                arrayTags[i][0] = seqNum;
                arrayTags[i][1] = pid;

                }   catch (Exception e){
                System.out.println("Erro num Servidor.");
                }
            }
        }
            //Decidir qual devolver através da tag mais importante
            resposta = this.devolverVersaoActualizada(contents,arrayTags);

            return resposta; 
    }

    public byte[] devolverVersaoActualizada(List<byte[]> contents,int[][] arrayTags){
            byte[] resposta = new byte[0];
            // SE SEQNUM FOR IGUAL NOS 3
            if ((arrayTags[0][0] == arrayTags[1][0]) && (arrayTags[1][0] == arrayTags [2][0])){
                if (arrayTags[0][1] > arrayTags[1][1]){
                    if(arrayTags[0][1] >= arrayTags[2][1]){
                        resposta=contents.get(0);
                    }
                    else{
                        resposta=contents.get(2);
                    }
                }
                else{
                    if(arrayTags[1][1] >= arrayTags[2][1]){
                        resposta=contents.get(1);
                    }
                    else{
                        resposta=contents.get(2);
                    }
                }

            }
            else if((arrayTags[0][0] > arrayTags[1][0]) && (arrayTags[0][0] > arrayTags [2][0])){
                resposta=contents.get(0);
            }
                
            else if((arrayTags[1][0] > arrayTags[0][0]) && (arrayTags[1][0] > arrayTags [2][0])){
                resposta=contents.get(1);
            }

            else if((arrayTags[2][0] > arrayTags[0][0]) && (arrayTags[2][0] > arrayTags [1][0])){
                resposta=contents.get(2);
            }

            else if((arrayTags[0][0]==arrayTags[1][0]) && (arrayTags[0][0] > arrayTags [2][0])){
                if (arrayTags[0][1] >= arrayTags[1][1]){
                    resposta=contents.get(0);
                }
                else{
                        resposta=contents.get(1);
                    }
            }
            
            else if((arrayTags[0][0]==arrayTags[2][0]) && (arrayTags[0][0] > arrayTags [1][0])){
                if (arrayTags[0][1] >= arrayTags[2][1]){
                    resposta=contents.get(0);
                }
                else{
                        resposta=contents.get(2);
                    }                 
            }

            else if((arrayTags[1][0]==arrayTags[2][0]) && (arrayTags[1][0] > arrayTags [0][0])){
                if (arrayTags[1][1] >= arrayTags[2][1]){
                    resposta=contents.get(1);
                }
                else{
                        resposta=contents.get(2);
                    }                 
            }
            return resposta;  
    }
     public String getTagMax(int[][] arrayTags){
            String resposta="";
            
            // SE SEQNUM FOR IGUAL NOS 3
            if ((arrayTags[0][0] == arrayTags[1][0]) && (arrayTags[1][0] == arrayTags [2][0])){
                if (arrayTags[0][1] > arrayTags[1][1]){
                    if(arrayTags[0][1] >= arrayTags[2][1]){
                        resposta=Integer.toString(arrayTags[0][0]);
                    }
                    else{
                        resposta=Integer.toString(arrayTags[2][0]);
                    }
                }
                else{
                    if(arrayTags[1][1] >= arrayTags[2][1]){
                        resposta=Integer.toString(arrayTags[1][0]);
                    }
                    else{
                         resposta=Integer.toString(arrayTags[2][0]);
                    }
                }

            }
            else if((arrayTags[0][0] > arrayTags[1][0]) && (arrayTags[0][0] > arrayTags [2][0])){
                resposta=Integer.toString(arrayTags[0][0]);
            }
                
            else if((arrayTags[1][0] > arrayTags[0][0]) && (arrayTags[1][0] > arrayTags [2][0])){
                resposta=Integer.toString(arrayTags[1][0]);
            }

            else if((arrayTags[2][0] > arrayTags[0][0]) && (arrayTags[2][0] > arrayTags [1][0])){
                resposta=Integer.toString(arrayTags[2][0]);
            }

            else if((arrayTags[0][0]==arrayTags[1][0]) && (arrayTags[0][0] > arrayTags [2][0])){
                if (arrayTags[0][1] >= arrayTags[1][1]){
                    resposta=Integer.toString(arrayTags[0][0]);
                }
                else{
                        resposta=Integer.toString(arrayTags[1][0]);
                    }
            }
            
            else if((arrayTags[0][0]==arrayTags[2][0]) && (arrayTags[0][0] > arrayTags [1][0])){
                if (arrayTags[0][1] >= arrayTags[2][1]){
                    resposta=Integer.toString(arrayTags[0][0]);
                }
                else{
                        resposta=Integer.toString(arrayTags[2][0]);
                    }                 
            }

            else if((arrayTags[1][0]==arrayTags[2][0]) && (arrayTags[1][0] > arrayTags [0][0])){
                if (arrayTags[1][1] >= arrayTags[2][1]){
                     resposta=Integer.toString(arrayTags[2][0]);
                }
                else{
                        resposta=Integer.toString(arrayTags[2][0]);
                    }                 
            }
            return resposta;  
    }
}