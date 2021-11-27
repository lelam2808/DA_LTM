package da_ltm.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class SendFileForm extends javax.swing.JFrame {

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private String myusername;
    private String host;
    private int port;
    private StringTokenizer st;
    private String sendTo;
    private String file;
    private ClientChatForm main;
    private Object chooser;
    // Tạo một form SendFileFormWindow
    public SendFileForm() {
        initComponents();
        progressbar.setVisible(false);
        this.setLocationRelativeTo(null);
    }
    
    
    /*
    Phương thức này được gọi đến khi người dùng click vào menu “Gửi File”, 
    sau đó kết nối đến Server và bắt đầu sẵn sàng để gửi file
    */
    public boolean prepare(String u, String h, int p, ClientChatForm m){
        this.host = h;
        this.myusername = u;
        this.port = p;
        this.main = m;
        /*  Kết nối đến Server  */
        try {
            socket = new Socket(host, port);
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());
            //  Format: SHARINGSOCKET [sender]
            String format = "SHARINGSOCKET "+ myusername;
            dataOut.writeUTF(format);
            System.out.println(format);
            
            /*  Khởi động SendFileFormWindow Thread    */
            new Thread(new SendFileThread(this)).start();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    /*Phương thức SendFileThread này sẽ gửi yêu cầu chuyển dữ liệu đến Server   */
    class SendFileThread implements Runnable{
        private SendFileForm form;
        public SendFileThread(SendFileForm form){
            this.form = form;
        }
        
        private void closeMe(){
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("[closeMe]: "+e.getMessage());
            }
            dispose();
        }
        
        @Override
        public void run() {
            try {
                while(!Thread.currentThread().isInterrupted()){
                    String data = dataIn.readUTF();  // Đọc nội dung của dữ liệu được nhận từ Server
                    st = new StringTokenizer(data);
                    String cmd = st.nextToken();  //  Lấy chữ đầu tiên từ dữ liệu
                    switch(cmd){
                        case "RECEIVE_FILE_ERROR":  // Định dạng: CMD_RECEIVE_FILE_ERROR [Message]
                            String msg = "";
                            while(st.hasMoreTokens()){
                                msg = msg+" "+st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFileForm.this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            this.closeMe();
                            break;
                            
                        case "RECEIVE_FILE_ACCEPT":  // Định dạng:RECEIVE_FILE_ACCEPT [Message]
                            /*  Bắt đầu khởi động thread File đính kèm   */

                            new Thread(new SendingFileThread(socket, file, sendTo, myusername, SendFileForm.this)).start();
                            break;
                            
                        case "SENDFILEERROR":
                            String emsg = "";
                            while(st.hasMoreTokens()){
                                emsg = emsg +" "+ st.nextToken();
                            }                                                     
                            System.out.println(emsg);                            
                            JOptionPane.showMessageDialog(SendFileForm.this, emsg,"Lỗi", JOptionPane.ERROR_MESSAGE);
                            form.updateAttachment(false);
                            form.disableGUI(false);
                            form.updateBtn("Gửi File");
                            break;
                        
                        
                        case "SENDFILERESPONSE":
                            /*
                            Format: SENDFILERESPONSE [username] [Message]
                            */
                            String rReceiver = st.nextToken();
                            String rMsg = "";
                            while(st.hasMoreTokens()){
                                rMsg = rMsg+" "+st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFileForm.this, rMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            dispose();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        button1 = new java.awt.Button();
        txtFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSendTo = new javax.swing.JTextField();
        progressbar = new javax.swing.JProgressBar();
        btnSendFile = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        button1.setLabel("button1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gửi File ");
        setResizable(false);

        txtFile.setEditable(false);
        txtFile.setBackground(new java.awt.Color(255, 255, 255));
        txtFile.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFileActionPerformed(evt);
            }
        });

        btnBrowse.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-attach-20.png"))); // NOI18N
        btnBrowse.setText("Chọn File");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        jLabel2.setText("( Nhập all để ");

        txtSendTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSendToActionPerformed(evt);
            }
        });

        progressbar.setBackground(new java.awt.Color(51, 255, 51));
        progressbar.setForeground(new java.awt.Color(255, 255, 255));
        progressbar.setStringPainted(true);

        btnSendFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-send-file-20.png"))); // NOI18N
        btnSendFile.setText("Gửi");
        btnSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendFileActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Gửi File");

        jLabel4.setText("Gửi cho tất cả )");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(171, Short.MAX_VALUE)
                .addComponent(btnSendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(160, 160, 160))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtFile, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                                    .addComponent(txtSendTo))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBrowse)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4))))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel4))
                    .addComponent(txtSendTo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnSendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFileActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        showOpenDialog();
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendFileActionPerformed
        // TODO add your handling code here:
        sendTo = txtSendTo.getText();
        file = txtFile.getText();

        if((sendTo.length() > 0) && (file.length() > 0) && !sendTo.equals("all")){
            try {
                // Format: SEND_FILE_XD [sender] [receiver] [filename]
                txtFile.setText("");
                String fname = getThisFilename(file);
                String format = "SEND_FILE_XD "+myusername+" "+sendTo+" "+fname;
                dataOut.writeUTF(format);
                System.out.println(format);
                updateBtn("Sending...");
                btnSendFile.setEnabled(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else if(sendTo.equals("all") && file.length() > 0){
            try {
                // Format: SEND_FILE_TO_ALL [sender] [filename]
                String fname = getThisFilename(file);
                System.out.println("gui tu: "+myusername+" den tat ca ");
                String format = "SEND_FILE_TO_ALL "+myusername+" "+fname;
                dataOut.writeUTF(format);
                System.out.println(format);
                updateBtn("Sending...");
                btnSendFile.setEnabled(false);
               
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Không để trống.!","Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSendFileActionPerformed

    private void txtSendToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSendToActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSendToActionPerformed
    public void showOpenDialog(){
        JFileChooser chooser = new JFileChooser();
        int intval = chooser.showOpenDialog(this);
        if(intval == chooser.APPROVE_OPTION){
            txtFile.setText(chooser.getSelectedFile().toString());
        }else{
            txtFile.setText("");
        }
    }
    
    
    /*   Phương thức này sẽ disable/enabled tất cả GUI   */
    public void disableGUI(boolean d){
        if(d){ // Disable
            txtSendTo.setEditable(false);
            btnBrowse.setEnabled(false);
            btnSendFile.setEnabled(false);
            txtFile.setEditable(false);
            progressbar.setVisible(true);
        } else { // Enable
            txtSendTo.setEditable(true);
            btnSendFile.setEnabled(true);
            btnBrowse.setEnabled(true);
            txtFile.setEditable(true);
            progressbar.setVisible(false);
        }
    }
    
    
    /*  Nhận Form Title   */
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    /*   Hàm này sẽ đóng Form  */
    protected void closeThis(){
        dispose();
    }
    
    /*  Hàm này sẽ nhận Filename */
    public String getThisFilename(String path){
        File p = new File(path);
        String fname = p.getName();
        return fname.replace(" ", "_");
    }
    
    /*  Cập nhật file đính kèm   */
    public void updateAttachment(boolean b){
        main.updateAttachment(b);
    }
    
    /*  Cập nhật nút btnSendFile text  */
    public void updateBtn(String str){
        btnSendFile.setText(str);
    }
    
    /**
     * Update progress bar
     * @param val 
     */
    public void updateProgress(int val){
        progressbar.setValue(val);
    }
    
    
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
            java.util.logging.Logger.getLogger(SendFileForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SendFileForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SendFileForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SendFileForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SendFileForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnSendFile;
    private java.awt.Button button1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JProgressBar progressbar;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextField txtSendTo;
    // End of variables declaration//GEN-END:variables
}
