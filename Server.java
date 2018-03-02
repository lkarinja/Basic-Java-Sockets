import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	static ServerSocket server;
	static List<Socket> clients = new ArrayList<Socket>();
	static List<PrintWriter> writers = new ArrayList<PrintWriter>();

	public static void main(String args[]) {
		try {
			server = new ServerSocket(1122);
			Thread send = new Thread() {
				public void run() {
					String data = "";
					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
					while (!server.isClosed()) {
						try {
							data = reader.readLine();
							for (PrintWriter writer : writers) {
								writer.println(data);
							}
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}
					}
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			send.start();
			while (!server.isClosed()) {
				Socket client = server.accept();
				clients.add(client);
				writers.add(new PrintWriter(client.getOutputStream(), true));
				new ClientHandler(client);
			}
			for(Socket client : clients) {
				client.close();
			}
			for(PrintWriter writer : writers) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

class ClientHandler implements Runnable {
	Socket client;
	BufferedReader in;
	PrintWriter out;

	ClientHandler(Socket client) throws IOException {
		this.client = client;
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		this.out = new PrintWriter(client.getOutputStream(), true);
		this.run();
	}

	@Override
	public void run() {
		System.out.println("New Client");
		Thread receive = new Thread() {
			public void run() {
				while (!client.isClosed()) {
					try {
						System.out.println("Read: " + in.readLine());
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		receive.start();
	}

}
