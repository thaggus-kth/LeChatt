package view;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

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
			Color color, String greeting) throws IOException {
		try {
			SessionController sc = new SessionController(username, color, ip, port, greeting);
			SessionWindow sw = new SessionWindow(sc);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	public static void newServerSession(String username, int port,
											Color color) throws IOException {
		Server serverController = new Server(username, color, port);
		SessionWindow sw = new SessionWindow(serverController);
	}
	

}
