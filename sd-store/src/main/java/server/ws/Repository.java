package server.ws;

import java.util.*;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.Store;

public class Repository {

	private String _owner;
	private HashMap<String,Store> _docs = new HashMap<String,Store>(); // Guarda nome documento e respectivo doc
	private int _capacity = 10 * 1024;
	
	public Repository(String user){
		this.setOwner(user);
	}

	public void setOwner(String user){
		_owner = user;
	}

	public String getOwner(){
		return _owner;
	}

	public HashMap<String,Store> getHashMap(){
		return _docs;
	}

	public void addDocument(Store doc){
		DocUserPair docUserPair =(DocUserPair) doc.getDocUserPair();
		String docId = docUserPair.getDocumentId();
		this.getHashMap().put(docId,doc);
		if (doc.getContents() != null){
			int size = (this.getActualCapacity() - (doc.getContents().length));
			setActualCapacity(size);
		}
	}

	public void removeDocument(String nameDoc){
		this.getHashMap().remove(nameDoc);
		// ajustar espaço
	}

	public List<String> listDocs() {
			List<String> docsList = new ArrayList<String>();
			for(String nameDoc : this.getHashMap().keySet()){
				docsList.add(nameDoc);
			}
			return docsList;
	}
	
	public int getActualCapacity(){
		return _capacity;
	}

	public void setActualCapacity(int size){
		_capacity = size;
	}


}
