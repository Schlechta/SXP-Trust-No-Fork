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

/**
 * This class contains a set of two rates that one user can give to another one.
 * The rapidity and conformity of a transaction is so evaluated during this process.
 */
public class UserRate {
	private float rapidity;
	private float conformity;
	private long date;

	/**
	 * Create a new couple of rating through two given values.
	 * @param rapidity
	 * @param conformity
	 */
	public UserRate(float rapidity, float conformity){
		super();
		this.rapidity = rapidity;
		this.conformity = conformity;
		this.date = System.currentTimeMillis();
	}

	/**
	 * Create a default couple of rating both set to undefined value (ie: -1).
	 */
	public UserRate(){ 
		super();
		this.rapidity = -1;
		this.conformity = -1;
		this.date = System.currentTimeMillis();
	}

	// Getters
	public String getRapidity() {
		return rapidity;
	}

	public String getConformity() {
		return conformity;
	}

	public long getDate() {
		return date;
	}

	// Setters
	public void setRapidity(float rapidity) {
		this.rapidity = (rapidity < 0 || rapidity > 5) ? -1 : rapidity;
	}

	public void setConfromity(float conformity) {
		this.conformity = (conformity < 0 || conformity > 5) ? -1 : conformity;
	}
}
