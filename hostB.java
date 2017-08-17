import java.io.*;
import java.net.*;

//this class will run as a separate thread and checks if hostA sent something and prints it
class listener implements Runnable
{

		private Socket clientB;
		private DataInputStream is;
		private DatagramSocket datagramSocket ;
		private exc lock;
		void receiveTCP(String filename)
		{
				lock.locked();
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
				lock.unlock();
		}

		void receiveUDP(String filename)
		{
				lock.locked();
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

				int max_buffer = 1024 ;
				int read = 0;
				int totalRead = 0;
				long remaining = filesize;
				try
				{
						while(remaining > 0)
						{
								byte[] buffer = new byte[max_buffer];
								DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
								//datagramSocket.setSoTimeout(0) ;
								datagramSocket.receive(packet) ;
								remaining -= packet.getLength();
								buffer = packet.getData();
								fos.write(buffer, 0, (int)packet.getLength());
						}
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				lock.unlock();
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

		public void start(Socket tempclientB, exc templock)
		{
				try
				{
						clientB = tempclientB;
						is = new DataInputStream(clientB.getInputStream());
						datagramSocket = new DatagramSocket(9876);
						lock = templock;
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
		private exc lock;
		public void start(Socket tempclientB,exc templock)
		{
				try
				{
						clientB = tempclientB;
						os = new DataOutputStream(clientB.getOutputStream());
						keyRead = new BufferedReader(new InputStreamReader(System.in));
						lock = templock;
				}
				catch (IOException e)
				{
						System.err.println(e);
				}
				new Thread(this,"hostB_sender").start() ;
		}

		void sendTCP(String filename)
		{
				lock.locked();
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
				lock.unlock();
		}

		void sendUDP(String filename)
		{
				lock.locked();
				DatagramSocket datagramSocket = null;
				FileInputStream fis = null;
				InetAddress receiverAddress = null;
				try
				{
						fis = new FileInputStream(filename);
						datagramSocket = new DatagramSocket();
						receiverAddress = InetAddress.getByName("localhost") ;
          //  System.out.println("ra = " + receiverAddress) ;
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
				int max_buffer = 1024 ;
				byte[] buffer = new byte[max_buffer];
				try
				{
						while (fis.read(buffer) > 0)
						{
								DatagramPacket packet = new DatagramPacket(
												buffer, buffer.length, receiverAddress, 9877);
								datagramSocket.send(packet);

						}
						fis.close();
            datagramSocket.close() ;
				}
				catch(IOException e)
				{
						System.err.println(e);
				}
				lock.unlock();
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

class exc
{
    private boolean flag = false;
    public synchronized void locked()
    {
        if(flag)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.err.println(e);
            }
        }
        flag = true;
    }
    public synchronized void unlock()
    {
        flag = false;
        notify();
    }
}

public class hostB
{
		public static void main(String args[])
		{
				ServerSocket Server = null;
				Socket client = null;
				exc lock = new exc();
				try {
						Server = new ServerSocket(9999);
						client = Server.accept();
				}
				catch (IOException e) {
						System.err.println(e);
				}
				listener mylistener = new listener();
				mylistener.start(client,lock);
				sender mysender = new sender();
				mysender.start(client,lock);
		}
}
