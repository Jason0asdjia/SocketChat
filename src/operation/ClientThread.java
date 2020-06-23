package operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import Controller.ClientController;
import Controller.ServerController;
import user.User;

/**
 * 
 * @Description ������Ϊ�ͻ��˷����߳�
 * @author Jason
 * @date 2020��6��23�� ����1:00:57
 */
public class ClientThread extends Thread {
	private Socket socket;
	private BufferedReader read;
	private PrintWriter write;
	private User user;
	private List<ClientThread> client;
	private DefaultListModel listModel;
	private JTextArea textArea;

	// Ϊ�û��ṩ�����������getter����
	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getRead() {
		return read;
	}

	public PrintWriter getWrite() {
		return write;
	}

	public User getUser() {
		return user;
	}

	public ClientThread(Socket socket, List<ClientThread> client, DefaultListModel listModel, JTextArea textArea) {
		super();
		this.socket = socket;
		this.client = client;
		this.listModel = listModel;
		this.textArea = textArea;
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			write = new PrintWriter(socket.getOutputStream());
			// �����û���Ϣ
			String info = read.readLine();
			StringTokenizer st = new StringTokenizer(info, "@");
			user = new User(st.nextToken(), st.nextToken());
			
			// ����ǰ�̵߳��û���ӵ������û�
			//	�ж��Ƿ��ظ���¼
			if (ServerController.onLineUser.containsKey(user.getName())) {
				write.println("FAILED@" + user.getName() + "@");
				write.flush();
			} else {
				ServerController.onLineUser.put(user.getName(), user);
//				for(String str:onLineUser.keySet()) {
//				System.out.println(str);
//			}
//				System.out.println(ClientController.onLineUser.size());

				// �������ӳɹ�����Ϣ
				write.println("SUCCESS@" + user.getName() + "@");
				write.flush();
				write.print("[ϵͳ֪ͨ] " + user.getName() + "����������ӳɹ���\r\n");
				write.flush();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		boolean flag = false;
		// ��¼�����Լ�������
		int len = client.size();
		// ������ǰ���������û���Ϣ
		try {
			if (client.size() > 0) {
				String temp = "";
				for (int i = client.size() - 1; i >= 0; i--) {
					// ������Լ�
//					System.out.println(client.get(i).getUser().getName());
//					System.out.println(user.getName());
					if (client.get(i).getUser().getName().equals(user.getName())) {
						len--;
						continue;
					}
					temp += (client.get(i).getUser().getName() + "/" + client.get(i).getUser().getIp()) + "@";
					flag = true;
				}
				if (flag) {
					client.get(client.size() - 1).getWrite().println("USERLIST@" + len + "@" + temp);
					client.get(client.size() - 1).getWrite().flush();
				}

			}

			// �����е��û����͸��û����ߵ���Ϣ
			for (int i = client.size() - 1; i >= 0; i--) {
				// �����Լ�
				if (client.get(i).getUser().getName().equals(user.getName())) {
					continue;
				}
				client.get(i).getWrite().println("ADD@" + user.getName());
				client.get(i).getWrite().flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���Ͻ��ܿͻ���������Ϣ�����д���
	 */
	@Override
	public void run() {
		String message = null;
		while (true) {
			try {
				message = read.readLine(); // ������Ϣ
				if (message.equals("CLOSE")) { // ��������
					textArea.append("[ϵͳ֪ͨ] " + this.getUser().getName() + "����!\r\n");
					// �Ͽ����ӵ���Դ
					this.read.close();
					this.write.close();
					this.socket.close();

					// �����������û����͸��û����ߵ���Ϣ
					for (int i = client.size() - 1; i >= 0; i--) {
						client.get(i).getWrite().println("DELETE@" + this.getUser().getName());
						client.get(i).getWrite().flush();
					}

					listModel.removeElement(user.getName());

					// ɾ�������ͻ��˵ķ����߳�
					for (int i = client.size() - 1; i >= 0; i--) {
						if (client.get(i).getUser() == user) {
							ClientThread temp = client.get(i);
							client.remove(i); // ɾ�����û��ķ����߳�
							temp.stop(); // ֹͣ�����߳�
							return;
						}
					}
				} else {
					sendMessage(message);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Description ת���ͻ��˷�������Ϣ
	 * @author Jason
	 * @date 2020��6��23��
	 * @param message
	 */
	public void sendMessage(String message) {
		StringTokenizer st = new StringTokenizer(message, "@");// ��@��Ϊ�ָ����ָ���Ϣ
		String name = st.nextToken(); // ����ʽȡ����һ���û�����
		String owner = st.nextToken(); // �ж�Ⱥ������˽��
		String contant = st.nextToken(); // ��Ϣ����
		if (owner.equals("ALL")) { // Ⱥ��
			message = name + "˵��" + contant;
			textArea.append(message + "\r\n");
			for (int i = client.size() - 1; i >= 0; i--) {
				client.get(i).getWrite().println(message); // ��ȡ�û������������ӡ��Ϣ
				client.get(i).getWrite().flush(); // ��ջ�����
			}
		} else if (owner.equals("ONE")) {// ˽��
			String privateName = st.nextToken(); // ��ȡ˽�Ķ��������
			System.out.println(privateName);
			System.out.println(contant);
			for (int i = client.size() - 1; i >= 0; i--) {
				System.out.print(client.get(i).getUser().getName() + " ");
				if (client.get(i).getUser().getName().equals(privateName)) {
					System.out.println("˽��");
					message = "ONE@" + name + "���Ķ���˵��" + contant;
					System.out.println("˽����Ϣ��" + message);
					client.get(i).getWrite().println(message);
					client.get(i).getWrite().flush();
				}
			}
		}
	}
}