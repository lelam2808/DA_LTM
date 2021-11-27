/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package da_ltm.client;

package do_an_ver1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ConnectDB {
	public static void insert(String message, int lenght) throws ClassNotFoundException {	
		Connection connection=null;
		try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				String connectionURL="jdbc:sqlserver://AnhLam:1433;databaseName=MessageDB;integratedSecurity=true;";
				connection=DriverManager.getConnection(connectionURL);
				System.out.println("ket noi ok roi!!!!!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("loi ket noi!!!");
			}
		try {
			
			String sql="INSERT INTO SaveMessageDB(id, message) VALUES('"+ lenght +"','"+ message +"')";
			Statement statement;
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			System.out.println("chen thanh cong");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("chen that bai " +e.getMessage());
		}
		
		
	}
	public static ArrayList<String> indb() throws ClassNotFoundException {
		Connection connection=null;
		ArrayList<String> row = new ArrayList<>();

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionURL="jdbc:sqlserver://AnhLam:1433;databaseName=MessageDB;integratedSecurity=true;";
			connection=DriverManager.getConnection(connectionURL);
			System.out.println("ket noi ok roi!!!!!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("loi ket noi!!!");
		}
		try {
		String sql="SELECT TOP 1 message FROM SaveMessageDB ORDER BY id Desc";
		Statement statement=connection.createStatement();
		ResultSet result=statement.executeQuery(sql);
		while(result.next()) {
			row.add(result.getString("message"));
		}
               
		System.out.println("lich su chat: "+row);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("in ra that bai " +e.getMessage());
		}
                return row;
	}
        public static void deleteDB() throws ClassNotFoundException {
		Connection connection=null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionURL="jdbc:sqlserver://AnhLam:1433;databaseName=MessageDB;integratedSecurity=true;";
			connection=DriverManager.getConnection(connectionURL);
			System.out.println("ket noi ok roi!!!!!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("loi ket noi!!!");
		}
		try {
                    String sql="delete from SaveMessageDB";
                    Statement statement=connection.createStatement();
                    ResultSet result=statement.executeQuery(sql);
                    System.out.println("Xoa DB thành công");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("in ra that bai " +e.getMessage());
		}
                
	}
//        public static void main(String[] args) throws ClassNotFoundException{
//            ArrayList<String> indb=indb();
//            System.out.println(indb);
//        }
}
