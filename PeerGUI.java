import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class PeerGUI extends JFrame {
    private JTextField hostField;
    private JTextField portField;
    private JTextField messageField;
    private JButton attachButton;
    private JButton sendButton;
    private File attachedFile;

    public PeerGUI() {
        setTitle("P2P File Transfer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        JLabel hostLabel = new JLabel("Peer Host:");
        hostField = new JTextField();
        add(hostLabel);
        add(hostField);

        JLabel portLabel = new JLabel("Peer Port:");
        portField = new JTextField();
        add(portLabel);
        add(portField);

        JLabel messageLabel = new JLabel("Message:");
        messageField = new JTextField();
        add(messageLabel);
        add(messageField);

        attachButton = new JButton("Attach File");
        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attachFile();
            }
        });
        add(attachButton);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        add(sendButton);

        setVisible(true);
    }

    private void attachFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            attachedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "File attached: " + attachedFile.getName());
        }
    }

    private void sendFile() {
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());
        String message = messageField.getText();

        if (attachedFile == null) {
            JOptionPane.showMessageDialog(this, "Please attach a file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Socket socket = new Socket(host, port)) {
            sendFile(socket, attachedFile);
            sendMessage(socket, message);
            JOptionPane.showMessageDialog(this, "File and message sent successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to send file and message.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sendFile(Socket socket, File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        dos.writeUTF(file.getName());
        while ((bytesRead = fis.read(buffer)) > 0) {
            dos.write(buffer, 0, bytesRead);
        }
        fis.close();
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PeerGUI();
            }
        });
    }
}
