import java.io.*;
import java.net.*;
public class hostB {
    public static void main(String args[]) {
        ServerSocket Server = null;
        String line;
        DataInputStream is;
        PrintStream os;
        Socket client = null;
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
// As long as we receive data, echo that data back to the client.
           while (true) {
             line = is.readLine();
             os.println(line);
           }
        }
    catch (IOException e) {
           System.out.println(e);
        }
    }
}
