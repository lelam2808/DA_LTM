package da_ltm.client;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClientChatForm extends javax.swing.JFrame {
    String username;
    String host;
    int port;
    Socket socket;
    DataOutputStream dataOut;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "";
//    Creates new form ClientChatFormWindow
    public ClientChatForm() {
        initComponents();
//         ClientChatForm.setLocationRelativeTo(null);
        this.setLocationRelativeTo(null);
    }
        public void initFrame(String username, String host, int port){
        this.username = username;
        this.host = host;
        this.port = port;
        setTitle(username);
        
        try {
            System.out.println("in ra danh sach chat");
            ConnectDB cn= new ConnectDB();
            ArrayList<String> data = cn.indb();
            System.out.println("so tin nhan :"+data.size());
            if(!data.isEmpty()){
                for(int i=0;i<data.size();i++){
                    jTextPane1.setEditable(true);
                    getMsgContentAppend(data.get(i).toString());
                    jTextPane1.setEditable(false);
                }              
            }
        }catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Kết nối 
        connectServer();
    }
    
    public void connectServer(){
        try {
            socket = new Socket(host, port);
            dataOut = new DataOutputStream(socket.getOutputStream());
            // gửi username đang kết nối
            dataOut.writeUTF("JOIN "+ username);
//            appendMessage(" Đã kết nối", "Trạng thái");
            // Khởi động Client Thread 
            new Thread(new ClientThread(socket, this)).start();
            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
            jButton3.setEnabled(true);
            // đã được kết nối
            isConnected = true;
            JOptionPane.showMessageDialog(null, " Bạn đã đăng nhập thành công !!!");
            
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến máy chủ, vui lòng thử lại sau.!","Kết nối thất bại",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: "+ e.getMessage(), "Lỗi");
        }
    }
    
//  Được kết nối
    public boolean isConnected(){
        return this.isConnected;
    }
    
    /*
        Hiển thị Message
    */
    public void appendMessage(String msg, String header){
        jTextPane1.setEditable(true);
        jTextPane1.setCharacterAttributes(MessageColorText.styleMessageColor(Color.BLACK), isConnected);
        getMsgHeader(header);
        getMsgContent(msg);
        jTextPane1.setEditable(false);
    }
    
    /*
        Tin nhắn chat
    */
    public void appendMyMessage(String msg, String header){
        jTextPane1.setEditable(true);
//        jTextPane1.setForeground(Color.red);
        jTextPane1.setCharacterAttributes(MessageColorText.styleMessageColor(Color.BLUE), isConnected);
        getMsgHeader(header);
        getMsgContent(msg);
        jTextPane1.setEditable(false);
    }
    
    /*
        Tiêu đề tin nhắn
    */
    public void getMsgHeader(String header){
        int len = jTextPane1.getDocument().getLength();
        jTextPane1.setCaretPosition(len);
        jTextPane1.replaceSelection(header+":");
    }
    /*
        Nội dung tin nhắn
    */
    public void getMsgContent(String msg){
        int len = jTextPane1.getDocument().getLength();
        
        jTextPane1.setCaretPosition(len);
        jTextPane1.replaceSelection(msg +"\n");
    }
    public void getMsgContentAppend(String msg){
        int len = jTextPane1.getDocument().getLength();
        jTextPane1.setCaretPosition(len);
        jTextPane1.replaceSelection(msg);
    }
    public void appendOnlineList(Vector list){
        sampleOnlineList(list); 
    }

//    danh sách online
    private void sampleOnlineList(Vector list){
        txtpane2.setEditable(true);
        txtpane2.removeAll();
        txtpane2.setText("");
        Iterator i = list.iterator();
        while(i.hasNext()){
            Object e = i.next();
            /*  Hiển thị Username Online   */
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
            JLabel label = new JLabel();
            label.setText(" "+ e);
            panel.add(label);
            int len = txtpane2.getDocument().getLength();
            txtpane2.setCaretPosition(len);
            txtpane2.insertComponent(panel);
            /*  Append Next Line   */
            sampleAppend();
        }
        txtpane2.setEditable(false);
    }
    private void sampleAppend(){
        int len = txtpane2.getDocument().getLength();
        txtpane2.setCaretPosition(len);
        txtpane2.replaceSelection("\n");
    }
