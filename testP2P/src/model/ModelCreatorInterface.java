package model;

public interface ModelCreatorInterface {
	
	
	/**
	 * G�n�re un mod�le
	 */
	public ObjetPDFModel getHTML();
	
	
	/**
	 * Cr�e le fichier PDF
	 */
	public void createPDF();

}
