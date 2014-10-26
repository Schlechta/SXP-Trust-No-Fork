package model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;


/**
 * Cette classe enregistre des advs trouv�s (dans un buffer) jusqu'a ce qu'on demande de les r�cuperer.
 * @author Prudhomme Julien
 *
 * @param <T>
 */
public class AdvertisementSearch<T extends Advertisement> implements DiscoveryListener{

	public ArrayList<T> advs = new ArrayList<T>();
	
	@Override
	public void discoveryEvent(DiscoveryEvent event) {
		Enumeration advs = event.getResponse().getAdvertisements();
		while(advs.hasMoreElements()) {
			this.advs.add((T) advs.nextElement());
		}
	}
	
    /**
     * Retourne tout les advs trouv� et vide le buffer
     * @return
     */
	public ArrayList<T> getAdvs() {
		ArrayList<T> advs = (ArrayList<T>) this.advs.clone();
		this.advs.clear();
		return advs;
	}

}
