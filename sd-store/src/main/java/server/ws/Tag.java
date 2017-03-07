package server.ws;


public class Tag {

	private int _seqNum;
	private int _clientId;

	public Tag(int num, int cliId){
		this.setSeqNum(num);
		this.setClientId(cliId);
	}

	public int getSeqNum(){
		return _seqNum;
	}

	public void setSeqNum(int num){
		_seqNum=num;
	}
	
	public int getClientId(){
		return _clientId;
	}


	public void setClientId(int num){
		_clientId=num;
	}
}



