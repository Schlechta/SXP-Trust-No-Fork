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
package view.interlocutors.search;

import model.data.item.Item;
import model.network.search.SearchListener;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import util.DateConverter;
import view.interlocutors.AbstractInterlocutor;
import controller.SearchItemController;

public class SearchItem extends AbstractInterlocutor implements SearchListener<Item> {

	public SearchItem(){
		super();
	}
	
	@Override
	public void run() {
		if(!isInitialized()) return;
		JSONObject c = getJSON(content);
		try {
			SearchItemController is = new SearchItemController();
			is.addListener(this);
			is.startSearch(c.getString("search"));
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			this.reset();
		}
	}

	@Override
	public void searchEvent(Item event) {
		try {
			JSONObject data = new JSONObject();
			data.put("query", "itemSearchFound");
			JSONObject content = new JSONObject();
			content.put("itemKey", event.getItemKey());
			content.put("owner", event.getOwner());
			content.put("nick", event.getFriendNick());
			content.put("title", event.getTitle());
			content.put("description", event.getDescription());
			content.put("category", event.getCategory().getStringChoice());
			content.put("contact", event.getContact());
			content.put("country", event.getCountry());
			content.put("image", event.getImage());
			content.put("lifetime", DateConverter.getString(event.getLifeTime()+event.getDate()));
			content.put("type", event.getType());
			data.put("content", content);
			com.sendText(data.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
