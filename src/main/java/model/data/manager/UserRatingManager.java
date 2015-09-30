/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme, Schlechta.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package model.data.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.jxta.discovery.DiscoveryService;

import org.jdom2.Element;

import util.Printer;
import util.StringToElement;
import util.VARIABLES;
import util.secure.AsymKeysImpl;
import model.data.contrat.Contrat;
import model.data.favorites.Favorites;
import model.data.item.Item;
import model.data.user.Conversations;
import model.data.user.User;
import model.data.user.UserRate;
import model.data.user.UserRating;
import model.data.user.UserMessage;
import model.network.search.Search;

public class UserRatingManager extends UserManager{
	private HashMap<BigInteger, HashMap<BigInteger, UserRating>> ratings = new HashMap<String,HashMap<String, UserRating>>;  // The string key is the user's public key in hexadecimal

	// Constructor
	public UserRatingManager(Manager m) {
		super(m);
	}

	// Getters
	/**
	 * Return the note map given from a given publicKey.
	 * @param to
	 * @return
	 */
	public HashMap<BigInteger, UserRating> getUserRatingHashmap(BigInteger to){
		return ratings.get(to);
	}

	/**
	 * Return the user rating information after a second get.
	 * @param records
	 * @param from
	 * @return
	 */
	public UserRating getUserRatingLast(HashMap<BigInteger, UserRating> records, BigInteger from){
		return records.get(from);
	}

	/**
	 * Return an Array List which contain latest rates given to "To" by "From".
	 * @param to
	 * @param from
	 * @return
	 */
	public UserRating getUserRating(BigInteger to, BigInteger from){
		ratings.get(to).get(from);
	}

	/**
	 * Return the user's rates array list.
	 * @param to
	 * @return a new list containing user's items
	 */
	public ArrayList<UserRating> getUserRatingArray(to) {
		HashMap<BigInteger, UserRating> finalMap = getUserRatingHashmap(to);
		Iterator<Integer> kIterator = finalMap.keySet().iterator();

		ArrayList<UserRating> ratingArray = new ArrayList<UserRating>();

		while(kIterator.hasNext()){
			UserRating key = kIterator.next();
			ratingArray.add(key);
		}

		return userItems;
	}

	
	// Adders

	/**
	 * Add a rating
	 * @param from the pubKey
	 * @param to the pubKey
	 * @param publish true if the note is emmediatly publish on the network.
	 */
	public void addRate(BigInteger from, BigInteger to) {

	}
}
