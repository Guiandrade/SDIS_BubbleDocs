package src.test;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import client.cli.*;
/**
 *  Test suite
 */
public class LoadTest {

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
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("teste4");
        doc.setUserId("tiago");
        port.createDoc(doc);
        String content = "Hello World";
        byte[] docBytes = content.getBytes();
        port.store(doc,docBytes);

        DocUserPair doc1 = new DocUserPair();
        doc1.setDocumentId("teste5");
        doc1.setUserId("tiago");
        port.createDoc(doc1);
        String content1 = "Hello World";
        byte[] docBytes1 = content1.getBytes();
        port.store(doc1,docBytes1);

        DocUserPair doc2 = new DocUserPair();
        doc2.setDocumentId("teste5");
        doc2.setUserId("gui");
        port.createDoc(doc2);
        String content2 = "Hello World";
        byte[] docBytes2 = content2.getBytes();
        port.store(doc2,docBytes2);
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
    public void testeLoadUmFicheiro_1() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document1 = new DocUserPair();
        document1.setDocumentId("teste4");
        document1.setUserId("tiago");
        port.load(document1);
    }

    @Test
    public void testeLoadUmFicheiro_2() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document2 = new DocUserPair();
        document2.setDocumentId("teste5");  // testa documento diferente do mesmo utilizador
        document2.setUserId("tiago");
        port.load(document2);
    }

    @Test
    public void testeLoadUmFicheiro_3() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document3 = new DocUserPair();
        document3.setDocumentId("teste5");
        document3.setUserId("gui");        // testa documento com o mesmo nome de utilizadores diferentes
        port.load(document3);
    }

    @Test
    public void testeLoadDoisFicheiros() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document4 = new DocUserPair();
        DocUserPair document5 = new DocUserPair();   // testa dois loads seguidos feitos por dois utilizadores diferentes
        document4.setDocumentId("teste5");
        document4.setUserId("gui");
        document5.setDocumentId("teste4");
        document5.setUserId("tiago");
        port.load(document4);
        port.load(document5);
    }

    @Test
    public void testeLoadFicheiroVazio() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception, DocAlreadyExists_Exception{
        DocUserPair document3 = new DocUserPair();
        document3.setDocumentId("teste6");
        document3.setUserId("gui");
        port.createDoc(document3);        
        port.load(document3);
    }
    
    @Test(expected=UserDoesNotExist_Exception.class)
    public void testeLoadUtilizadorErrado() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document3 = new DocUserPair();
        document3.setDocumentId("teste5");
        document3.setUserId("victor");  // testa user que não existe
        port.load(document3);
    }

    @Test(expected=DocDoesNotExist_Exception.class)
    public void testeLoadNomeFicheiroErrado() throws UserDoesNotExist_Exception, DocDoesNotExist_Exception{
        DocUserPair document4 = new DocUserPair();
        document4.setDocumentId("teste6");  // testa nome de documento que não existe
        document4.setUserId("tiago");
        port.load(document4);
    }
}