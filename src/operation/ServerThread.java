package operation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import displayUI.ServerUI;

/**
 * 
 * @Description �����������߳�
 * @author Jason
 * @date 2020��6��23�� 
 */
public class ServerThread extends Thread {
	private ServerSocket serverSocket;
	private ServerUI SU;
	private List<ClientThread> client;
	private JTextArea textArea;
	private DefaultListModel listModel;

	public ServerThread(ServerSocket serverSocket, ServerUI SU, List<ClientThread> client,
			DefaultListModel listModel, JTextArea textArea) {
		this.serverSocket = serverSocket;
		this.SU = SU;
		this.client = client;
		this.listModel = listModel;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		// ��ͣ�صȴ��ͻ�����
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ClientThread clients = new ClientThread(socket, client, listModel, textArea);
				client.add(clients);
//				System.out.println(client.size());
				listModel.addElement(clients.getUser().getName()); // ���������б�


				clients.start(); // �����ͻ��˷����߳�
				clients.update(); //�����û��б�
				textArea.append("[ϵͳ֪ͨ] " + clients.getUser().getName() + "�����ˣ�\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
