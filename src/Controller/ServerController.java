package Controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import displayUI.PortConfig;
import displayUI.ServerUI;
import operation.ClientThread;
import operation.ServerThread;

public class ServerController {
	private boolean isStart = false;
	private List<ClientThread> client = null;
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private ServerUI SU;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerController SC = new ServerController();
					SC.SU.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * ��ʼ��
	 */

	public ServerController() {
		SU = new ServerUI();
		PortConfig pc = new PortConfig();
		
		
		// �˿�����
		SU.btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				PortConfig pc = new PortConfig();
				pc.setVisible(true);
			}
		});

		// ��������
		SU.btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});

		// ֹͣ����
		SU.btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		// �˳�
		SU.btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// ���ı������ӻس����͹���
		SU.txt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		// ��������
		SU.btnSend.setEnabled(false);
		SU.btnSend.addActionListener(new ActionListener() {
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
		if (!isStart) {
			JOptionPane.showMessageDialog(SU, "��������δ��������������������");
			return;
		}
		if (client.size() == 0) {
			JOptionPane.showMessageDialog(SU, "û���û����ߣ����ܷ�����Ϣ��");
			return;
		}
		String message = SU.txt.getText().trim(); // ȥ���ַ���ͷ����β���Ŀ��ַ���
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(SU, "��Ϣ����Ϊ�գ�");
			return;
		}
		sendServerMessage(message); // Ⱥ����Ϣ
		SU.textArea.append("[ϵͳ֪ͨ] " + SU.txt.getText() + "\r\n");
		SU.txt.setText(null);
	}

	/**
	 * 
	 * @Description ������Ⱥ����Ϣ
	 * @author Jason
	 * @date 2020��6��23��
	 * @param message
	 */
	public synchronized void sendServerMessage(String message) {
		for (int i = client.size() - 1; i >= 0; i--) {
			client.get(i).getWrite().println("[ϵͳ֪ͨ] " + message);
			client.get(i).getWrite().flush();
		}
	}

	/**
	 * 
	 * @Description �Ӷ˿�����PortConfig���ȡtextPort��Ķ˿�ֵ������������
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public void start() {
		if (isStart) {
			JOptionPane.showMessageDialog(SU, "�������Ѿ�����������");
			return;
		}
		int port = -1;
		try {
			port = Integer.parseInt(PortConfig.textPort.getText());
			if (port < 0) {
				JOptionPane.showMessageDialog(SU, "�˿ں�Ϊ��������");
				return;
			}
			if(!serverStart(port)) {return ;}
			SU.textArea.append("���������������˿ںţ�" + port + "\r\n");
			JOptionPane.showMessageDialog(SU, "�����������ɹ���");

			SU.btnConnect.setEnabled(false);
			SU.btnUser.setEnabled(false);
			SU.btnStop.setEnabled(true);
			SU.txt.setEditable(true);
			SU.btnSend.setEnabled(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(SU, "�˿ں�Ϊ��������");
		}
	}

	/**
	 * 
	 * @Description ������ֹͣ����
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public void stop() {
		if (!isStart) {
			JOptionPane.showMessageDialog(SU, "��������δ����������ֹͣ��");
			return;
		}
		try {
			closeServer();
			SU.btnConnect.setEnabled(true);
			SU.btnUser.setEnabled(true);
			SU.btnStop.setEnabled(false);
			SU.txt.setEditable(false);
			SU.btnSend.setEnabled(false);

			SU.textArea.append("�������ѳɹ�ֹͣ��\r\n");
			JOptionPane.showMessageDialog(SU, "��������ֹͣ��");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(SU, "ֹͣ�����������쳣!");
		}
	}

	/**
	 * 
	 * @Description ��������������
	 * @author Jason
	 * @date 2020��6��23��
	 * @param port
	 */
	public boolean serverStart(int port) {
		client = new ArrayList<ClientThread>(); // �ſͻ����߳�
		try {
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket, SU, client, SU.listModel, SU.textArea);
			serverThread.start();
			isStart = true;
		} catch (BindException e) {
			isStart = false;
			JOptionPane.showMessageDialog(SU, "�˿ںű�ռ�ã�");
		} catch (Exception e) {
			e.printStackTrace();
			isStart = false;
			JOptionPane.showMessageDialog(SU, "�����������쳣");
		}
		return isStart == true;
	}

	/**
	 * 
	 * @Description �������ر�
	 * @author Jason
	 * @date 2020��6��23��
	 */
	public void closeServer() {
		try {
			if (serverThread != null) {
				serverThread.stop(); // ֹͣ�����߳�
			}

			// ���������������û����ߵ���Ϣ
			for (int i = client.size() - 1; i >= 0; i--) {
				client.get(i).getWrite().println("CLOSE");
				client.get(i).getWrite().flush();
				// �ͷ���Դ
				client.get(i).stop(); // ֹͣ����Ϊ�ͻ�������߳�
				client.get(i).getRead().close();
				client.get(i).getWrite().close();
				client.get(i).getSocket().close();
				client.remove(i);
			}
			// �رշ���������
			if (serverSocket != null) {
				serverSocket.close();
			}

			SU.listModel.removeAllElements(); // ����û��б�
			isStart = false;
		} catch (IOException e) {
			e.printStackTrace();
			isStart = true;
		}
	}

}
