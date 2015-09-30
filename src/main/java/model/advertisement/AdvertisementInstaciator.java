/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package model.advertisement;

import model.data.item.Item;
import model.data.user.User;
import model.data.user.UserRating;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory.Instantiator;
import net.jxta.document.Element;

/**
 * This class is a wrapper to construct an Instantiator for our Advertisements
 * Once the AdvertisementInstanciator registered to JXTA, JXTA can construct a received
 * advertisement automatically
 * @author Prudhomme Julien
 *
 */
public class AdvertisementInstaciator implements Instantiator{
	
	private Class<? extends Advertisement> advClass;
	private String advType;
	
	public AdvertisementInstaciator(AbstractAdvertisement advClass) {
		this.advClass = advClass.getClass();
		this.advType = advClass.getAdvType();
	}
	
	@Override
	public String getAdvertisementType() {
		return advType;
	}

	@Override
	public Advertisement newInstance() {
		try {
			return advClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Advertisement newInstance(Element root) {
		try {
			return advClass.getConstructor(Element.class).newInstance(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void RegisterAllAdv() {
		Item.register();
		User.register();
		// UserRating.register(); TO ENABLE ONCE IT WORKS.
	}

}
