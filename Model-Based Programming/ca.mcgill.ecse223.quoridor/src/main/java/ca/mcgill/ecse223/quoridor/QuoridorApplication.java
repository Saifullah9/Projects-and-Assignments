package ca.mcgill.ecse223.quoridor; 

import ca.mcgill.ecse223.quoridor.model.Quoridor; 
import ca.mcgill.ecse223.quoridor.view.QuoridorPage;

public class QuoridorApplication {

	private static Quoridor quoridor;
	private static QuoridorPage view;


	public static Quoridor getQuoridor() {
		if (quoridor == null) {
			quoridor = new Quoridor();
		}
 		return quoridor;
	}
	
	public static QuoridorPage getQuoridorView() {
		if (view == null) {
			view = new QuoridorPage();
            view.setVisible(true);
		}
		return view;
	}
	
	public static void main(String[] args) {
		// start UI
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                view = new QuoridorPage();
                view.setVisible(true);
            }
        });
	}

}
