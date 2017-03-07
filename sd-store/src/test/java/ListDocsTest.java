package src.test;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import server.ws.*;
/**
 *  Test suite
 */
public class ListDocsTest {

    // static members
    private static SDStoreImpl port = new SDStoreImpl();
    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception{
    
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("teste");
        doc.setUserId("tiago");
        port.createDoc(doc);
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // port= null;
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
    public void testeListaComUmFicheiro() throws UserDoesNotExist_Exception{
        port.listDocs("tiago");
    }

    @Test
    public void testeListaComDoisFicheiro() throws UserDoesNotExist_Exception,DocAlreadyExists_Exception{
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("teste1");
        doc.setUserId("tiago");
        port.createDoc(doc);

        port.listDocs("tiago");
    }
    @Test
    public void testeListarDoisUsers() throws UserDoesNotExist_Exception,DocAlreadyExists_Exception{
        DocUserPair doc = new DocUserPair();
        doc.setDocumentId("teste37");
        doc.setUserId("tiago");
        port.createDoc(doc);
        port.listDocs("tiago");


        DocUserPair doc2 = new DocUserPair();
        doc2.setDocumentId("teste1");
        doc2.setUserId("alberto");
        port.createDoc(doc2);
        port.listDocs("alberto");

    }
    
    @Test(expected=UserDoesNotExist_Exception.class)
    public void testExceptionWithAnnotation() throws UserDoesNotExist_Exception{
        port.listDocs("victor");
    }
}