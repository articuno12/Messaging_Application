import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostB sent something and prints it
class listener implements Runnable
{

		private Socket clientA;
		private DataInputStream is;

		void receiveTCP(String filename)
		{
				long filesize = 0;
				try
				{
						filesize = is.readLong();
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				FileOutputStream fos = null ;
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
								//System.out.println("hereA");
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
				catch(IOException e)
				{
						System.err.println(e);
				}
		}

		void receiveUDP(String filename)
		{
				long filesize = 0;
				DatagramSocket datagramSocket = null;
				try
				{
						filesize = is.readLong();
						//System.out.println("got size = "  +  filesize) ;
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
										System.out.println(">>" + rmessage); // displaying at DOS prompt
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

		public void start(Socket tempclientA)
		{
				try
				{
						clientA = tempclientA;
						is = new DataInputStream(clientA.getInputStream());
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				new Thread(this,"hostA_listener").start() ;
		}
}

//this class will run as a thread and sends message to hostB
class sender implements Runnable
{

		private DataOutputStream os = null;
		private Socket clientA ;
		private BufferedReader keyRead = null;

		public void start(Socket tempclientA)
		{
				try
				{
						clientA = tempclientA ;
						os = new DataOutputStream(clientA.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				new Thread(this,"hostA_sender").start() ;
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

public class hostA
{
		public static void main(String[] args)
		{

				Socket server = null;
				try
				{
						server = new Socket("127.0.0.1", 9999);
				}
				catch (UnknownHostException e)
				{
						System.err.println("Don't know about host: hostname");
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				listener mylistener = new listener();
				mylistener.start(server);
				sender mysender = new sender();
				mysender.start(server);
		}
}