//    Set myTitle
    public void setMyTitle(String s){
        setTitle(s);
    }
//    Phương thức tải get download
    public String getDownloadFolder(){
        return this.mydownloadfolder;
    }
//    phương thức get host của socket
    public String getMyHost(){
        return this.host;
    }
//    Phương thức get Port nhận cổng của socket
    public int getMyPort(){
        return this.port;
    }
//    Phương thức nhận My Username
    public String getMyUsername(){
        return this.username;
    }
//    Cập nhật Attachment 
    public void updateAttachment(boolean b){
        this.attachmentOpen = b;
    }
    
    /*
        Hàm này sẽ mở 1 file chooser
    */
    public void openFolder(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = chooser.showDialog(this, "Mở Thư Mục");
        if(open == chooser.APPROVE_OPTION){
            mydownloadfolder = chooser.getSelectedFile().toString()+"\\";
        } else {
            mydownloadfolder = "D:\\";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        button1 = new java.awt.Button();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtpane2 = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setText("jMenuItem1");

        button1.setLabel("button1");

        jMenu1.setText("File");
        jMenuBar2.add(jMenu1);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        setResizable(false);

        jTextPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-paper-plane-15.png"))); // NOI18N
        jButton1.setText("Send");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtpane2.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
        txtpane2.setForeground(new java.awt.Color(120, 14, 3));
        txtpane2.setAutoscrolls(false);
        txtpane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(txtpane2);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/person.png"))); // NOI18N
        jLabel1.setText("Online User");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/chatroom.png"))); // NOI18N
        jLabel2.setText("Chat Room");

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logout.png"))); // NOI18N
        jButton2.setText(" Log Out");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/share.png"))); // NOI18N
        jButton3.setText("Share File");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel3)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(10, 10, 10))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel3))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        try {
            String content = username+" "+ evt.getActionCommand();
            dataOut.writeUTF("CHATALL "+ content);
            appendMyMessage(" "+evt.getActionCommand(), username);
            jTextField1.setText("");
        } catch (IOException e) {
            appendMessage(" Không thể gửi tin nhắn đi bây giờ, không thể kết nối đến Máy Chủ tại thời điểm này, xin vui lòng thử lại sau hoặc khởi động lại ứng dụng này.!", "Lỗi");
        }
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(!jTextField1.getText().toString().isEmpty()){
            try {
                String content = username+" "+ jTextField1.getText();
                dataOut.writeUTF("CHATALL "+ content);
                appendMyMessage(" "+jTextField1.getText()+".", username);
                jTextField1.setText("");
                try {
                    ConnectDB.insert(jTextPane1.getText(), jTextPane1.getText().length());  
                    System.out.println(jTextPane1.getText());
                } catch (ClassNotFoundException ex) {
                   Logger.getLogger(ClientChatForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException e) {
                appendMessage(" Không thể gửi tin nhắn đi bây giờ, vui lòng thử lại sau hoặc khởi động lại ứng dụng này.!", "Lỗi");
            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Không thể gửi tin nhắn, vui lòng nhập tin nhắn trước!","Gửi tin nhắn thất bại",JOptionPane.ERROR_MESSAGE);
        }  
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(!attachmentOpen){
            SendFileForm s = new SendFileForm();
            if(s.prepare(username, host, port, this)){
                s.setLocationRelativeTo(null);
                s.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thiết lập Chia sẻ File tại thời điểm này, xin vui lòng thử lại sau.!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int comfirm =JOptionPane.showConfirmDialog(null, "Bạn chắc chắc muốn đăng xuất ?");
        if(comfirm==0){
            try{
                socket.close();
                setVisible(false);
                // login form
                new LoginForm().setVisible(true);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientChatForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane txtpane2;
    // End of variables declaration//GEN-END:variables
}
