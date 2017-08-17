import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostB sent something and prints it
class listener implements Runnable
{

		private Socket clientA;
		private BufferedReader keyRead;
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
						System.out.println(e);
				}
				FileOutputStream fos = null ;
				try
				{
						fos = new FileOutputStream(filename);
				}
				catch (IOException e)
				{
						System.out.println(e);
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
								System.out.println("read " + totalRead + " bytes.");
								fos.write(buffer, 0, (int)read);
						}
				}
				catch (IOException e)
				{
						System.out.println(e);
				}
				try
				{
						fos.close();
				}
				catch(IOException e)
				{
						System.out.println(e);
				}
		}

		void receiveUDP(String filename)
		{

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
										System.out.println(">>" + rmessage); // displaying at DOS prompt
										System.out.flush() ;
										String [] aStr = rmessage.split(" ");
										if(aStr[0]=="Sending")
										{
												if(aStr[2] !=null && aStr[1]!=null)
												{
														if(aStr[2]=="UDP") receiveUDP(aStr[1]);
														else if(aStr[2]=="TCP") receiveTCP(aStr[1]);
												}
										}
								}
								if(rmessage == "q") break;
						}
						is.close();
				}
				catch (IOException e)
				{
						System.out.println(e);
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
						System.out.println(e);
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
						System.out.println(e);
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
						System.out.println(e);
				}
				long filesize = 0;
				try
				{
						filesize = fis.getChannel().size();
						os.writeLong(filesize);
				}
				catch(IOException e)
				{
						System.out.println(e);
				}
				byte[] buffer = new byte[2048];
				try
				{
						while (fis.read(buffer) > 0)
						{
								os.write(buffer);
						}
						fis.close();
				}
				catch(IOException e)
				{
						System.out.println(e);
				}
		}

		void sendUDP(String filename)
		{

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
										if(aStr[0]=="Sending")
										{
												if(aStr[2] !=null && aStr[1]!=null)
												{
														if(aStr[2]=="UDP") sendUDP(aStr[1]);
														else if(aStr[2]=="TCP") sendTCP(aStr[1]);
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
