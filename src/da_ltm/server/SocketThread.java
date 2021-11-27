
package da_ltm.server;

import da_ltm.client.ClientChatForm;
import da_ltm.client.ConnectDB;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketThread implements Runnable {
    Socket socket;
    ServerForm main;
    DataInputStream dataIn;
    StringTokenizer st;
    String client, filesharing_username;
    private final int BUFFER_SIZE = 100;
    
    public SocketThread(Socket socket, ServerForm main) {
        this.main = main;
        this.socket = socket;
        try {
            dataIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            main.appendMessage("[SocketThreadIOException]: " + e.getMessage());
        }
    }
    /*   Hàm này sẽ lấy client socket trong danh sách client socket sau đó sẽ thiết lập kết nối    */
    private void createConnection(String receiver, String sender, String filename) {
        try {
            main.appendMessage("[createConnection]: đang tạo kết nối chia sẻ file.");
            Socket s = main.getClientList(receiver);
            if (s != null) { // Client đã tồn tại
                main.appendMessage("[createConnection]: Socket OK");
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                main.appendMessage("[createConnection]: DataOutputStream OK");
                // Format:  FILE_XD [sender] [receiver] [filename]
                // thay bang send file xd de mo dialog
                String format = "FILE_XD " + sender + " " + receiver + " " + filename;
                dosS.writeUTF(format);
                main.appendMessage("[createConnection]: " + format);
            } else {// Client không tồn tại, gửi lại cho sender rằng receiver không tìm thấy.
                main.appendMessage("[createConnection]: Client không được tìm thấy '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("SENDFILEERROR " + "Client '" + receiver + "' không được tìm thấy trong danh sách, bảo đảm rằng user đang online.!");
            }
        } catch (IOException e) {
            main.appendMessage("[createConnection]: " + e.getLocalizedMessage());
        }
    }
    private void createConnectionAll(String receiver, String sender, String filename) {
        try {
            main.appendMessage("[createConnection]: đang tạo kết nối chia sẻ file.");
            Socket s = main.getClientList(receiver);
            if (s != null) { // Client đã tồn tại
                main.appendMessage("[createConnection]: Socket OK");
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                main.appendMessage("[createConnection]: DataOutputStream OK");
                // Format:  FILE_XD [sender] [receiver] [filename]
                // thay bang send file xd de mo dialog
                String format = "FILE_XD_ALL " + sender + " " + receiver + " " + filename;
                dosS.writeUTF(format);
                main.appendMessage("[createConnection]: " + format);
            } else {// Client không tồn tại, gửi lại cho sender rằng receiver không tìm thấy.
                main.appendMessage("[createConnection]: Client không được tìm thấy '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("SENDFILEERROR " + "Client '" + receiver + "' không được tìm thấy trong danh sách, bảo đảm rằng user đang online.!");
            }
        } catch (IOException e) {
            main.appendMessage("[createConnection]: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                /**
                 * Phương thức nhận dữ liệu từ Client *
                 */
                String data = dataIn.readUTF();
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
             
                switch (CMD) {
                    case "JOIN":
                        /**
                         * JOIN [clientUsername] *
                         */
                        String clientUsername = st.nextToken();
                        client = clientUsername;
                        main.setClientList(clientUsername);
                        main.setSocketList(socket);
                        main.appendMessage(clientUsername + " tham gia Chatroom.!");
                        break;
                    case "CHATALL":
                        /**
                         * CHATALL [from] [message] *
                         */
                        String chatall_from = st.nextToken();
                        String chatall_msg = "";
                        while (st.hasMoreTokens()) {
                            chatall_msg = chatall_msg + " " + st.nextToken();
                        }
                        
//                        String chatall_content = chatall_msg;
                        String chatall_content = chatall_from + " " + chatall_msg;
//                        try {
//                            ConnectDB.insert(chatall_content);   
//                        } catch (ClassNotFoundException ex) {
//                            Logger.getLogger(ClientChatForm.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        for (int x = 0; x < main.clientList.size(); x++) {
                            if (!main.clientList.elementAt(x).equals(chatall_from)) {
                                try {
                                    Socket tsoc2 = (Socket) main.socketList.elementAt(x);
                                    DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
                                    dos2.writeUTF("MESSAGE " + chatall_content);
                                } catch (IOException e) {
                                    main.appendMessage("[CHATALL]: " + e.getMessage());
                                }
                            }
                        }
                        main.appendMessage("[CHATALL]: " + chatall_content);
                        break;

                    case "SHARINGSOCKET":
                        main.appendMessage("SHARINGSOCKET : Client thiết lập một socket cho kết nối chia sẻ file...");
                        String file_sharing_username = st.nextToken();
                        filesharing_username = file_sharing_username;
                        main.setClientFileSharingUsername(file_sharing_username);
                        main.setClientFileSharingSocket(socket);
                        main.appendMessage("SHARINGSOCKET : Username: " + file_sharing_username);
                        main.appendMessage("SHARINGSOCKET : Chia Sẻ File đang được mở");
                        break;

                    case "SENDFILE":
                        main.appendMessage("SENDFILE : Client đang gửi một file...");
                        /*
                         Format: SENDFILE [Filename] [Size] [Recipient] [Consignee]  from: Sender Format
                         Format: SENDFILE [Filename] [Size] [Consignee] to Receiver Format
                         */
                        String file_name = st.nextToken();
                        String filesize = st.nextToken();
                        String sendto = st.nextToken();
                        String consignee = st.nextToken();
                        main.appendMessage("SENDFILE : Từ: " + consignee);
                        main.appendMessage("SENDFILE : Đến: " + sendto);
                        /**
                         * Nhận client Socket *
                         */
                        main.appendMessage("SENDFILE : sẵn sàng cho các kết nối..");
                        Socket cSock = main.getClientFileSharingSocket(sendto); /* Consignee Socket  */
                        /*   Now Check if the consignee socket was exists.   */

                        if (cSock != null) { /* Exists   */

                            try {
                                main.appendMessage("SENDFILE : Đã được kết nối..!");
                                /**
                                 * Đầu tiên là viết filename..  *
                                 */
                                main.appendMessage("SENDFILE : đang gửi file đến client...");
                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("SENDFILE " + file_name + " " + filesize + " " + consignee);
                                /**
                                 * Thứ hai là đọc nội dung file   *
                                 */
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[Integer.parseInt(filesize)];
                                int cnt;
                                while ((cnt = input.read(buffer)) > 0) {
                                    sendFile.write(buffer, 0, cnt);
                                }
                                sendFile.flush();
                                sendFile.close();
                                /**
                                 * Xóa danh sách client *
                                 */
                                main.removeClientFileSharing(sendto);
                                main.removeClientFileSharing(consignee);
                                main.appendMessage("SENDFILE : File đã được gửi đến client...");
                            } catch (IOException e) {
                                main.appendMessage("[SENDFILE]: " + e.getMessage());
                            }
                        } else { /*   Không tồn tại, return error  */
                            /*   FORMAT: SENDFILEERROR  */

                            main.removeClientFileSharing(consignee);
                            main.appendMessage("SENDFILE : Client '" + sendto + "' không tìm thấy.!");
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("SENDFILEERROR " + "Client '" + sendto + "' không tìm thấy, Chia Sẻ File sẽ thoát.");
                        }
                        break;
                    case "SENDFILE_ALL":
                        main.appendMessage("SENDFILE_1 : Client đang gửi một file...");
                        /*Format: CMD_SENDFILE [Filename] [Size] [sender]  from: Sender Format*/
                        String fileName = st.nextToken();
                        String fileSize = st.nextToken();
                        String sender = st.nextToken();
                        for(int i=0;i<main.clientList.size();i++){
                            if(!sender.equals(main.clientList.get(i))){
                                main.appendMessage("SENDFILE_1 : Từ: " + sender);
                                main.appendMessage("SENDFILE_1 : Đến: " + main.clientList.get(i));
                                main.appendMessage("SENDFILE_1 : sẵn sàng cho các kết nối..");
                                Socket cSock1 = main.getClientFileSharingSocket(main.clientList.get(i).toString());
                                if (cSock1 != null) { /* Exists   */
                                    try {
                                        main.appendMessage("SENDFILE_1 : Đã được kết nối..!");
                                        /**
                                         * Đầu tiên là viết filename..  *
                                         */
                                        main.appendMessage("SENDFILE_1 : đang gửi file đến client...");
                                        DataOutputStream cDos = new DataOutputStream(cSock1.getOutputStream());
                                        cDos.writeUTF("SENDFILE_1 " + fileName + " " + fileSize + " " + main.clientList.get(i)+" ");
                                        /**
                                         * Thứ hai là đọc nội dung file   *
                                         */
                                        InputStream input = socket.getInputStream();
                                        OutputStream sendFile = cSock1.getOutputStream();
                                        byte[] buffer = new byte[Integer.parseInt(fileSize)];
                                        int cnt;
                                        while ((cnt = input.read(buffer)) > 0) {
                                            sendFile.write(buffer, 0, cnt);
                                        }
                                        sendFile.flush();
                                        sendFile.close();
//                                         main.removeClientFileSharing(sender);
                                        main.removeClientFileSharing(main.clientList.get(i).toString());
                                        main.appendMessage("SENDFILE_1 : File đã được gửi đến client...");
                                    } catch (IOException e) {
//                                        main.appendMessage("[SENDFILE_1 loi sendfile all]: " + e.getMessage());
                                    }
                                } else { /*   Không tồn tại, return error  */
                                    /*   FORMAT: CMD_SENDFILEERROR  */
                                    main.removeClientFileSharing(main.clientList.get(i).toString());
                                    main.appendMessage("SENDFILE_1 : Client '" + sender + "' không tìm thấy.!");
                                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                    dos.writeUTF("SENDFILEERROR " + "Client '" + sender + "' không tìm thấy, Chia Sẻ File sẽ thoát.");
                                }
                            }
                        }
                        main.removeClientFileSharing(sender);
                        break;
                    case "SEND_FILE_TO_ALL":  // Format: SEND_FILE_TO_ALL [sender] [receiver]    
                            try {
                                String send_sender = st.nextToken();
                                String send_filename = st.nextToken();
                  
                                for( int i=0;i<main.clientList.size();i++){
                                    if(!main.clientList.get(i).equals(send_sender)){
                                        System.out.println("danh sách client hiện có: "+main.clientList.get(i));
                                        main.appendMessage("[SEND_FILE_TO_ALL]: Host: " + send_sender);
                                        this.createConnectionAll(main.clientList.get(i).toString(), send_sender, send_filename);
                                    }
                                }
                            } catch (Exception e) {
                                main.appendMessage("[SEND_FILE_TO_ALL]: " + e.getLocalizedMessage());
                            }
                        break;
                    case "SENDFILERESPONSE":
                        /*
                         Format: SENDFILERESPONSE [username] [Message]
                         */
                        String receiver = st.nextToken(); // phương thức nhận receiver username
                        String rMsg = ""; // phương thức nhận error message
                        main.appendMessage("[SENDFILERESPONSE]: username: " + receiver);
                        while (st.hasMoreTokens()) {
                            rMsg = rMsg + " " + st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) main.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("SENDFILERESPONSE" + " " + receiver + " " + rMsg);
                        } catch (IOException e) {
                            main.appendMessage("[SENDFILERESPONSE]: " + e.getMessage());
                        }
                        break;

                    case "SEND_FILE_XD":  // Format: SEND_FILE_XD [sender] [receiver]                        
                        try {
                            String send_sender = st.nextToken();
                            String send_receiver = st.nextToken();
                            String send_filename = st.nextToken();
                            main.appendMessage("[SEND_FILE_XD]: Host: " + send_sender);
                            this.createConnection(send_receiver, send_sender, send_filename);
                        } catch (Exception e) {
                            main.appendMessage("[SEND_FILE_XD]: " + e.getLocalizedMessage());
                        }
                        break;

                    case "SEND_FILE_ERROR":  // Format:  CMD_SEND_FILE_ERROR [receiver] [Message]
                        String eReceiver = st.nextToken();
                        String eMsg = "";
                        while (st.hasMoreTokens()) {
                            eMsg = eMsg + " " + st.nextToken();
                        }
                        try {
                            /*  Gửi Error đến File Sharing host  */
                            Socket eSock = main.getClientFileSharingSocket(eReceiver); // phương thức nhận file sharing host socket cho kết nối
                            DataOutputStream eDos = new DataOutputStream(eSock.getOutputStream());
                            //  Format:  RECEIVE_FILE_ERROR [Message]
                            eDos.writeUTF("RECEIVE_FILE_ERROR " + eMsg);
                        } catch (IOException e) {
                            main.appendMessage("[RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;

                    case "SEND_FILE_ACCEPT": // Format:  SEND_FILE_ACCEPT [receiver] [Message]
                        String aReceiver = st.nextToken();
                        String aMsg = "";
                        while (st.hasMoreTokens()) {
                            aMsg = aMsg + " " + st.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = main.getClientFileSharingSocket(aReceiver); // get the file sharing host socket for connection
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDos.writeUTF("RECEIVE_FILE_ACCEPT " + aMsg);
                        } catch (IOException e) {
                            main.appendMessage("[RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;

                    default:
                        main.appendMessage("[CMDException]: Không rõ lệnh " + CMD);
                        break;
                }
            }
        } catch (IOException e) {
            /*   đây là hàm chatting client, remove nếu như nó tồn tại..   */
            System.out.println(client);
            System.out.println("File Sharing: " + filesharing_username);
            main.removeFromTheList(client);
            if (filesharing_username != null) {
                main.removeClientFileSharing(filesharing_username);
            }
            main.appendMessage("[SocketThread]: Kết nối client bị đóng..!");
        }
    }

}
