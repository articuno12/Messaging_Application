import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostA sent something and prints it
class listener implements Runnable
{

		private Socket clientB;
		private DataInputStream is;

		void receiveTCP(String filename)
		{
				//	System.out.println("yoyo") ;
				long filesize = 0;
				try
				{
						filesize = is.readLong();
						//System.out.println("got size = "  +  filesize) ;
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				FileOutputStream fos = null;
				try
				{
						fos = new FileOutputStream(filename);
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				byte[] buffer = new byte[2048];
				long read = 0;
				long totalRead = 0;
				long remaining = filesize;
				try
				{
						while((read = is.read(buffer, 0, (int)Math.min(buffer.length, remaining))) > 0)
						{
								totalRead += read;
								remaining -= read;
								//System.out.println("read " + totalRead + " bytes.");
								fos.write(buffer, 0, (int)read);
						}
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				try
				{
						fos.close();
				}
				catch(Exception e)
				{
						System.err.println(e);
				}
				//System.out.println("finished writing") ;
		}

		void receiveUDP(String filename)
		{
				long filesize = 0;
				DatagramSocket datagramSocket = null;
				try
				{
						filesize = is.readLong();
						System.out.println("got size = "  +  filesize) ;
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				FileOutputStream fos = null ;
				try
				{
						fos = new FileOutputStream(filename);
						datagramSocket = new DatagramSocket();
				}
				catch (IOException e)
				{
						System.err.println(e);
				}

				int max_buffer = 65508/2;
				byte[] buffer = new byte[max_buffer];
				int read = 0;
				int totalRead = 0;
				long remaining = filesize;
				try
				{
						while(remaining > 0)
						{
								DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
								datagramSocket.receive(packet);
								remaining-=packet.getLength();
								byte[] buffer2 = packet.getData();
								fos.write(buffer2, 0, (int)packet.getLength());
								System.out.println("here");
						}
				}
				catch (IOException e)
				{
						System.err.println(e);
				}

		}

		public void run()
		{
				try
				{
						String rmessage;
						while(true)
						{
								if((rmessage = is.readUTF()) != null) //receive from hostA
								{
										if(rmessage.isEmpty()) continue ;
										System.out.println(">> " + rmessage); // displaying at DOS prompt
										System.out.flush() ;
										String [] aStr = rmessage.split(" ");
										if(aStr[0].indexOf("Sending")!=-1)
										{
												if(aStr[2] !=null && aStr[1]!=null)
												{
														if(aStr[2].indexOf("UDP")!=-1) receiveUDP(aStr[1]);
														else if(aStr[2].indexOf("TCP")!=-1) receiveTCP(aStr[1]);

												}
										}
								}
								if(rmessage == "q") break;
						}
						is.close();
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
		}

		public void start(Socket tempclientB)
		{
				try
				{
						clientB = tempclientB;
						is = new DataInputStream(clientB.getInputStream());
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				new Thread(this,"hostB_listener").start() ;
		}
}

//this class will run as a thread and sends message to hostA
class sender implements Runnable
{

		private DataOutputStream  os = null;
		private Socket clientB ;
		private BufferedReader keyRead = null;

		public void start(Socket tempclientB)
		{
				try
				{
						clientB = tempclientB;
						os = new DataOutputStream(clientB.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				new Thread(this,"hostB_sender").start() ;
		}

		void sendTCP(String filename)
		{
				FileInputStream fis = null;
				try
				{
						fis = new FileInputStream(filename);
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				long filesize = 0;
				try
				{
						filesize = fis.getChannel().size();
						os.writeLong(filesize);
						os.flush();
				}
				catch(IOException e)
				{
						System.err.println(e);
				}
				byte[] buffer = new byte[2048];
				try
				{
						while (fis.read(buffer) > 0)
						{
								os.write(buffer);
								os.flush();
						}
						fis.close();
				}
				catch(IOException e)
				{
						System.err.println(e);
				}
		}

		void sendUDP(String filename)
		{
				DatagramSocket datagramSocket = null;
				FileInputStream fis = null;
				InetAddress receiverAddress = null;
				try
				{
						fis = new FileInputStream(filename);
						datagramSocket = new DatagramSocket();
						receiverAddress = InetAddress.getLocalHost();
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				long filesize = 0;
				try
				{
						filesize = fis.getChannel().size();
						os.writeLong(filesize);
						os.flush();
				}
				catch(IOException e)
				{
						System.err.println(e);
				}
				int max_buffer = 65508/2;
				byte[] buffer = new byte[max_buffer];
				try
				{
						while (fis.read(buffer) > 0)
						{
								// os.write(buffer);
								// os.flush();
								DatagramPacket packet = new DatagramPacket(
												buffer, buffer.length, receiverAddress, 9999);
								datagramSocket.send(packet);
						}
						fis.close();
				}
				catch(IOException e)
				{
						System.err.println(e);
				}

		}

		public void run()
		{
				try
				{
						String smessage;
						while(true)
						{
								smessage = keyRead.readLine();
								if(smessage!=null)
								{
										os.writeUTF(smessage);
										os.flush();       // sending to server
										String [] aStr = smessage.split(" ");
										if(aStr[0].indexOf("Sending")!=-1)
										{
												if(aStr[2] !=null && aStr[1]!=null)
												{
														if(aStr[2].indexOf("UDP")!=-1) sendUDP(aStr[1]);
														else if(aStr[2].indexOf("TCP")!=-1) sendTCP(aStr[1]);
												}
										}
								}
								if(smessage =="q") break;
						}
						// clean up:
						os.close();
				}
				catch (IOException e)
				{
						System.err.println("IOException:  " + e);
				}

		}
}

public class hostB
{
		public static void main(String args[])
		{
				ServerSocket Server = null;
				Socket client = null;
				try {
						Server = new ServerSocket(9999);
						client = Server.accept();
				}
				catch (IOException e) {
						System.err.println(e);
				}
				listener mylistener = new listener();
				mylistener.start(client);
				sender mysender = new sender();
				mysender.start(client);
		}
}
