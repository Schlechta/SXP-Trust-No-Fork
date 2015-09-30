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
package model.data.user;

import model.advertisement.AbstractAdvertisement;
import model.advertisement.AdvertisementInstaciator;
import net.jxta.document.AdvertisementFactory;

import org.jdom2.Element;

import util.Hasher;
import util.secure.AsymKeysImpl;

/**
 * This class can be instantiated for contains an user.
 * This class extends AbstractAdvertisement and can be used like an advertisement.
 * @author Michael Dubuis
 * @author Julien Prudhomme
 *
 */
public class User extends AbstractAdvertisement implements Comparable<User>{
	private String nick;			// Friendly user nickname (useless for the system)
	private String hashPwd;			// PassWord is never saved but the hash of the password have to be saved
	private String name;			// The family name of the user
	private String firstName;		// The first name of the user
	private String email;			// The email of the user
	private String phone;			// The phone of the user
	private long date;				// The date of creation/update of the user's profile
	private UserRate rate;			// The general rate of the user.
	private String clearPassword;	// is never saved, not null only if a user log in this.
	
	/**
	 * To make new User (during registration)
	 * This method will generate new Key
	 * @param nick
	 * @param passWord
	 * @param name
	 * @param firstName
	 * @param email
	 * @param phone
	 * @param peerID
	 */
	public User(String nick,String passWord,String name,
			String firstName,String email,
			String phone
			){
		super();
		this.nick = nick;
		this.hashPwd = Hasher.SHA256(passWord);
		this.clearPassword = passWord;				// NEW user, so password saved in clearPassword
		this.name = name;
		this.firstName = firstName; 
		this.email = email;
		this.phone = phone;
		this.date = System.currentTimeMillis();
		this.rate = new UserRate();

		setKeys(new AsymKeysImpl(false, passWord));		// TODO false / true finally choice ?
	}
	
	/**
	 * Empty Constructor
	 */
	public User(){ 
		super(); 
		this.rate = new UserRate();
	}
	
	/**
	 * Construct a new user based on a XML, well and known formated string.
	 * @param XML
	 */
	public User(String XML) {
		super(XML);
	}

	/**
	 * Construct a new user based on an XML element
	 * @param u
	 */
	public User(Element u) {
		super(u);
	}
	
	@SuppressWarnings("rawtypes")
	public User(net.jxta.document.Element u) {
		super(u);
	}

