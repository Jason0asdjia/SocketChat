package DAO;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

import Controller.ServerController;

/**
 * 
 * @Description ����mysql���ݿ�
 * @author Jason
 * @date 2020��6��26��
 */
public class DAO {
	private static ServerController SC = new ServerController();
	private static Connection conn = null;
	public DAO() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
//			e.printStackTrace();
			JOptionPane.showMessageDialog(SC.SU, "���ݿ���������ʧ��" + e.getMessage());

		}
	}

	// �������ݿ�
	public static Connection getConn() {
		try {
			String url="jdbc:mysql://192.168.1.2:3306/SocketChat?&characterEncoding=utf-8&autoReconnect=true&useSSL=false";   //127.0.0.1:3306/database
			conn = DriverManager.getConnection(url,"root","123456");
			return conn;
		}catch(Exception e){
			JOptionPane.showMessageDialog(SC.SU, "���ݿ�����ʧ��" + e.getMessage());
			return null;
		}
	}
}
