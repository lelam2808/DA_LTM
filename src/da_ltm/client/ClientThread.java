package da_ltm.client;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
public class ClientThread implements Runnable{
    Socket socket;
    DataInputStream dataIn;
    DataOutputStream dataOut;
    ClientChatForm mainForm;
    StringTokenizer st;
    public ClientThread(Socket socket, ClientChatForm mainForm){
        this.mainForm = mainForm;
        this.socket = socket;
        try {
            dataIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            mainForm.appendMessage("[IOException]: "+ e.getMessage(), "Lỗi");
        }
    }
    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                String data = dataIn.readUTF();
                st = new StringTokenizer(data);
                /** Get Message return **/
                String MSG_RT = st.nextToken();
                switch(MSG_RT){
                    case "MESSAGE":
                        String msg = "";
                        String frm = st.nextToken();
                        while(st.hasMoreTokens()){
                            msg = msg +" "+ st.nextToken();
                        }
                        mainForm.appendMessage(msg+".", frm);
                        break; 
                    case "ONLINE":
                        Vector online = new Vector();
                        while(st.hasMoreTokens()){
                            String list = st.nextToken();
                            if(!list.equalsIgnoreCase(mainForm.username)){
                                online.add(list);
                            }
                        }
                        mainForm.appendOnlineList(online);
                        break;
                    //  hàm này sẽ thông báo đến client rằng có một file nhận, Chấp nhận hoặc từ chối file  
                    case "FILE_XD":  // Format:  FILE_XD [sender] [receiver] [filename]
                        String sender = st.nextToken();
                        String receiver = st.nextToken();
                        String fname = st.nextToken();
                        int confirm = JOptionPane.showConfirmDialog(mainForm, "Từ: "+sender+"\ntên file: "+fname+"\nbạn có Chấp nhận file này không.?");
                        if(confirm == 0){ // client chấp nhận yêu cầu, sau đó thông báo đến sender để gửi file
                            /* chọn chỗ lưu file   */
                            mainForm.openFolder();
                            try {
                                dataOut = new DataOutputStream(socket.getOutputStream());
                                // Format:  SEND_FILE_ACCEPT [ToSender] [Message]
                                String format = "SEND_FILE_ACCEPT "+sender+" Chấp nhận";
                                dataOut.writeUTF(format);
                                
                                /*  hàm này sẽ tạo một socket filesharing  để tạo một luồng xử lý file đi vào và socket này sẽ tự động đóng khi hoàn thành.  */
                                Socket fSoc = new Socket(mainForm.getMyHost(), mainForm.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                fdos.writeUTF("SHARINGSOCKET "+ mainForm.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSoc, mainForm)).start();
                            } catch (IOException e) {
                                System.out.println("[FILE_XD]: "+e.getMessage());
                            }
                        } else { // client từ chối yêu cầu, sau đó gửi kết quả tới sender
                            try {
                                dataOut = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "SEND_FILE_ERROR "+sender+" Người dùng từ chối yêu cầu của bạn hoặc bị mất kết nối.!";
                                dataOut.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[FILE_XD]: "+e.getMessage());
                            }
                        }                       
                        break;   
                    // cái này danh cho việc gửi file đến toàn bộ người dùng hiên có 
                    case "FILE_XD_ALL":  // Format:  FILE_XD_SS [sender1] [receiver1] [filename1]
                        String sender1 = st.nextToken();
                        String receiver1 = st.nextToken();
                        String fname1 = st.nextToken();
//                        int confirm1 = JOptionPane.showConfirmDialog(mainForm, "Từ: "+sender1+ "\nfile: "+fname1+"\nbạn có Chấp nhận file này không.?");
//                            /* chọn chỗ lưu file   */
//                        if(confirm1==0){
//                             mainForm.openFolder();
                            try {
                                dataOut = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ACCEPT [ToSender] [Message]
                                String format = "SEND_FILE_ACCEPT "+sender1+" Chấp nhận";
                                dataOut.writeUTF(format);
                                /*  hàm này sẽ tạo một socket filesharing  để tạo một luồng xử lý file đi vào và socket này sẽ tự động đóng khi hoàn thành.  */
                                Socket fSoc = new Socket(mainForm.getMyHost(), mainForm.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                fdos.writeUTF("SHARINGSOCKET "+ mainForm.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSoc, mainForm)).start();
                            } catch (IOException e) {
                                System.out.println("[FILE_XD_ALL]: "+e.getMessage());
                            }                 
//                        }
//                        else { // client từ chối yêu cầu, sau đó gửi kết quả tới sender
//                            try {
//                                dataOut = new DataOutputStream(socket.getOutputStream());
//                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
//                                String format = "SEND_FILE_ERROR "+sender1+" Người dùng từ chối yêu cầu của bạn hoặc bị mất kết nối.!";
//                                dataOut.writeUTF(format);
//                            } catch (IOException e) {
//                                System.out.println("[FILE_XD]: "+e.getMessage());
//                            }
//                        }      
                           
                        break;     
                    default: 
                        mainForm.appendMessage("[Exception]: Không rõ lệnh "+ MSG_RT, "CMDException");
                    break;
                }
            }
        } catch(IOException e){
            mainForm.appendMessage(" Bị mất kết nối đến Máy chủ, vui lòng thử lại.!", "Lỗi");
        }
    }
}