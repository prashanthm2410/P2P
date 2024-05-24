import java.io.*;
import java.net.*;

public class PeerBackend {
    private ServerSocket serverSocket;

    public PeerBackend(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void startServer() {
        System.out.println("Server started. Listening for connections...");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new PeerHandler(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PeerHandler implements Runnable {
        private Socket socket;

        public PeerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String fileName = dis.readUTF();
                FileOutputStream fos = new FileOutputStream(fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();

                String message = dis.readUTF();
                System.out.println("Received message: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            PeerBackend peerBackend = new PeerBackend(5000);
            peerBackend.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
