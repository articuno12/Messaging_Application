import java.io.*;
import java.net.*;

public class hostA {
		public static void main(String[] args) {

				Socket server = null;
				DataOutputStream os = null;
				DataInputStream is = null;
				BufferedReader keyRead = null;
				// BufferedReader receiveRead = null;
				// PrintWriter pwrite = null;
				try {
						server = new Socket("127.0.0.1", 9999);
						os = new DataOutputStream(server.getOutputStream());
						is = new DataInputStream(server.getInputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
						// pwrite = new PrintWriter(os, true);
						// receiveRead = new BufferedReader(new InputStreamReader(is));
				} catch (UnknownHostException e) {
						System.err.println("Don't know about host: hostname");
				} catch (IOException e) {
						System.err.println("Couldn't get I/O for the connection to: hostname");
				}
				if (server != null && os != null && is != null) {
						try {
								os.writeUTF("HELO\n");
								// keep on reading from/to the socket till we receive the "Ok" from SMTP,
								// once we received that then we want to break.
								String responseLine;
								// while ((responseLine = is.readLine()) != null) {
								//     System.out.println("Server: " + responseLine);
								//     if (responseLine.indexOf("Ok") != -1) {
								//       break;
								//     }
								// }
								String receiveMessage, sendMessage;
								while(true)
								{
										sendMessage = keyRead.readLine();
										System.out.println(sendMessage);  // keyboard reading
										os.writeUTF(sendMessage);
										os.flush();       // sending to server
										//pwrite.flush();                    // flush the data
										// if((receiveMessage = receiveRead.readLine()) != null) //receive from server
										// {
										// 		System.out.println(receiveMessage); // displaying at DOS prompt
										// }
										if(sendMessage =="q") break;
								}
								// clean up:
								os.close();
								is.close();
								server.close();
						} catch (UnknownHostException e) {
								System.err.println("Trying to connect to unknown host: " + e);
						} catch (IOException e) {
								System.err.println("IOException:  " + e);
						}
				}
		}
}
