

package da_ltm.server;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {
    
    ServerSocket server;
    ServerForm main;
    boolean keepActive = true;
    int i=1;
    public ServerThread(int port, ServerForm main){
        main.setForeground(Color.black);
        try {
            this.main = main;
            server = new ServerSocket(port);
            main.appendMessage("Máy Chủ đã khởi động !");
        } 
        catch (IOException e) { main.appendMessage("[IOException]: "+ e.getMessage()); }
    }

    @Override
    public void run() {
        try {
            while(keepActive){
                Socket socket = server.accept();
                new Thread(new SocketThread(socket, main)).start();
                System.out.println("SocketThread: "+i);
                i++;
            }
        } catch (IOException e) {
            main.appendMessage( e.getMessage());
        }
    }
    
    
    public void stop(){
        try {
            server.close();
            keepActive = false;
            System.out.println("Máy Chủ Tạm dừng !");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
