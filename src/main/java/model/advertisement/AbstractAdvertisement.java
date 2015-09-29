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
package model.advertisement;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import util.Printer;
import util.StringToElement;
import util.VARIABLES;
import util.secure.AsymKeysImpl;
import util.secure.ElGamal;
import util.secure.ElGamalSign;
import model.network.NetworkInterface;
import model.network.communication.Communication;
import model.network.communication.service.update.UpdateMessage;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.Attributable;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;


/**
 * This class simplify the interface with Jxta to produce advertisement for
 * a class. This helps to produce a valid xml file for JXTA(datas and indexes) or for our own use.
 * There is a few abstract method to implements. You can use addKey and addValue method in setKeys and putvalues.
 * This abstract class give an auto signature method.
 * Each time you call a method that will call your putValues method, the lastUpdated field is updated.
 * @author Julien Prudhomme
 */
public abstract class AbstractAdvertisement extends Advertisement{

	private AbstractAdvertisement old = null; //for update. After each updates the last change are saved here.
	
	/*
	 * An hashMap that usually contain the key and value of this advertisement content, 
	 * for generating an XML file for JXTA or for saving datas.
	 */
	protected HashMap<String, String> keyValues = new HashMap<String, String> ();
	
	protected HashMap<String, Boolean> keyCanBeUpdated = new HashMap<String, Boolean>();
	
	/*
	 * An array list that contains the indexes for this advertisement. Indexes are used by JXTA for
	 * advertisements publication and search.
	 */
	protected ArrayList<String> indexes = new ArrayList<String>();
	
	private AsymKeysImpl keys = null;
	
	/*
	 * The Elgamal signature of this object.
	 */
	private ElGamalSign signature; 
	
	private String keyId;			// ID of the object
	
	/*
	 * Last time this object was modified
	 */
	private long lastUpdated;
	
	public long getLastUpdated(){
		return this.lastUpdated;
	}
	
	/**
	 * Instantiate an empty Advertisement, just setting the appropriate keys.
	 */
	public AbstractAdvertisement() {
		super();
		addKey("signature", false, true);
		addKey("lastUpdated", false, true);
		addKey("keyId", true, false);
		addKey("keys", false, false);
		addKey("superPublicKey", true, false);
		setKeys(); //setting the default keys and indexes for this advertisement.
		setId();
	}
	
