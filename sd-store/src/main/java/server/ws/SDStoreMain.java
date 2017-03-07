package server.ws;

import javax.xml.ws.Endpoint;

import server.ws.UDDINaming;


public class SDStoreMain {

    public static void main(String[] args) {
        // Check arguments
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL wsName wsURL%n", SDStoreMain.class.getName());
            return;
        }
        String uddiURL = args[0];
        String name = args[1];
        String url = args[2];
        String porto = args[3];
        String numServer = args[4];
        
        int porta=Integer.parseInt(porto);
        int numServidor=Integer.parseInt(numServer);

           
            Endpoint endpoint = null;
            UDDINaming uddiNaming = null;
            try {
               
                    SDStoreImpl port = new SDStoreImpl();
                    port.usersPreDefinidos();
                    endpoint = Endpoint.create(port);
                    
                    // Numbering url and name to differentiate the 3 servers
                    if(numServidor!=0){
                            name= name +("-"+numServidor+"");
                            url="http://localhost:"+porta+"/store-ws/endpoint";

                        }
                    
                   
                    System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);

                    // publish endpoint
                    System.out.printf("Starting (url) %s%n", url);
                    endpoint.publish(url);

                    uddiNaming = new UDDINaming(uddiURL);
                    uddiNaming.rebind(name, url);

        
                // wait
                System.out.println("Awaiting connections");
                System.out.println("Press enter to shutdown");
                System.in.read();
                

        } catch(Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();

        } finally {
            try {
                if (endpoint != null) {
                        //stop endpoint
                        endpoint.stop();
                        System.out.printf("Stopped %s%n", url);
                    
                }
            } catch(Exception e) {
                System.out.printf("Caught exception when stopping: %s%n", e);
            }
            try {
                if (uddiNaming != null) {
                        // delete from UDDI
                        uddiNaming.unbind(name);
                        System.out.printf("Deleted '%s' from UDDI%n", name);
                }
            } catch(Exception e) {
                System.out.printf("Caught exception when deleting: %s%n", e);
            }
        }
    }
}
