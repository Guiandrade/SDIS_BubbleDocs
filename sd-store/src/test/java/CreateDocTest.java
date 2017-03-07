package src.test;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import server.ws.*;
/**
 *  Test suite
 */
public class CreateDocTest {

    // static members
    SDStoreImpl port = new SDStoreImpl();

    public static void oneTimeSetUp(){
        
    }

    @AfterClass
    public static void oneTimeTearDown() {

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