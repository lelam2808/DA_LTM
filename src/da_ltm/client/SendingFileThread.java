package da_ltm.client;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class SendingFileThread implements Runnable {
    protected Socket socket;
    private DataOutputStream dataOut;
    protected SendFileForm form;
    protected String file;
    protected String receiver;
    protected String sender;
   
    
    public SendingFileThread(Socket soc, String file, String receiver, String sender, SendFileForm frm){
        this.socket = soc;
        this.file = file;
        this.receiver = receiver;
        this.sender = sender;
        this.form = frm;
    }

    @Override
    public void run() {
        try {
            form.disableGUI(true);
            System.out.println("Gửi File..!");
            dataOut = new DataOutputStream(socket.getOutputStream());
            /** Write filename, recipient, username  **/
            File filename = new File(file);
            int len = (int) filename.length();
            System.out.println("len: "+len);
            int fileSizeOneSend = (int)Math.ceil(len / 100); // phương thức nhận kích thước file
            System.out.println("fileSize: "+fileSizeOneSend);
            String clean_filename = filename.getName();
            if(receiver.equals("all")){
                dataOut.writeUTF("SENDFILE_ALL "+ clean_filename.replace(" ", "_") +" "+ fileSizeOneSend +" "+ sender);
                System.out.println("Từ: "+ sender);
                System.out.println("Đến Tất Cả ");
            }
            else{
                dataOut.writeUTF("SENDFILE "+ clean_filename.replace(" ", "_") +" "+ fileSizeOneSend +" "+ receiver +" "+ sender);
                System.out.println("Từ: "+ sender);
                System.out.println("Đến: "+ receiver);
            }
           
            /** Create an stream **/
            InputStream input = new FileInputStream(filename);
            OutputStream output = socket.getOutputStream();
            /*  Các tiến trình trên màn hình  */
 
            // Đọc file 
            BufferedInputStream bis = new BufferedInputStream(input);
            /** Tạo một chỗ để chứa file **/
            byte[] buffer = new byte[fileSizeOneSend];
            int count, percent = 0;
            while((count = bis.read(buffer)) > 0){
                percent = percent + count;
                int p = (percent / fileSizeOneSend);
                form.updateProgress(p);
                output.write(buffer, 0, count);
            }
            /* Cập nhật AttachmentForm GUI */
            form.setMyTitle("File đã được gửi đi.!");
            form.updateAttachment(false); //  Cập nhật Attachment 
            JOptionPane.showMessageDialog(form, "File đã gửi thành công.!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            form.closeThis();
            /* Đóng gửi file */
            output.flush();
            output.close();
            System.out.println("File đã được gửi..!");
        } catch (IOException e) {
            form.updateAttachment(false); //  Cập nhật Attachment
            System.out.println("[SendFile]: "+ e.getMessage());
        }
    }
}