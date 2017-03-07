package src.test;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import client.cli.*;
/**
 *  Test suite
 */
public class CreateDocTest {

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
    public void criarSemExcecoes() throws DocAlreadyExists_Exception{
            DocUserPair doc = new DocUserPair();
            doc.setDocumentId("teste");
            doc.setUserId("gui");

            port.createDoc(doc); //criacao do documento 

    }
    
    @Test(expected=DocAlreadyExists_Exception.class)
    public void criarComExcecao() throws DocAlreadyExists_Exception{
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("badjora");
        doc.setUserId("gui");

        port.createDoc(doc);

        DocUserPair docIgual = new DocUserPair();
        docIgual.setDocumentId("badjora");
        docIgual.setUserId("gui");
        port.createDoc(docIgual); // DocAlreadyExists_Exception here
    }

    @Test
    public void criarDoisDocsMesmoUser() throws DocAlreadyExists_Exception{
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("aaa");
        doc.setUserId("gui");

        port.createDoc(doc); //criacao do documento 

        DocUserPair doc2 = new DocUserPair();
        doc2.setDocumentId("bbb");
        doc2.setUserId("gui");

        port.createDoc(doc2); //criacao do segundo documento 
    }

    @Test
    public void criarDoisDocsUsersDiferentes() throws DocAlreadyExists_Exception{
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("aaa");
        doc.setUserId("andrade");

        port.createDoc(doc); //criacao do documento 

        DocUserPair doc2 = new DocUserPair();
        doc2.setDocumentId("ccc");
        doc2.setUserId("gui");

        port.createDoc(doc2); //criacao do segundo documento 
    }

}