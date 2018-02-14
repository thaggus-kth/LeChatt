package view;

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
	
//	public static void newClientSession(String username, String ip, int port,
//			Color color, String greeting) {
//		SessionController sc = new SessionController(username, color, ip, port, greeting);
//		SessionWindow sw;
//	}
	

}
