package controller;

import view.Application;
import model.User;

/**
 * Cr�e un PDF de type contrat de vente/�change
 * @author Ismael Cussac
 *
 */
public class GeneratePdfContrat implements Validator {

	private int i;
	private User user;
	
	public GeneratePdfContrat(int i) {
		this.i = i;
		this.user = Application.getInstance().getUsers().getConnectedUser();
	}
	
	public boolean validate() {
		return user.getObjets().get(i) != null && user != null;
		
	}

	public boolean process() {
		user.getObjets().get(i).createPdf();
		return true;
	}
}
