import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 8888;
    private static LinkedList<PrintWriter> lstWriters = new LinkedList<>();
    public static int count = 0;

    void go() throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server started");
        Server.count = 0;
        try {
            while (true) {
                // Blocks until a connection occurs:
                Socket socket = s.accept();
                Server.count = Server.count + 1;
                System.out.println("Client " + Server.count + " has connected");
                PrintWriter writer = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream())),
                        true);
                writer.println("Client " + Server.count + " has connected");
                writer.flush();// 传id给客户端
                lstWriters.add(writer);
                ServerHandler reader = this.new ServerHandler(socket);
                Thread handler = new Thread(reader);
                handler.start();
            }
        } finally {
            s.close();
        }
    }

    public static void main(String[] args)
            throws IOException {

        Server server = new Server();
        server.go();

    }

    public class ServerHandler implements Runnable {
        // 监听客户端并转发
        String message;
        BufferedReader in;
        Socket socket;

        public ServerHandler(Socket s) {
            this.socket = s;
            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                while ((message = in.readLine()) != null) {
                    for (PrintWriter writer : lstWriters) {
                        writer.println(message);
                        writer.flush();
                    }
                    System.out.println("[Server] " + message);
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