	//////////////////////////////////////////////////// TOOLS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * Verifies that the password in parameter corresponds to hashPwd
	 * @param password
	 * @return
	 */
	public boolean isPassword(String password){
		try {
			String hash = Hasher.SHA256(password);
			return hash.equals(this.hashPwd);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/////////////////////////////////////////////////// GETTERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public String getNick() {
		return nick;
	}
	public String getHashPwd() {
		return hashPwd;
	}
	public String getName() {
		return name;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getEmail() {
		return email;
	}
	public String getPhone() {
		return phone;
	}
	public long getDate(){
		return date;
	}
	public UserRate getRate(){
		return rate;
	}
	public String getRateAsString(){
		return Float.toString(rate.getRapidity()) + "|" + Float.toString(rate.getConformity());
	}
	public String getClearPwd(){
		return clearPassword;
	}
	
	/////////////////////////////////////////////////// SETTERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public void setNick(String login) {
		this.nick = login == null ? "":login;
	}
	public void setPassWord(String passWord){
		this.hashPwd = Hasher.SHA256(passWord);
	}
	public void setClearPassword(String password) {
		this.clearPassword = password;
	}
	public void setHashPwd(String hashPwd) {
		this.hashPwd = hashPwd;
	}
	public void setName(String name) {
		this.name = name == null ? "": name;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName == null ? "": firstName;
	}
	public void setEmail(String email) {
		this.email = email == null ? "": email;
	}
	public void setPhone(String phone) {
		this.phone = phone == null ? "":phone;
	}
	public void setDate(long date){
		this.date = date == 0 ? System.currentTimeMillis() : date;
	}
	public void setRate(UserRate rate){
		this.rate = rate;
	}
	public void setRate(float rapidity, float conformity){
		this.rate = new UserRate(rapidity, conformity);
	}
	public void setRate(){
		this.rate = new UserRate();
	}
	public void setRateFromString(String rate){
		String s[] = rate.split ("|");
	
		this.rate = new UserRate(Float.parseFloat(s[0]), Float.parseFloat(s[1]));
	}
	public void setClearPwd(String password){
		this.clearPassword = password;
	}
	
	//////////////////////////////////////////////// ADVERTISEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * Give the good class to the constructor
	 */
	public static void register() {
		User u = new User();
		AdvertisementFactory.registerAdvertisementInstance(u.getAdvType(), new AdvertisementInstaciator(u));
	}
	
	/**
	 * Used to define Keys and initialize some values
	 */
	@Override
	protected void setKeys() {
		clearPassword = null;
		this.addKey("nick", true, true);
		this.addKey("hashPwd", false, true);
		this.addKey("name", false, true);
		this.addKey("firstName", false, true);
		this.addKey("email", false, true);
		this.addKey("phone", false, true);
		this.addKey("date", false, false);
		this.addKey("rate", true, true);
	}

	/**
	 * Used to add all keys
	 */
	@Override
	protected void putValues() {
		this.addValue("nick", this.getNick());
		this.addValue("hashPwd", this.getHashPwd());
		this.addValue("name", this.getName());
		this.addValue("firstName", this.getFirstName());
		this.addValue("email", this.getEmail());
		this.addValue("phone", this.getPhone());
		this.addValue("date", Long.toString(this.getDate()));
		this.addValue("rate", getRateAsString());
	}

	@Override
	protected String getAdvertisementName() {
		return this.getClass().getName();
	}

	@Override
	protected boolean handleElement(org.jdom2.Element e) {
		String val = e.getText();
		switch(e.getName()){
		case "nick":
			setNick(val);
			return true;
		case "hashPwd":
			setHashPwd(val);
			return true;
		case "name":
			setName(val);
			return true;
		case "firstName":
			setFirstName(val);
			return true;
		case "email":
			setEmail(val);
			return true;
		case "phone":
			setPhone(val);
			return true;
		case "date":
			setDate(Long.parseLong(val));
			return true;
		case "key":									// Not used for now, can be used if XML format is changed
			boolean all = true;
			for(Element f: e.getChildren()) {
				all &= handleElement(f);
			}
			return all;
		case "rate":
			setRateFromString(val);
			return true;
		default:
			return false;
		}
	}

	/////////////////////////////////////////////////// OVERRIDE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	@Override
	public int compareTo(User u) {
		if(this.equals(u))
			return 0;
		if(!this.getNick().equalsIgnoreCase(u.getNick()))
			return this.getNick().compareTo(u.getNick());
		return 0;
	}
	
	@Override
	public boolean equals(Object u) {
		if(! (u instanceof User))
			return false;
		User user = (User) u;
		if( (this.getKeys().getPublicKey() == null || user.getKeys().getPublicKey() == null)
				&& (this.getKeys().getPublicKey() != null || user.getKeys().getPublicKey() != null))
			return false;
		if((this.getKeys().getP() == null || user.getKeys().getP() == null)
				&& (this.getKeys().getP() != null || user.getKeys().getP() != null))
			return false;
		if((this.getKeys().getG() == null || user.getKeys().getG() == null)
				&& (this.getKeys().getG() != null || user.getKeys().getG() != null))
			return false;
		if(!(this.getKeys().getG() != null || user.getKeys().getG() != null) && 
				(this.getKeys().getPublicKey().compareTo(user.getKeys().getPublicKey()) != 0) ||
				!(this.getKeys().getP() != null || user.getKeys().getP() != null) && 
				(this.getKeys().getP().compareTo(user.getKeys().getP()) != 0) ||
				!(this.getKeys().getPublicKey() != null || user.getKeys().getPublicKey() != null) && 
				(this.getKeys().getG().compareTo(user.getKeys().getG()) != 0) &&
				this.getDate()==user.getDate())
			return false;
		return true;
	}
	
	@Override
	public User clone(){
		User u = new User();
		u.setName(this.getName());
		u.setDate(this.getDate());
		u.setEmail(this.getEmail());
		u.setFirstName(this.getFirstName());
		u.setKeys(this.getKeys());
		u.setNick(this.getNick());
		u.setPhone(this.getPhone());
		u.setHashPwd(this.getHashPwd());
		u.setRate(this.getRate());
		return u;
	}

	@Override
	public String getSimpleName() {
		return getClass().getSimpleName();
	}
	
	////////////////////////////////////////////////// MAIN FOR TEST \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static void main(String[] args){
		User user = new User("nick", "password", "name", "firstName", "email", "phone");
		user.getKeys().decryptPrivateKey("password");
		user.sign(user.getKeys());
		User user2 = new User(user.toString());
		System.out.println(user.getKeys().getPrivateKey());
		user2.getKeys().decryptPrivateKey("password");
		System.out.println(user2.getKeys().getPrivateKey());
		if(user.checkSignature(user.getKeys())) {
			System.out.println("Signature 1 ok");
		}
		if(user2.checkSignature(user2.getKeys())) {
			System.out.println("Signature 2 ok");
		}
	}
}
