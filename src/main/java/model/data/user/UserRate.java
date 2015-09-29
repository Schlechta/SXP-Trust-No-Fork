/* Copyright 2015 Schlechta.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */

package model.data.user;

import model.advertisement.AbstractAdvertisement;
import org.jdom2.Element;

/**
 * This class contains a set of two rates that one user can give to another one.
 * The rapidity and conformity of a transaction is so evaluated during this process.
 */
public class UserRate extends AbstractAdvertisement{
	private float rapidity;
	private float conformity;

	/**
	 * Create a new couple of rating through two given values.
	 * @param rapidity
	 * @param conformity
	 */
	public UserRate(float rapidity, float conformity){
		super();
		this.rapidity = rapidity;
		this.conformity = conformity;
	}

	public UserRate(UserRate rate){
		super();
		this.rapidity = rate.getRapidity();
		this.conformity = rate.getConformity();
	}

	/**
	 * Create a default couple of rating both set to undefined value (ie: -1).
	 */
	public UserRate(){ 
		super();
		this.rapidity = -1;
		this.conformity = -1;
	}

	/**
	 * Construct a new personal user rating based on a XML, well and known formated string.
	 * @param XML
	 */
	public UserRate(String XML) {
		super(XML);
	}

	/**
	 * Construct a new personal user rating based on an XML element
	 * @param u
	 */
	public UserRate(Element u) {
		super(u);
	}

	@SuppressWarnings("rawtypes")
	public UserRate(net.jxta.document.Element u) {
		super(u);
	}

	// Getters
	public String getRapidity() {
		return rapidity;
	}

	public String getConformity() {
		return conformity;
	}

	// Setters
	public void setRapidity(float rapidity) {
		this.rapidity = (rapidity < 0 || rapidity > 5) ? -1 : rapidity;
	}

	public void setConfromity(float conformity) {
		this.conformity = (conformity < 0 || conformity > 5) ? -1 : conformity;
	}

	// Advertisement

	/**
	 * Used to define Keys and initialize some values
	 */
	@Override
	protected void setKeys() {
		this.addKey("rapidity", true, true);
		this.addKey("conformity", true, true);
	}

	/**
	 * Used to add all keys
	 */
	@Override
	protected void putValues() {
		this.addValue("rapidity", this.getRapidity().toString());
		this.addValue("conformity", this.getConformity().toString());
	}

	@Override
	protected String getAdvertisementName() {
		return this.getClass().getName();
	}

	@Override
	protected boolean handleElement(org.jdom2.Element e) {
		String val = e.getText();
		switch(e.getName()) {
			case "rapidity": setRapidity(new Float(val)); return true;
			case "conformity": setConformity(new Float(val)); return true;
		}
		return false;
	}

	@Override
	public UserRate clone(){
		return new UserRate(this.getRapidity(), this.getConformity());
	}

	@Override
	public String getSimpleName() {
		return getClass().getSimpleName();
	}
}
