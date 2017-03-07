package client.cli;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import pt.ulisboa.tecnico.sdis.store.ws.*;

public class SDStoreClient {

    public static void main(String[] args) throws Exception {

        String cliNum= args[2];
        System.out.println("Client PID: "+cliNum);
        SDStoreFrontEnd frontEnd = new SDStoreFrontEnd(cliNum);
        frontEnd.setup();

        System.out.println("Remote call ...\n");
        SDStoreMenu menu = new SDStoreMenu(frontEnd);
        while (true){
            menu.display();
        }
    }

}
