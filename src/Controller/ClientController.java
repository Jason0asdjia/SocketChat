package Controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import displayUI.ClientUI;
import displayUI.ConnectUI;
import displayUI.UserConfig;
import operation.MessageThread;
import user.User;

public class ClientController {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientController CC = new ClientController();
					CC.CU.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
//		ClientController CC = new ClientController();
//		new ClientUI();
	}
	//�Ƿ�����
	private boolean isConnect = false;
	private BufferedReader read;
	private PrintWriter write;
	private Socket socket;
	private MessageThread messageThread;
	public ClientUI CU;

	/*
	 * ��ʼ��
	 */
	public ClientController() {
		CU = new ClientUI();
		UserConfig UserCf = new UserConfig();
		ConnectUI ConUI = new ConnectUI();

		// �û����ð�ť
		CU.btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				UserConfig c = new UserConfig();
				UserCf.setVisible(true);
			}
		});

		// �������ð�ť
		CU.btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				ConnectUI c = new ConnectUI();
				ConUI.setVisible(true);
			}
		});
		// ��¼��ť
		CU.btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});

		// ע��
		CU.btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});

		// �˳�
		CU.btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// ˽��
		CU.btnPrivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToOne();
			}
		});
		// ���ı������ӻس����͹���
		CU.textSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		// ��������
		CU.btnSend.setEnabled(false);
		CU.btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

	}

	/**
	 * 
	 * @Description ������Ϣ
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public synchronized void send() {
		if (!isConnect()) {
			JOptionPane.showMessageDialog(CU, "��û�����ӷ��������޷�������Ϣ��");
			return;
		}
		String message = CU.textSend.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(CU, "��Ϣ����Ϊ��");
			return;
		}
		sendMessage(CU.getTitle() + "@" + "ALL" + "@" + message);
		CU.textSend.setText(null);
	}

	public synchronized void sendToOne() {
		if (!isConnect()) {
			JOptionPane.showMessageDialog(CU, "��û�����ӷ��������޷�������Ϣ��");
			return;
		}
		String message = CU.textSend.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(CU, "��Ϣ����Ϊ��");
			return;
		}
		String name = CU.comboBox.getSelectedItem().toString();
		sendMessage(CU.getTitle() + "@" + "ONE" + "@" + message + "@" + name);
		CU.textSend.setText(null);
	}

	/**
	 * 
	 * @Description ��¼����
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public void login() {
		int port = -1;
		if (isConnect()) {
			JOptionPane.showMessageDialog(CU, "�Ѿ���������״̬�������ظ����ӣ�");
			return;
		}
		try {
			try {
				port = Integer.parseInt(ConnectUI.portNumber.getText().trim());
//				System.out.println(port);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(CU, "����Ķ˿ںŲ��淶��Ҫ��Ϊ������");
			}

			String hostIp = ConnectUI.IpNumber.getText().trim();
			String name = UserConfig.textName.getText().trim();
			User user = new User(name, hostIp);

			if (hostIp.equals("") || name.equals("")) {
				JOptionPane.showMessageDialog(CU, "Ip��ַ���û���������Ϊ�գ�");
				return;
			}

//			System.out.println(hostIp+" "+ name);
			boolean flag = connecServer(port, hostIp, name);
//			if (flag == false) {
//				JOptionPane.showMessageDialog(CU, "�����������ʧ�ܣ�");
//				return;
//			}
			
			

		} catch (Exception e) {
			JOptionPane.showMessageDialog(CU, e.toString());
		}
	}

	/**
	 * 
	 * @Description ���ӷ�����
	 * @author Jason
	 * @date 2020��6��23��
	 * @param port
	 * @param hostIp
	 * @param name
	 * @return
	 */
	public boolean connecServer(int port, String hostIp, String name) {
		try {
			socket = new Socket(hostIp, port); // ���ݶ˿ںźźͷ�����
			write = new PrintWriter(socket.getOutputStream());
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// ���Ϳͻ��˵Ļ�����Ϣ
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			// ����������Ϣ���߳�
			messageThread = new MessageThread(this, read, write, socket);
			messageThread.start();
			//�жϵ�ǰ�߳��Ƿ��Ѿ�����
			
			setConnect(true); // ״̬��Ϊ��������
			return true;
		} catch (Exception e) {
			CU.textShow.append("��˿ں�Ϊ��" + port + ",   IP��ַΪ��" + hostIp + "�ķ���������ʧ�ܣ�\r\n");
			setConnect(false); // ״̬Ϊ��δ����
			return false;
		}
	}

	/**
	 * 
	 * @Description ע������
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public void logout() {

//		String hostIp = ConnectUI.IpNumber.getText().trim();
//		String name = UserConfig.textName.getText().trim();

		if (!isConnect()) {
			JOptionPane.showMessageDialog(CU, "�Ѿ��ǶϿ�״̬��Ŷ��");
			return;
		}
		try {
			boolean flag = closeConnect(); // �Ͽ�����
			if (!flag) {
				JOptionPane.showMessageDialog(CU, "�Ͽ����ӷ����쳣��");
				return;
			}
			JOptionPane.showMessageDialog(CU, "�Ͽ��ɹ���");

			CU.comboBox.removeAllItems();
			CU.comboBox.revalidate();

			CU.btnConnect.setEnabled(true);
			CU.btnUser.setEnabled(true);
			CU.btnLogin.setEnabled(true);
			CU.btnLogout.setEnabled(false);
			CU.textSend.setEditable(false);
			CU.btnSend.setEnabled(false);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(CU, e.toString());
		}

	}

	/**
	 * 
	 * @Description �ͻ��˹ر�
	 * @author Jason
	 * @date 2020��6��23��
	 * @return
	 */
	public synchronized boolean closeConnect() {
		try {
			sendMessage("CLOSE"); // ���ͶϿ����������������
			messageThread.stop(); // ֹͣ������Ϣ���߳�
			// �ͷ���Դ
			if (read != null) {
				read.close();
			}
			if (write != null) {
				write.close();
			}
			if (socket != null) {
				socket.close();
			}
			setConnect(false);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			setConnect(true);
			return false;
		}

	}

	/**
	 * 
	 * @Description ������Ϣ
	 * @author Jason
	 * @date 2020��6��23��
	 * @param message
	 */
	public synchronized void sendMessage(String message) {
		System.out.println(message);
		write.println(message);
		write.flush();
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	
}
