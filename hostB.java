import java.io.*;
import java.net.*;
public class hostB {
		public static void main(String args[]) {
				ServerSocket Server = null;
				String line;
				DataInputStream is;
				PrintStream os;
				Socket client = null;
				BufferedReader keyRead = null;
				// BufferedReader receiveRead = null;
				// PrintWriter pwrite = null;
				try {
						Server = new ServerSocket(9999);

				}
				catch (IOException e) {
						System.out.println(e);
				}

				try {
						client = Server.accept();
						is = new DataInputStream(client.getInputStream());
						os = new PrintStream(client.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
						// pwrite = new PrintWriter(os, true);
						// receiveRead = new BufferedReader(new InputStreamReader(is));

						String receiveMessage, sendMessage;
						while(true){
								if((receiveMessage = is.readUTF()) != null) //receive from hostA
								{
										System.out.println("here");
										System.out.println(receiveMessage); // displaying at DOS prompt
								}
								// sendMessage = keyRead.readLine();  // keyboard reading
								// pwrite.println(sendMessage);       // sending to server
								// pwrite.flush();                    // flush the data
								if(receiveMessage == "q") break;
						}
				}
				catch (IOException e) {
						System.out.println(e);
				}
		}
}
