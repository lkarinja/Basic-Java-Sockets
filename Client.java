import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;

	public static void main(String args[]) {
		try {
			socket = new Socket("localhost", 1122);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread send = new Thread() {
			public void run() {
				String data = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				while (!socket.isClosed()) {
					try {
						data = reader.readLine();
						out.println(data);
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}
				try {
					out.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
		Thread receive = new Thread() {
			public void run() {
				while (!socket.isClosed()) {
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
