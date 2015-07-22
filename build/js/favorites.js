/**
 * JavaScript for managing favorites
 * @author Michael DUBUIS
 */
var favoritesList = "favoritesList";
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 								    QUERY FROM JAVASCRIPT TO MODEL									   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
// Ask to the model to add itemKey in Favorites
function addItemFavorites(itemKey){
	var content = {"itemKey":itemKey};
	sendQuery("addItemFavorites", content);
}

// Ask to the model to remove itemKey in Favorites
function removeItemFavorites(itemKey){
	if(confirm("Are you sure to delete this item from Favorites ?")) {
		var content = {"itemKey":itemKey};
		sendQuery("removeItemFavorites", content);
	}
}

// Ask to the model to send item's itemKey from Favorites 
function loadItemFavorites(itemKey){
	var content = {"itemKey":itemKey};
	sendQuery("loadItemFavorites", content);
}

//Ask to the model to send all data items from Favorites (for the current user)
function loadItemsFavorites(){
	sendQueryEmpty("loadItemsFavorites");
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 								    ANSWER FROM MODEL TO JAVASCRIPT									   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
function favoritesItemsLoaded(content){
	$("#"+favoritesList).append(newRowFavorites(content));
	$("#"+itemList+" #"+removePunctuation(content.itemKey)).find("a.buttonFavorites").addClass("inFavorites");
	$("#"+itemList+" #"+removePunctuation(content.itemKey)).find("a.buttonFavorites").removeClass("buttonFavorites");
}

function favoritesItemLoaded(content){
	displayItemFavorites(content);
}

function itemFavoritesRemoved(content){
	var id = "favorites"+removePunctuation(content.itemKey);
	$("#"+id).detach();
	removeDisplayItemFavorites();
	$("#"+itemList+" #"+removePunctuation(content.itemKey)).find("a.inFavorites").addClass("buttonFavorites");
	$("#"+itemList+" #"+removePunctuation(content.itemKey)).find("a.inFavorites").removeClass("inFavorites");
}

function favoritesItemsLoadingStart(content){
	/*var div = document.createElement("div");
	div.attr("id","loading");
	div.append(document.createTextNode("Loading..."));
	$("aside").append(div);*/
}
function favoritesItemsLoadingEnd(content){
	$("aside #loading").remove();
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 											HTML GENERATOR											   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
function getFavoritesDisplay(){
	var div = document.createElement("div");
	$(div).append(getTableItem(favoritesList));
	var loading = document.createElement("div");
	$(loading).attr("id", "loading");
	$(div).append(loading);
	return div;
}

function getItemFavoritesDisplay(){
	var div = document.createElement("div");
	$(div).attr("id", "itemFavoritesDisplayer");
	for ( var i = 0 ; i < itemFavoritesDisplayer.length ; i++ ) {
		$(div).append(getElement(itemFavoritesDisplayer[i]));
	}
	return div;
}

function clearFavoritesTable(){
	$("aside #favoritesList tbody").empty();
	var div = document.createElement("div");
	$(div).attr("id","loading");
	$(div).append("Loading...");
	$("aside").append(div);
}
function newRowFavorites(content){
	var row = document.createElement("tr");
	$(row).attr("id", "favorites"+removePunctuation(content.itemKey));
	// Title cell
	var cell1 = document.createElement("td");
	$(cell1).attr("class", "rowTitle");
	$(cell1).append(document.createTextNode(content.title));
	$(cell1).attr("onclick", "loadItemFavorites('"+content.itemKey+"');");
	$(row).append(cell1);
	// Description cell
	var cell2 = document.createElement("td");
	$(cell2).attr("class", "rowDescription");
	$(cell2).attr("onclick", "loadItemFavorites('"+content.itemKey+"');");
	if(content.description.length > 100)
		$(cell2).append(content.description.substring(0, 100)+" [...]");
	else
		$(cell2).append(content.description);
	$(row).append(cell2);
	// Buttons Cell
	var cell3 = document.createElement("td");
	$(cell3).attr("class", "rowActions");
	// Remove Button
	var removeButton = document.createElement("a");
	$(removeButton).attr("class", "button buttonRemove");
	$(removeButton).attr("onclick", "removeItemFavorites('"+content.itemKey+"');");
	//$(removeButton).append(document.createTextNode("Remove"));
	$(cell3).append(removeButton);
	$(row).append(cell3);
	return row;
}

function removeDisplayItemFavorites(){
	$("aside #itemFavoritesDisplayer").remove();
}

function displayItemFavorites(content){
	removeDisplayItemFavorites();
	$("aside").append(getItemFavoritesDisplay());
	$.each(content, function(key, value){
		if(key=="image")
			$("#itemFavoritesDisplayer"+" #"+key).attr("src", value);
		else{
			var text = document.createTextNode(value);
			$("#itemFavoritesDisplayer"+" #"+key).append(text);
		}
	});
}

function switchFavorites(){
	if($("aside").hasClass("hidden")){
		displayFavorites();
		$("nav .favoritesButton").addClass("selected");
	} else {
		hideFavorites();
		$("nav .favoritesButton").removeClass("selected");
	}
}