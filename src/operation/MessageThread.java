package operation;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import Controller.ClientController;
import displayUI.ClientUI;
import user.User;

/**
 * 
 * @Description �ͻ������õ���Ϣ���ݽ����߳�
 * @author Haomu
 * @date 2019��12��1�� ����1:00:57
 */
public class MessageThread extends Thread {
	private BufferedReader read;
	private PrintWriter write;
	private Socket socket;
	private ClientController CC;

	// ������Ϣ�̵߳Ĺ��췽��
	public MessageThread(ClientController CC, BufferedReader read, PrintWriter write, Socket socket) {
		super();
		this.CC = CC;
		this.read = read;
		this.write = write;
		this.socket = socket;
	}

	/**
	 * 
	 * @Description �ͻ�������ر�
	 * @author Haomu
	 * @date 2019��12��1�� ����4:52:36
	 * @throws Exception
	 */
	public synchronized void closeConnect() throws Exception {
		// ����û��б�
		CC.CU.comboBox.removeAllItems();

		// �����ر������ͷ���Դ
		if (read != null) {
			read.close();
		}
		if (write != null) {
			write.close();
		}
		if (socket != null) {
			socket.close();
		}
		CC.setConnect(false); // ��״̬��Ϊδ����״̬
		CC.CU.btnConnect.setEnabled(true);
		CC.CU.btnUser.setEnabled(true);
		CC.CU.btnLogin.setEnabled(true);
		CC.CU.btnLogout.setEnabled(false);
		CC.CU.textSend.setEditable(false);
		CC.CU.btnSend.setEnabled(false);
	}

	@Override
	public void run() { // ���Ͻ�����Ϣ
		String message = "";
		while (true) {
			try {
				message = read.readLine();
				System.out.println(message);
				StringTokenizer st = new StringTokenizer(message, "/@");
				String command = st.nextToken();
				System.out.println("cm"+command);
				if (command.equals("CLOSE")) { // �ر�����
					CC.CU.textShow.append("�������ѹرգ�\r\n");
					closeConnect(); // �����ر�����
					return; // �����߳�
				} else if (command.equals("ADD")) { // ���û����߸����б�
					String userName = "";
					String userIp = "";
					if ((userName = st.nextToken()) != null) {
						User user = new User(userName, userIp);
						CC.onLineUser.put(userName, user);
						CC.CU.comboBox.addItem(userName);
						CC.CU.comboBox.revalidate();
					}
					CC.CU.textShow.append("[ϵͳ֪ͨ] " + userName + "�����ˣ�\r\n");
				} else if (command.equals("DELETE")) { // ���û����߸����б�
					String userName = st.nextToken();
					User user = (User) CC.onLineUser.get(userName);
					CC.onLineUser.remove(userName);

					CC.CU.comboBox.removeItem(userName);
					CC.CU.comboBox.revalidate();
					CC.CU.textShow.append("[ϵͳ֪ͨ] " + userName + "�����ˣ�\r\n");
				} else if (command.equals("USERLIST")) { // �����û��б�
					int size = Integer.parseInt(st.nextToken());
					String userName = null;
					String userIp = null;
					for (int i = 0; i < size; i++) {
						userName = st.nextToken();
						userIp = st.nextToken();
						User user = new User(userName, userIp);
						CC.onLineUser.put(userName, user);
						CC.CU.comboBox.addItem(userName);
						CC.CU.comboBox.revalidate();
					}
				}
					else if(command.equals("ONE")){
						String msg = st.nextToken();
//						CC.CU.textShow.setForeground(Color.blue);
						CC.CU.textShow.append(msg + "\r\n");
					}
				else { // ��ͨ��Ϣ
					CC.CU.textShow.append(message + "\r\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}