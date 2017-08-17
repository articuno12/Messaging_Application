import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostA sent something and prints it
class listener implements Runnable {

		private Socket clientB;
		private DataInputStream is;
		private BufferedReader keyRead;
		public void run(){
				try {
						String rmessage;
						while(true){
								if((rmessage = is.readUTF()) != null) //receive from hostA
								{
										System.out.println(">>");
										System.out.println(rmessage); // displaying at DOS prompt
										System.out.flush() ;
								}
								if(rmessage == "q") break;
						}
						is.close();
				}
				catch (IOException e) {
						System.out.println(e);
				}
		}

		public void start(Socket tempclientB){
				try{
						clientB = tempclientB;
						is = new DataInputStream(clientB.getInputStream());
				}
				catch (IOException e) {
						System.out.println(e);
				}
				new Thread(this,"hostB_listener").start() ;
		}
}

//this class will run as a thread and sends message to hostA
class sender implements Runnable {

		private DataOutputStream  os = null;
		private Socket clientB ;
		private BufferedReader keyRead = null;

		public void start(Socket tempclientB){
				try{
						clientB = tempclientB;
						os = new DataOutputStream(clientB.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
				}
				catch (IOException e) {
						System.out.println(e);
				}
				new Thread(this,"hostB_sender").start() ;
		}

		public void run(){
				try{
						String smessage;
						while(true)
						{
								smessage = keyRead.readLine();
								if(smessage!=null){


										System.out.println(smessage);  // keyboard reading
										os.writeUTF(smessage);
										os.flush();       // sending to server

								}
								if(smessage =="q") break;
						}
						// clean up:
						os.close();
				}
				catch (IOException e) {
						System.err.println("IOException:  " + e);
				}

		}
}

public class hostB {
		public static void main(String args[]) {
				ServerSocket Server = null;
				Socket client = null;
				try {
						Server = new ServerSocket(9999);
						client = Server.accept();
				}
				catch (IOException e) {
						System.out.println(e);
				}
				listener mylistener = new listener();
        mylistener.start(client);
        sender mysender = new sender();
				mysender.start(client);
		}
}
