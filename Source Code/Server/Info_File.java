

public class Info_File {
	
	private String peerID;
	private String fileName;
	
	public Info_File(){
		
	}
	
	public Info_File(String peerID,String fileName){
		this.peerID = peerID;
		this.fileName = fileName;
	}
	
	public String getID(){
		return peerID;
	}
	
	public String getName(){
		return fileName;
	}
	
	public void setID(String peerID){
		this.peerID = peerID;
	}
	
	public void setName(String fileName){
		this.fileName = fileName;
	}

}
