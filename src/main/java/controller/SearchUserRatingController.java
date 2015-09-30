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

package controller;

import java.util.ArrayList;
import java.util.HashMap;

import model.Application;
import model.data.user.User;
import model.data.user.UserRate;
import model.data.user.UserRating;
import model.network.search.Search;
import model.network.search.SearchListener;

public class SearchUserController implements SearchListener<UserRating>{
	
	private ArrayList<SearchListener<UserRating>> listeners = new ArrayList<SearchListener<UserRating>>();
	private HashMap<String, UserRating> ratings = new HashMap<>();
	
	public void startSearch(String nick) {
		Search<UserRating> s = new Search<UserRating>(Application.getInstance().getNetwork(), UserRating.class.getSimpleName(), "nick", false);
		s.addListener(this);
		System.out.println("start search for user nammed " + nick);
		s.search(nick, 0, 0);
	}
	
	public void startSearchByPublicKey(String publicKey) {
		Search<UserRating> s = new Search<UserRating>(Application.getInstance().getNetwork(), UserRating.class.getSimpleName(), "superPublicKey", false);
		s.addListener(this);
		System.out.println("start search for user with public key " + publicKey);
		s.search(publicKey, 3000, 5);
	}
	
	public UserRating getPublicKeyResult() {
		return ratings.size() == 0 ? null: ratings.values().iterator().next();
	}
	
	public void addListener(SearchListener<UserRating> l) {
		listeners.add(l);
	}
	
	private void notifyListeners(UserRating event) {
		for(SearchListener<UserRating> l: listeners) {
			l.searchEvent(event);
		}
	}
	
	@Override
	public void searchEvent(UserRating event) {
		if(!event.checkSignature(event.getKeys())) {
			System.out.println("bad signature for " + event.getNick());
			return;
		}
		if(ratings.containsKey(event.getKeys().getPublicKey().toString(16))) {
			UserRating r = ratings.get(event.getKeys().getPublicKey().toString(16));
			if(event.getLastUpdated() > r.getLastUpdated()) {
				ratings.put(event.getKeys().getPublicKey().toString(16), event);
				notifyListeners(event);
			}
		}
		else {
			ratings.put(event.getKeys().getPublicKey().toString(16), event);
			notifyListeners(event);
		}
	}
}
