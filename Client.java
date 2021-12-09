import java.net.*;
import java.awt.*;

import javax.swing.*;
import java.awt.event.*;

import java.io.*;

public class Client extends JFrame {

    private static final int PORT = 8888;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int id;
    private int cnt = 0;
    // private Boolean flag = false;

    JTextArea textArea;
    JScrollPane scroller;
    JPanel south;
    JTextField textField;
    JButton button;

    // GUI
    public Client() {
        setTitle("Client");
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        // 显示
        textArea = new JTextArea();
        scroller = new JScrollPane(textArea);
        textArea.setFocusable(false);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(BorderLayout.CENTER, scroller);
        // 输入
        south = new JPanel();
        textField = new JTextField(50);
        button = new JButton("send");
        south.add(textField);
        south.add(button);
        getContentPane().add(BorderLayout.SOUTH, south);

        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        Client clientFrame = new Client();
        clientFrame.connect();

        ClientHandler reader = clientFrame.new ClientHandler();
        Thread handler = new Thread(reader);
        handler.start();

    }

    public void connect() throws IOException {
        try {
            socket = new Socket("localhost", PORT);
            System.out.println(socket);
            // flag = true;
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            // Enable auto-flush:
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())),
                    true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Fail to connect server!");
            if (socket != null)
                socket.close();
        }

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (message != null && !message.equals("")) {

                    try {
                        out.println("Client " + id + ":" + message);
                        out.flush();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Message cannot be empty!");
                }
                textField.setText("");
            }
        };
        textField.addActionListener(listener);
        button.addActionListener(listener);
    }

    public class ClientHandler implements Runnable {

        String message;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                while ((message = in.readLine()) != null) {
                    if (cnt == 0) {
                        String[] str = message.split(" ");
                        id = Integer.parseInt(str[1]);
                        cnt++;
                    }
                    textArea.append(message + "\n");
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
}