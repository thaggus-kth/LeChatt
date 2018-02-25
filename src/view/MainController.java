package view;

import java.awt.Color;
import session.Server;
import session.SessionController;

public final class MainController {
	
	private static LobbyWindow lw;
	
	public static void main(String[] args) {
		showLobbyWindow();
		
		// debug
		System.out.println("Main exit.");

	}
	
	public static void showLobbyWindow() {
		if (lw == null) {
			lw = new LobbyWindow();
		}
		lw.setVisible(true);
	}
	
	public static void newClientSession(String username, String ip, int port,
			Color color, String greeting) {
		SessionController sc = new SessionController(username, color, ip, port, greeting);
		SessionWindow sw = new SessionWindow(sc);
	}

	public static void newServerSession(String username, int port,
														Color color) {
		Server serverController = new Server(username, color, port);
		SessionWindow sw = new SessionWindow(serverController);
	}
	

}