	/**
	 * Instantiate and initialize this advertisement with the given root element.
	 * @param root A root element, that contain all data needed.
	 */
	public AbstractAdvertisement(Element root) {
		this();
		initialize(root);
	}
	
	
	/**
	 * Construct and initialize the class with a string in XML format.
	 * @param XML
	 */
	public AbstractAdvertisement(String XML) {
		this();
		SAXBuilder saxBuilder=new SAXBuilder();
        Reader stringReader=new StringReader(XML);
        Element root = null;
        try {
			root = saxBuilder.build(stringReader).getRootElement();
		} catch (JDOMException e) {
			System.err.println("Parse problem in " + this.getAdvType());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        initialize(root);
	}
	
	
	
	
	@SuppressWarnings("rawtypes")
	/**
	 * Instantiate and initialize this advertisement with the given root element given by Jxta.
	 * @param root the root element given by Jxta.
	 */
	public AbstractAdvertisement(net.jxta.document.Element root) {
		this();
		initialize(root);
	}
	
	/**
	 * Get the class.getSimpleName() of the child class
	 * @return
	 */
	public abstract String getSimpleName();
	
	
	/**
	 * Get all key and their values
	 * @return
	 */
	public HashMap<String, String> getKeysValues() {
		return this.keyValues;
	}
	
	public void setKeys(AsymKeysImpl keys) {
		if(keys == null) return;
		this.keys = keys.copy();
	}
	
	public AsymKeysImpl getKeys() {
		return this.keys;
	}
	
	/**
	 * Return the value according to the key. 
	 * @param key The element name.
	 * @return the element value.
	 */
	protected String getValue(String key) {
		if(this.keyValues.containsKey(key)) {
			return this.keyValues.get(key);
		}
		else throw new IllegalArgumentException("Key " + key + " inconnue");
	}
	
	/**
	 * Add a new key for this advertisement.
	 * @param key the new key
	 * @param isIndexed true if the key should be indexed for Jxta.
	 */
	protected void addKey(String key, boolean isIndexed, boolean canBeUpdated) {
		keyValues.put(key, null);
		if(isIndexed) {
			indexes.add(key);
		}
		keyCanBeUpdated.put(key, canBeUpdated);
	}
	
	protected void addValue(String key, String value) {
		if(!keyValues.containsKey(key)) {
			throw new IllegalArgumentException("unknown key");
		}
		keyValues.put(key, value);
	}
	
	
	/**
	 * Return the advertisement name. Used for creating the advertisement type.
	 * For example class.getName()
	 * @return A string that represent the advertisement
	 */
	protected abstract String getAdvertisementName();
	
	@Override
	public String getAdvType() {
		return "jxta:" + getAdvertisementName();
	}
	
	/**
	 * Contruct the root element according to the advertisement name and they key's value.
	 * @return an element.
	 */
	public Element getRootElement() {
		superPutValues();
		Element root = new Element(getAdvertisementName());
		ArrayList<String> sorted = new ArrayList<>();
		for(String key: keyValues.keySet()) {
			sorted.add(key);
		}
		Collections.sort(sorted);
		
		for(String key: sorted) { //creating an element for each keys. Add to root content.
			Element e = new Element(key);
			e.addContent(keyValues.get(key));
			root.addContent(e);
		}
		return root;
	}
	
	/**
	 * Return a simple document for saving or manipulating the advertisement DOM.
	 * @return a Jdom2 Document.
	 */
	public org.jdom2.Document getDocument() {
		return new org.jdom2.Document(this.getRootElement());
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Document getDocument(MimeMediaType mediatype) {
		superPutValues();
		StructuredDocument adv = StructuredDocumentFactory.newStructuredDocument(mediatype, getAdvType());
		
		if (adv instanceof Attributable) {
            ((Attributable) adv).addAttribute("xmlns:jxta", "http://jxta.org");
        }
		
		for (String key : keyValues.keySet()) {
			net.jxta.document.Element e = adv.createElement(key,keyValues.get(key));
			adv.appendChild(e);
		}
		return adv;
	}
	
	@Override
	public String[] getIndexFields() {
		return (String[]) indexes.toArray(new String[1]);
	}
	
	/**
	 * Will be called on new instances to set the advertisement's available elements.
	 * You should also initialize the data here.
	 */
	protected abstract void setKeys();
	
	/**
	 * Will be called to put the class properties in the xml document.
	 */
	protected abstract void putValues();
	
	private void superPutValues() {
		addValue("signature", signature == null ? null:signature.toString());
		addValue("lastUpdated", Long.toString(lastUpdated));
		addValue("keyId", keyId);
		if(keys != null) {
			addValue("superPublicKey", keys.getPublicKey().toString(16));
			addValue("keys",keys.toString());
		}
		putValues();
	}
	
	/**
	 * Initialize this advertisement with the root element handle by JXTA.
	 * @param root
	 */
	@SuppressWarnings("rawtypes")
	protected void initialize(net.jxta.document.Element root) {
		if (!TextElement.class.isInstance(root)) {
            throw new IllegalArgumentException(getClass().getName() + "initialize needs a TextElement.");
        }
		
		TextElement doc = (TextElement) root; //create the doc.
		if (!doc.getName().equals(getAdvType())) {
            throw new IllegalArgumentException(
            		"Error : " + getClass().getName() + " isn't " + doc.getName());
            //Jxta didn't call the right advertisement to construct. Unexpected error.
        }
		
		Enumeration elements = doc.getChildren();
        while(elements.hasMoreElements()) {
        	TextElement elem = (TextElement) elements.nextElement();
        	Element e = new Element(elem.getName()); //convert into a Jdom element.
        	e.addContent(elem.getValue());
        	if (!superHandleElement(e)) {
                throw new IllegalDataException(elem.getName());
                //this element is unknown for this advertisement.
            }
        }
	}
	
	/**
	 * Initialize this advertisement with a Jdom root element
	 * @param root 
	 */
	protected void initialize(Element root) {
		for(Element e: root.getChildren()) {
			if(!superHandleElement(e)) {
				throw new IllegalDataException(e.getName());
                //this element is unknown for this advertisement.
			}
		}
	}
	
	/**
	 * Called on each element find in the root element given by Jxta or user.
	 * @param an element
	 * @return true if the element is known and handled. Otherwise false.
	 */
	abstract protected boolean handleElement(Element e);
	
	/**
	 * Set the signature with the handled value
	 * @param signature
	 */
	private boolean setSignature(String signature) {
		Element sign = StringToElement.getElementFromString(signature, "signs");
		
		if(sign.getChild("signR") == null) return false;
		BigInteger r = new BigInteger(sign.getChild("signR").getValue(), 16);
		if(sign.getChild("signS") == null) return false;
		BigInteger s = new BigInteger(sign.getChild("signS").getValue(), 16);
		this.signature = new ElGamalSign(r, s);
		return true;
	}
	
	public String getSignature() {
		return signature.toString();
	}
	
	private boolean superHandleElement(Element e) {
		switch(e.getName()) {
		case "signature": setSignature(e.getValue()); return true;
		case "lastUpdated": lastUpdated = new Long(e.getValue()); return true;
		case "keyId": keyId = e.getValue(); return true;
		case "keys": 
			if(e.getValue().isEmpty()) { keys = null; return true; }
			keys = new AsymKeysImpl(e.getValue()); 
			return true;
		case "superPublicKey": return true;
		default: return handleElement(e);
		}
	}
	
	@Override
	public ID getID() {
		return null;
	}
	
	
	@Override
	/**
	 * Return a string, XML-Formatted, representing this instance.
	 */
	public String toString() {
		org.jdom2.Element document = this.getRootElement();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String xmlString = outputter.outputString(document);
		return xmlString;
	}
	
	public String getDocumentString() {
		org.jdom2.Document document = this.getDocument();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String xmlString = outputter.outputString(document);
		return xmlString;
	}
	
	
	/**
	 * Get the concatenated value of all the elements (except signature itself) sorted in alpha-order
	 * Used to generate a hash of the advertisement.
	 * @return a string that represent all the elements values concatenated
	 */
	public String getConcatenedElements() {
		superPutValues();
		ArrayList<String> sortedElements = new ArrayList<String>();
		for(String s: keyValues.keySet()) {
			if(!s.equals("signature") && keyValues.get(s) != null && !keyValues.get(s).isEmpty()) {
				String value = s + ":" + keyValues.get(s) + "\n";
				sortedElements.add(value);
			}
		}
		Collections.sort(sortedElements);
		StringBuffer s = new StringBuffer();
		for(String c : sortedElements) {
			s.append(c);
		}
		return s.toString();
	}
	
	/**
	 * generate and save this advertisement signature.
	 * @param keys - The AsymKeysImp that contain a private key.
	 */
	public ElGamalSign sign(AsymKeysImpl keys) {
		ElGamal crypter = new ElGamal(keys);
		lastUpdated = System.currentTimeMillis();
		signature = crypter.getMessageSignature(getConcatenedElements().getBytes());
		if(signature == null) System.err.println(this.getAdvertisementName()+" : Signature null");
		return signature;
	}
	
	/**
	 * Check the signature of this object according to a public key.
	 * @param keys - An AsymKeysImpl object that contain a public key.
	 * @return true if ok, else false
	 */
	public boolean checkSignature(AsymKeysImpl keys) {
		if(signature == null) return false;
		ElGamal crypter = new ElGamal(keys);
		return crypter.verifySignature(getConcatenedElements().getBytes(), signature);
	}
	
	

	public boolean checkUpdateMessage(Element root) {
		return !(
					root.getChild("lastUpdated")  == null ||
					root.getChild("signature")    == null || //signature of update message
					root.getChild("newSignature") == null ||
					root.getChild("asymKeys")     == null
				);
				
	}
	
	/**
	 * Update the Advertisement if lastUpdated in the root element is superior and if the signature is correct.
	 * @param root
	 */
	public void receiveUpdateMessage(Element root) {
		
		if(!checkUpdateMessage(root)) return; //Update message incorrect
	}
	
	/**
	 * Throw an update message to the network.
	 * @param com
	 */
	@SuppressWarnings("unchecked")
	public void throwUpdate(Communication com, AsymKeysImpl emmitter) {
		UpdateMessage update = new UpdateMessage(this, emmitter);
		com.getService("updateService").sendMessage(update, (PeerID[])null);
		old = this.clone(); //keeping current object state for future update computation.
		System.out.println(update);
		
	}
	
	public AbstractAdvertisement getOld() {
		return old;
	}
	
	/**
	 * Get the hashmap of the updatable keys.
	 * @return
	 */
	public HashMap<String, String> getUpdatableKeys() {
		superPutValues();
		HashMap<String, String> updatable = new HashMap<String, String>();
		for(String key : this.keyValues.keySet()) {
			if(keyCanBeUpdated.get(key)) {
				updatable.put(key, keyValues.get(key));
			}
		}
		return updatable;
	}
	
	public String getId() {
		return keyId;
	}
	
	private void setId() {
		keyId = this.getAdvType() + ":" + UUID.randomUUID() + UUID.randomUUID();
	}
	
	public void setId(String id) {
		this.keyId = id;
	}
	
	/**
	 * Clone the Abstract advertisement (only with declared fields in setKeys)
	 * For example, for the User class that extends abstractAdvertisement, 
	 * user.clone() is the same result that new User(user.toString())
	 */
	public AbstractAdvertisement clone() {
		try {
			return this.getClass().getConstructor(String.class).newInstance(this.toString());
			//we retrieve the right AbstractAdvertisement child that invoke this method
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Publish an object on his PeerGroup
	 * @param NetworkInterface the current network.
	 */
	public void publish(NetworkInterface n) {
		IdAdvertisement idAdv = new IdAdvertisement(this);
		try {
			System.out.println("Publish dans le group " + getSimpleName());
			if(n.getGroup(getSimpleName()) == null) {
				Printer.printError(this, "publish", getSimpleName() + " group doesn't exist");
				//this error mean you forgot to create the right group for this advertisement.
			}
			DiscoveryService service = n.getGroup(getSimpleName()).getDiscoveryService();
			DiscoveryService idService = n.getGroup("id-" + getSimpleName()).getDiscoveryService();
			service.publish(this);
			idService.publish(idAdv);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
