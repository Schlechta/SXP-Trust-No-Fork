package model.objet;

import java.util.Locale;

import model.advertisement.AbstractAdvertisement;
import net.jxta.document.Element;
import net.jxta.document.XMLElement;
import net.jxta.id.ID;

/**
 * Class Item : description of object
 */

public class Item extends AbstractAdvertisement<Item>{
	
	public static enum TYPE{
		WISH,						// If object is needed
		PROPOSAL					// If object is proposed
	};
	
	private String owner;			// Owner of the object
	private String friendlyNick;	// Friendly-user Pseudo of owner
	private String title;			// Title of the object
	private Category category;		// Category of the object
	private String description;		// Big description of the object
	private String image;			// Image of the object (convert with Base64)
	private Locale zone;			// Localization of the object (example : UK for United Kingdom)
	private String contact;			// Description of method for contact the owner
	private long date;				// Date of post/update
	private long lifeTime;			// LifeTime of the object (at the end of this, the object is delete)
	private TYPE type;				// Proposal/Wish
	
	public Item(String owner,String title,
			Category category, String description, String image,
			Locale zone,String contact,long date,long lifeTime,TYPE type){
		super();
		this.owner = owner;
		this.title = title;
		this.category = category;
		this.description = description ;
		this.image = image;
		this.zone = zone;
		this.contact = contact;
		this.date = date;
		this.lifeTime = lifeTime;
		this.type = type;
		putKeys();
	}
	
	public Item(){
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getFriendNick(){
		return friendlyNick;
	}
	
	public void setFriendNick(String friendNick){
		this.friendlyNick = friendNick;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Locale getZone() {
		return zone;
	}

	public void setCountry(String language, String country){
		zone = new Locale(language, country);
	}
	
	public String getCountry(){
		return zone.getDisplayCountry();
	}
	
	public String getLanguage(){
		return zone.getLanguage();
	}

	public void setZone(Locale zone) {
		this.zone = zone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(long vie) {
		this.lifeTime = vie;
	}
	
	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
	
	public Item getItem(){
		return this;
	}
	
	//////////////////////////////////////////////// ADVERTISEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * Used to define Keys
	 */
	@Override
	protected void setKeys() {
		this.addKey("owner", true);
		this.addKey("friendNick", false);
		this.addKey("title", true);
		this.addKey("category",true);
		this.addKey("description",false);
		this.addKey("image",false);
		this.addKey("zone",true);
		this.addKey("contact",false);
		this.addKey("date",true);
		this.addKey("lifeTime",false);
		this.addKey("type", true);
	}
	
	/**
	 * Used to add all keys
	 */
	@Override
	protected void putKeys() {
		putValue("owner", this.getOwner());
		putValue("friendNick", this.getFriendNick());
		putValue("title", this.getTitle());
		putValue("category", category.getChoice());
		putValue("description", this.getDescription());
		putValue("image", this.getImage());
		putValue("zone", this.getCountry());
		putValue("contact", this.getContact());
		putValue("date", String.valueOf(this.getDate()));
		putValue("lifeTime", String.valueOf(this.getLifeTime()));
		putValue("type", this.getType().toString());
	}
	
	/**
	 * Used to create Element for a known key
	 */
	@Override
	@SuppressWarnings({"rawtypes","null"})
	protected Element getElement(String key) {
		XMLElement root = null;
		root.addAttribute(key, keyValue.get(key));
		return root;
	}

	@Override
	public ID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getIndexFields() {
		// TODO Need verification
		return (String[]) indexes.toArray();
	}
	
	@Override
	public String getAdvType(){
		return "jxta:"+Item.class.getName();
	}
}
