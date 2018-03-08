


import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class Peer {
	
	/* 
	 *  and register all files in the local file list.
	 */
	public static void Monitor_file(String path, procedure peerfunction){
		File file = new File(path);
		String test[];
		test = file.list();
		if(test.length!=0){
			for(int i = 0; i<test.length; i++){
			Thread_for_register(test[i],peerfunction);
			}
		}
		// Use WrThread to auto update register file
		new WrThread(path,peerfunction);
	}
	
	/*
	 *  Register the file to the index server
	 */ 
	public static void Thread_for_register(String fileName, procedure peerfunction){
		Socket socket = null;
		StringBuffer sb = new StringBuffer("register ");
		try{			
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			// register to the local file
			
			peerfunction.Do_register(Info_Peer.local.ID, fileName);
			
			// register to the server end
			// Send register message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
			
			pw.println(sb.toString());

		}catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	


	public void do_it(procedure peerfunction)throws IOException{
		
		boolean exit = false;
		// Store file name
		String fileName = null;			
		
		BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
		
		// Usage Interface
		while(!exit)
		{
			System.out.println("\n1 Register all file\n2 Search a file\n3 Exit");
			
			switch(Integer.parseInt(localReader.readLine()))
			{
			case 1:
				{	
					 Monitor_file(Info_Peer.local.path,peerfunction);				
						
				break;					
				}
			
			case 2:{					
				boolean find;
				System.out.println("Enter the file name:");
				fileName = localReader.readLine();
				// Search file through index server
				find = searchThread(fileName, peerfunction);
				
				if(find){
					System.out.println("\n1 Download the file\n2 Cancel and back");
					switch(Integer.parseInt(localReader.readLine())){
					case 1:					
						{download(fileName, peerfunction);					
					break;
						}
					default:
						break;			
					}
				 }
				break;
			}
			
			case 3:
			{
				exit = true;
				System.exit(0);
			break;
			}
			default:
				break;
			}
			
		}	
	}
	


	public static void Thread_for_unregister(String fileName, procedure peerfunction){
		Socket socket = null;
		
		System.out.println("in");


		StringBuffer sb = new StringBuffer("unregister ");
		try{			
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			// Unregister to the local file
			
			peerfunction.unDo_register(Info_Peer.local.ID, fileName);
			
			// Unregister to the server end
			// Send unregister message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
			
			pw.println(sb.toString());

		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean searchThread(String fileName, procedure peerfunction){
		
		Info_Peer peerinfo = new Info_Peer();
		peerinfo.initial();
		Socket socket = null;
		StringBuffer sb = new StringBuffer("search ");
		boolean find = false;
		// Store file ID
		String findID = null;
		
		try{			
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			
			// Send search message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
			pw.println(sb.toString());
			
			// Get peer list
			while(!("bye".equals(findID = br.readLine()))){
				Info_Peer.dest.destList.add(findID);							
			}
			
			// If find file in some peers, output their address
			if((find = peerfunction.search(fileName))== true){
				for(int i=0; i<Info_Peer.dest.destPath.size(); i++){
					System.out.println(fileName+" is found on "+Info_Peer.dest.destPath.get(i));
				}
			}

		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		return find;
		
	}
	
	/*
	 *  Used to download file from other clients
	 */
	public void download(String fileName, procedure peerfunction){
		
		String IP = null;
		String folder = null;
		int port = 0;
		int serverPort = 0;
		
		String address = Info_Peer.dest.destPath.get(0);
		
		String[] info = address.split("\\:");
		IP = info[0];
		port = Integer.parseInt(info[1]);
		folder = info[2];
		/*
		 *  Set up serverPort
		 */
		serverPort = Info_Peer.local.downloadPort;
		// Set up a server socket to receive file
		new DThread(serverPort,fileName);
		
		// Set up a socket connection to the peer destination
		Socket socket = null;
		
		StringBuffer sb = new StringBuffer("download ");
		try{			
			peerSocket peersocket = new peerSocket(IP, port);
			socket = peersocket.socket;
			
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
						
			// Send download message
			sb.append(fileName);
			sb.append(" " + serverPort);
			sb.append(" " + Info_Peer.local.IP);
			pw.println(sb.toString());
   
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	
	}
	
	
	
	public static void main(String args[])throws IOException{
	    procedure peerfunction = new procedure();
	    peerfunction.intialize();
	 //   Monitor_file(Info_Peer.local.path,peerfunction);
	    ServerSocket server = null;
	    try{
	    	server = new ServerSocket(Info_Peer.local.serverPort);
	    	System.out.println("\n Peer  started!");
//	    	System.out.println(server);
	    	new PThread(server);
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
		new Peer().do_it(peerfunction);

	}
}

/*
 *   Used to receive file from file client
 *   Step 1. Set up a server socket
 *   Step 2. Waiting for input data 
 */
class DThread extends Thread{
	int port;
	String fileName;
	public DThread(int port,String fileName){
		this.port = port;
		this.fileName = fileName;
		start();
	}
	
	public void run(){
		try {
			ServerSocket server = new ServerSocket(port);
			//while(true){
				Socket socket = server.accept();  
                receiveFile(socket,fileName);  
                socket.close();
                server.close();
			//}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void receiveFile(Socket socket, String fileName) throws IOException{
		byte[] inputByte = null;  
        int length = 0;  
        DataInputStream dis = null;  
        FileOutputStream fos = null;  
        String filePath = "./Look/" + fileName;  
        try {  
            try {  
                dis = new DataInputStream(socket.getInputStream());  
                File f = new File("./Look");  
                if(!f.exists()){  
                    f.mkdir();    
                }  

                fos = new FileOutputStream(new File(filePath));      
                inputByte = new byte[1024];     
                System.out.println("\nStart receiving..."); 
                System.out.println("display file " + fileName);
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
                    fos.write(inputByte, 0, length);  
                    fos.flush();      
                }  
                System.out.println("Finish receive:"+filePath);  
            } finally {  
                if (fos != null)  
                    fos.close();  
                if (dis != null)  
                    dis.close();  
                if (socket != null)  
                    socket.close();   
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}

/*
 *  Watch file
 *  Listening to the local file folder
 *  When there is a change, register or 
 *  unregister file in the local list.
 */
class WrThread extends Thread {
	String path = null;
	procedure peerfunction = null;

	public WrThread(String path,procedure peerfunction){
		this.path = path;
		this.peerfunction = peerfunction;
		
		start();
	}
	
	public void run(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Info_Peer.local.fileList.size()!=0){
					for(int i = 0; i < Info_Peer.local.fileList.size(); i++){
						File file = new File(path + File.separator +
								Info_Peer.local.fileList.get(i));
						if(!file.exists()){
							System.out.println(Info_Peer.local.fileList.get(i)+" was removed!");
							Peer.Thread_for_unregister(Info_Peer.local.fileList.get(i),peerfunction);
							
						}
					}
				}
			}
			
		}, 1000, 100);
		     
	}
}
class peerServer{
	public ServerSocket serversocket;
	public int port;
	
	public peerServer()throws IOException{
		port = Info_Peer.local.serverPort;
		serversocket = new ServerSocket(port);
	}
	
	public peerServer(int port)throws IOException{
		this.port = port;
		serversocket = new ServerSocket(port);
	}
	
	public PrintWriter getWriter(Socket socket)throws IOException{
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
		
	}
	
	public BufferedReader getReader(Socket socket)throws IOException{
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
		
	}
}

class peerSocket{
    
	public Socket socket=null;
	private procedure pf = new procedure();
	
	public peerSocket()throws IOException{
		pf.intialize();
		socket = new Socket(Info_Peer.local.IP,Info_Peer.local.clientPort);
	}
	
	public peerSocket(String IP, int port)throws IOException{
		pf.intialize();
		Info_Peer.local.clientPort = port;
		socket = new Socket(IP,Info_Peer.local.clientPort);
	}
	
	public PrintWriter getWriter(Socket socket)throws IOException{
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
		
	}
	
	public BufferedReader getReader(Socket socket)throws IOException{
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
		
	}
}

