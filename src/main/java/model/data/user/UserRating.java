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

import java.math.BigInteger;
import util.secure.AsymKeysImpl;
import controller.ManagerBridge;

import org.jdom2.Element;

/**
 * This class extends UserRate with a date and a connection between users through their public keys.
 */
public class UserRating extends UserRate{
	private BigInteger pubKeyFrom;
	private BigInteger pubKeyTo;
	private long date;
	
	/**
	 * Create a new rating from an user to another.
	 * @param rapidity
	 * @param conformity
	 * @param pubKeyFrom
	 * @param pubKeyTo
	 * @param date
	 */
	public UserRating(float rapidity, float conformity,
						BigInteger pubKeyFrom, BigInteger pubKeyTo){
		super(rapidity, conformity);
		this.pubKeyFrom = pubKeyFrom;
		this.pubKeyTo = pubKeyTo;
		this.date = System.currentTimeMillis();
	}

	/**
	 * Create a new rating from the current user to another.
	 * @param rapidity
	 * @param conformity
	 * @param pubKeyTo
	 * @param date
	 */
	public UserRating(float rapidity, float conformity, BigInteger pubKeyTo){
		super(rapidity, conformity);
		this.pubKeyFrom = ManagerBridge.getCurrentUser().getKeys().getPublicKey();
		this.pubKeyTo = pubKeyTo;
		this.date = System.currentTimeMillis();
	}

	/**
	 * Create a new rating from the current user to another.
	 * @param rate
	 * @param pubKeyTo
	 */
	public UserRating(UserRate rate, BigInteger pubKeyTo){
		super(rate);
		this.pubKeyFrom = ManagerBridge.getCurrentUser().getKeys().getPublicKey();
		this.pubKeyTo = pubKeyTo;
		this.date = System.currentTimeMillis();
	}

	/**
	 * Create a default rate to a given publickey user account.
	 * @param pubKeyTo
	 */
	public UserRating(BigInteger pubKeyTo){ 
		super();
		this.pubKeyTo = pubKeyTo;
		this.date = System.currentTimeMillis();
	}

	/**
	 * Construct a new personal user rating based on a XML, well and known formated string.
	 * @param XML
	 */
	public UserRating(String XML) {
		super(XML);
	}

	/**
	 * Construct a new personal user rating based on an XML element
	 * @param u
	 */
	public UserRating(Element u) {
		super(u);
	}

	@SuppressWarnings("rawtypes")
	public UserRating(net.jxta.document.Element u) {
		super(u);
	}

	// Getters

	public BigInteger getPubKeyFrom() {
		return pubKeyFrom;
	}

	public BigInteger getPubKeyTo() {
		return pubKeyTo;
	}

	public long getDate() {
		return date;
	}

	public UserRate getRate() {
		return new UserRate (super.getRapidity(), super.getConformity());
	}

	// Setters

	public void setPubKeyFrom(User u) {
		this.pubKeyFrom = u.getKeys().getPublicKey();
	}

	public void setPubKeyFrom(BigInteger pubKeyFrom) {
		this.pubKeyFrom = pubKeyFrom;
	}

	public void setPubKeyFrom() {
		this.pubKeyFrom = ManagerBridge.getCurrentUser().getKeys().getPublicKey();
	}

	public void setPubKeyTo(User u) {
		this.pubKeyTo = u.getKeys().getPublicKey();
	}

	public void setPubKeyTo(BigInteger pubKeyTo) {
		this.pubKeyTo = pubKeyTo;
	}

	public void setDate (long date){
		this.date = date;
	}

	// Advertisement

	/**
	 * Used to define Keys and initialize some values.
	 */
	@Override
	protected void setKeys() {
		super.setKeys();
		this.addKey("pubKeyFrom", true, false);
		this.addKey("pubKeyTo", true, false);
		this.addKey("date", false, false);
	}

	/**
	 * Used to add all keys.
	 */
	@Override
	protected void putValues() {
		super.putValues();
		this.addValue("pubKeyFrom", this.getPubKeyFrom().toString());
		this.addValue("pubKeyTo", this.getPubKeyTo().toString());
		this.addValue("date", Long.toString(this.getDate()));
	}

	@Override
	protected String getAdvertisementName() {
		return this.getClass().getName();
	}

	@Override
	protected boolean handleElement(org.jdom2.Element e) {
		String val = e.getText();

		switch(e.getName()) {
			case "pubKeyFrom": setPubKeyFrom(new BigInteger(val)); return true;
			case "pubKeyTo": setPubKeyTo(new BigInteger(val)); return true;
			case "date": setDate(Long.parseLong(val)); return true;
		}

		return super.handleElement(e);
	}

	@Override
	public UserRating clone(){
		return new UserRating(this.getRapidity(), this.getConformity(),
								this.getPubKeyFrom(), this.getPubKeyTo());
	}

	@Override
	public String getSimpleName() {
		return getClass().getSimpleName();
	}
}
