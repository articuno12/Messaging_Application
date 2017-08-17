import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostB sent something and prints it
class listener implements Runnable {

		private Socket clientA;
		private BufferedReader keyRead;
		private DataInputStream is;
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

		public void start(Socket tempclientA){
				try{
						clientA = tempclientA;
						is = new DataInputStream(clientA.getInputStream());
				}
				catch (IOException e) {
						System.out.println(e);
				}
				new Thread(this,"hostA_listener").start() ;
		}
}

//this class will run as a thread and sends message to hostB
class sender implements Runnable {

		private DataOutputStream os = null;
		private Socket clientA ;
		private BufferedReader keyRead = null;

		public void start(Socket tempclientA){
				try{
					  clientA = tempclientA ;
						os = new DataOutputStream(clientA.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
				}
				catch (IOException e) {
						System.out.println(e);
				}
				new Thread(this,"hostA_sender").start() ;
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

public class hostA {
		public static void main(String[] args) {

				Socket server = null;
				try {
						server = new Socket("127.0.0.1", 9999);
				} catch (UnknownHostException e) {
						System.err.println("Don't know about host: hostname");
				} catch (IOException e) {
						System.err.println(e);
				}
				listener mylistener = new listener();
        mylistener.start(server);
        sender mysender = new sender();
				mysender.start(server);
		}
}
