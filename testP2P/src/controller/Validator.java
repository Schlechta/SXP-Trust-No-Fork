package controller;

/**
 * Tout formulaire doit �tre v�rifier (validate() avant d'envoyer la requ�te (process())
 * @author Prudhomme Julien
 *
 */
public interface Validator {
	
	/**
	 * Valide une ou des entr�es
	 * @return false une ou des entr�e sont �rronn�e
	 */
	public boolean validate();
	
	
	/**
	 * Se sert des donn�es valid�e pour faire la requ�te
	 * @return vrai si la requete �r�ussi ou faux (erreur r�seau, etc)
	 */
	public boolean process();
}
