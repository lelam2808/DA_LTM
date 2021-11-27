package da_ltm.client;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

public class ReceivingFileThread implements Runnable {
   
    protected Socket socket;
    protected DataInputStream dataIn;
    protected DataOutputStream dataOut;
    protected ClientChatForm main;
    protected StringTokenizer st;
    private final int BUFFER_SIZE = 100;
    
    public ReceivingFileThread(Socket soc, ClientChatForm m){
        this.socket = soc;
        this.main = m;
        try {
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " +e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                String data = dataIn.readUTF();
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
                switch(CMD){
                    //   hàm này sẽ xử lý việc nhận một file trong một tiến trình 
                    case "SENDFILE":
                        String senderUsername= null;
                            try {
                                String filename = st.nextToken();
                                int filesize = Integer.parseInt(st.nextToken());
                                senderUsername = st.nextToken(); // Get the Sender Username
                                main.setMyTitle("Đang tải File....");
                                System.out.println("Đang tải File....");
                                System.out.println("From: "+ senderUsername);
                                String path = main.getDownloadFolder() + filename;                
//                                 String path ="C:\\Users\\OS\\OneDrive\\Desktop\\" + filename;  
                                /*  Creat Stream   */
                                FileOutputStream fos = new FileOutputStream(path);
                                InputStream input = socket.getInputStream();                                
                                /*  Monitor Progress   */
                                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(main, "Downloading file please wait...", input);
                                /*  Buffer   */
                                BufferedInputStream bis = new BufferedInputStream(pmis);
                                /**  Create a temporary file **/
                                byte[] buffer = new byte[filesize];
                                int count, percent = 0;
                                while((count = bis.read(buffer)) != -1){
                                    percent = percent + count;
                                    int p = (percent / filesize);
                                    main.setMyTitle("Downloading...  "+ p +"%");
                                    fos.write(buffer, 0, count);
                                }
                                fos.flush();
                                fos.close();
                                main.setMyTitle(main.getMyUsername());
                                JOptionPane.showMessageDialog(null, "File đã được tải đến \n'"+ path +"'");
                                System.out.println("File đã được lưu: "+ path);
                            } catch (IOException e) {
                                /*
                                Gửi lại thông báo lỗi đến sender
                                Định dạng: SENDFILERESPONSE [username] [Message]
                                */
                                DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                                eDos.writeUTF("SENDFILERESPONSE "+ senderUsername + " Kết nối bị mất, vui lòng thử lại lần nữa.!");
                                System.out.println(e.getMessage());
                                main.setMyTitle(main.getMyUsername());
                                JOptionPane.showMessageDialog(main, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                                socket.close();
                            }
                        break;
                    case "SENDFILE_1":
                        String sender_Username= null;
                        while(st.hasMoreTokens()){
                            System.out.println("hàm nhận file all được chạy");
                            try {
                                String filename = st.nextToken();
                                int filesize = Integer.parseInt(st.nextToken());
                                sender_Username = st.nextToken(); // Get the Sender Username
//                                String cmd_text = st.nextToken();
                                main.setMyTitle("Đang tải File....");
                                
                                System.out.println("Đang tải File....");
                                System.out.println("From: "+ sender_Username);
//                                String path = main.getDownloadFolder() + filename;     
                                 String path ="C:\\Users\\OS\\OneDrive\\Desktop\\" + filename;  
                                /*  Creat Stream   */
                                FileOutputStream fos = new FileOutputStream(path);
                                InputStream input = socket.getInputStream();                                
                                /*  Monitor Progress   */
                                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(main, "Downloading file please waiting...", input);
                                /*  Buffer   */
                                BufferedInputStream bis = new BufferedInputStream(pmis);
                                /**  Create a temporary file **/
                                byte[] buffer = new byte[filesize];
                                int count, percent = 0;
                                while((count = bis.read(buffer)) != -1){
                                    percent = percent + count;
                                    int p = (percent / filesize);
//                                    if(p<=100){
////                                        main.setMyTitle("Downloading...  "+ p +"%");
//                                    }
                                    fos.write(buffer, 0, count);
                                }
//                                fos.flush();
//                                fos.close();
                                main.setMyTitle(main.getMyUsername());
                                JOptionPane.showMessageDialog(null, "File đã được tải đến \n'"+ path +"'");
                                System.out.println("File đã được lưu: "+ path);
                            } catch (IOException e) {
                                /*
                                Gửi lại thông báo lỗi đến sender
                                */
                                DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                                eDos.writeUTF("SENDFILERESPONSE "+ sender_Username + " Kết nối bị mất, vui lòng thử lại lần nữa.!");
                                System.out.println(e.getMessage());
                                main.setMyTitle(main.getMyUsername());
                                JOptionPane.showMessageDialog(main, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                                socket.close();
                            }
                        }   
                        System.out.println("hàm nhận file all kết thúc");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " +e.getMessage());
        }
    }
}

