import java.io.*;
import java.net.*;

public class hostA {
  public static void main(String[] args) {

        Socket server = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            server = new Socket("127.0.0.1", 9999);
            os = new DataOutputStream(server.getOutputStream());
            is = new DataInputStream(server.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        if (server != null && os != null && is != null) {
            try {
                os.writeBytes("HELO\n");
// keep on reading from/to the socket till we receive the "Ok" from SMTP,
// once we received that then we want to break.
                String responseLine;
                while ((responseLine = is.readLine()) != null) {
                    System.out.println("Server: " + responseLine);
                    if (responseLine.indexOf("Ok") != -1) {
                      break;
                    }
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
