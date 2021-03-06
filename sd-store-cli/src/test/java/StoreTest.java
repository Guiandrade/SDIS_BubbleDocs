package src.test;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import client.cli.*;
/**
 *  Test suite
 */
public class StoreTest {

    // static members
    private static SDStore port;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws Exception{
        String uddiURL = "http://localhost:8081";
        String name = "SDStore";

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        SDStore_Service service = new SDStore_Service();
        port = service.getSDStoreImplPort();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        port= null;
    }
    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void storeSemExcecoes() throws DocAlreadyExists_Exception,CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store1");
            doc.setUserId("gui");

            port.createDoc(doc); //criacao do documento
            String s ="Hello !";
            byte[] docBytes = s.getBytes();
            port.store(doc,docBytes); 

    }
    
    
    @Test
    public void storeSemExcecoesMesmoFicheiro2x() throws DocAlreadyExists_Exception, CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store2");
            doc.setUserId("gui");

            port.createDoc(doc); //criacao do documento
            String s ="Hello !";
            byte[] docBytes = s.getBytes();
            port.store(doc,docBytes); 

            String s2= "Aqui estava Hello !";
            byte[] doc2Bytes = s2.getBytes();
            port.store(doc,doc2Bytes); 
    }
    @Test
    public void storeDocsMesmoNomeUsersDiferentes() throws DocAlreadyExists_Exception, CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store2");
            doc.setUserId("andrade");

            port.createDoc(doc); //criacao do documento
            String s ="Hello !";
            byte[] docBytes = s.getBytes();
            port.store(doc,docBytes); 

            DocUserPair doc2 = new DocUserPair();
            doc2.setDocumentId("store2");
            doc2.setUserId("joze");

            port.createDoc(doc2); //criacao do documento
            String s2 ="Hello !";
            byte[] doc2Bytes = s2.getBytes();
            port.store(doc2,doc2Bytes);
    }
    @Test(expected=DocDoesNotExist_Exception.class)
    public void storeComExcecaoDocInexistente() throws DocDoesNotExist_Exception,CapacityExceeded_Exception,UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store3");
            doc.setUserId("andrade");

            String s ="Hello !";
            byte[] docBytes = s.getBytes();

            port.store(doc,docBytes); //Excecao Aqui

    }
    @Test(expected=UserDoesNotExist_Exception.class)
    public void storeComExcecaoUserInexistente() throws DocDoesNotExist_Exception,CapacityExceeded_Exception,UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store4");
            doc.setUserId("xxx");

            String s ="Hello !";
            byte[] docBytes = s.getBytes();

            port.store(doc,docBytes); //Excecao Aqui
    }
    @Test(expected=CapacityExceeded_Exception.class)
    public void storeComExcecaoCapacidadeExcedida() throws  DocAlreadyExists_Exception,DocDoesNotExist_Exception,CapacityExceeded_Exception,UserDoesNotExist_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("store4");
            doc.setUserId("gui");
            port.createDoc(doc); //criacao do documento

            byte[] docBytes = new byte[10240000];

            port.store(doc,docBytes); //Excecao Aqui
    }



}