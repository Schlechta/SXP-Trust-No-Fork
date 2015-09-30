/**
 * JavaScript for managing search
 * @author Michael DUBUIS
 */
var itemSearchFieldAreLoaded = false;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 								    QUERY FROM JAVASCRIPT TO MODEL									   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
/**
 * Ask to the model to send item's searchable field
 */
function loadItemSearchField(){
	if(itemSearchFieldAreLoaded)
		return;
	sendQueryEmpty("loadItemSearchField");
	itemSearchFieldAreLoaded = true;
}
/**
 * Ask to the model to send item's category for searching
 */
function loadItemSearchFieldCategory(){
	sendQueryEmpty("loadItemSearchFieldCategory");
}
/**
 * Ask to the model to send item's type for searching
 */
function loadItemSearchFieldType(){
	sendQueryEmpty("loadItemSearchFieldType");
}
function searchItem(){
	if($("#search").val()!=""){
		$("#"+itemSearchList+" tbody").empty();
		var content = {"search":$("#search").val()};
		sendQuery("searchItem", content);
	}else{
		alert("Impossible to launch an empty search !")
	}
}
function loadItemSearch(itemKey){
	$("#"+removePunctuation(itemKey)+" .searchDisplayer").toggle(100);
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 								    ANSWER FROM MODEL TO JAVASCRIPT									   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
function itemSearchFieldLoaded(content){
	var option = document.createElement("option");
	$(option).append(content.field);
	$("#field").append(option);
}

function itemSearchFieldsLoaded(content){
	var option = document.createElement("option");
	$(option).append(content.option);
	$("#searchField").append(option);
}

function updateSearchField(){
	var field = $("#field").val();
	switch(field){
	case "category":
		var select = document.createElement("select");
		$(select).attr("id", "searchField");
		$(select).attr("name", "searchField");
		$("#searchField").replaceWith(select);
		loadItemSearchFieldCategory();
		break;
	case "type":
		var select = document.createElement("select");
		$(select).attr("id", "searchField");
		$(select).attr("name", "searchField");
		$("#searchField").replaceWith(select);
		loadItemSearchFieldType();
		break;
	default:
		var input = document.createElement("input");
		$(input).attr("id", "searchField");
		$(input).attr("name", "searchField");
		$(input).attr("type", "text");
		$("#searchField").replaceWith(input);
	}
}

function itemSearchFound(content){
	/*if($("#"+removePunctuation(content.itemKey)).lenght == 0)*/
		$("#"+itemSearchList).append(newRowItemSearch(content));
	/*else
		$("#"+itemSearchList+" #"+removePunctuation(content.itemKey)).replaceWith(newRowItem(content));*/
}
function itemSearchLoaded(content) {
	displayItemFavorites(content);
}
function itemSearchNotLoaded(content) {
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 											HTML GENERATOR											   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
function getSearchItem(){
	var content = document.createElement("div");
	$(content).attr("id", "content");
	$(content).append(searchForm);
	$(content).append(itemSearchTable);
	return content;
}

function addSearch(){
	var field = $("#field").val();
	var searchField = $("#searchField").val();
	var search = $("#search").val();
	if(search == ""){
		$("#search").val(field+":'"+searchField+"'");
	}else{
		$("#search").val(search+", "+field+":'"+searchField+"'");
	}
	$("#searchField").val("");
}
function resetSearch(){
	$("#search").val("");
	$("#itemSearchList tbody").empty();
}

function newRowItemSearch(content){
	var description = content.description.length>400?content.description.substring(0, 400):content.description;
	var row = getClone(searchItemRow);
	$(row).attr("id", removePunctuation(content.itemKey));
	$(row).find(".rowTitle").attr("onclick", "loadItemSearch('" + content.itemKey + "');");
	$(row).find("#nick").text(content.nick);
	$(row).find("#addToKnownUsers").attr("onclick", "addToKnownUsers('" + content.nick + "', '" + content.owner + "');");
	$(row).find("#sendMessage").attr("onclick", "sendMessageTo2('" + content.owner + "', '" + content.title + "');");
	$(row).find(".rowDescription").attr("onclick", "loadItemSearch('" + content.itemKey + "');");
	$(row).find(".rowTitle .resume").append(content.title);
	$(row).find(".rowDescription .resume").text(description);
	$(row).find("#title").text(content.title);
	$(row).find("#category").text(content.category);
	$(row).find("#type").text(content.type);
	$(row).find("#description").text(content.description);
	$(row).find("#country").text(content.country);
	$(row).find("#contact").text(content.contact);
	$(row).find("#image").attr("src", content.image);
	$(row).find(".rowActions .buttonFavorites").attr("onclick", "addItemFavorites('"+content.itemKey+"');");
	return row;
}