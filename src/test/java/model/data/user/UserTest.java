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

import static org.junit.Assert.*;

import java.math.BigInteger;

import model.data.user.User;

import org.junit.Test;

import util.Hasher;
import util.secure.AsymKeysImpl;

/**
 * Test for User Class
 * @author Michael
 *
 */
public class UserTest {
	private static String nick = "nick";
	private static String password = "password";
	private static String hashPassword = Hasher.SHA256(password);
	private static String name = "name";
	private static String firstName = "firstName";
	private static String email = "email";
	private static String phone = "phone";
	private static long date = System.currentTimeMillis();
	private static AsymKeysImpl key = new AsymKeysImpl(password);
	private static UserRate rate = new (3,4);
	
	@Test
	public void password(){
		User user1 = new User(nick, password, name, firstName, email, phone);
		assertEquals(hashPassword, user1.getHashPwd());
		assertEquals(password, user1.getClearPwd());
		
		// Expected ClearPassword null in user2 construct with XML 
		User user2 = new User(user1.toString());
		assertEquals(hashPassword, user2.getHashPwd());
		assertNull(user2.getClearPwd());
	}
	
	@Test
	public void constructors(){
		User user1,user2,user3;
		
		// Two type of constructor
		user1 = new User(nick, password, name, firstName, email, phone);
		user1.setKeys(key);
		user2 = new User(user1.toString());
		user3 = new User(user2.toString());
		
		assertTrue(user1.equals(user2));
		assertTrue(user2.equals(user3));
		assertTrue(user3.equals(user1));
		
		// Compare with empty key and not empty key (different expected)
		user1.setKeys(null);
		assertFalse(user1.equals(user2));
		assertFalse(user1.equals(user3));
		
		// Compare two user with empty key (equals expected)
		user2.setKeys(null);
		assertTrue(user1.equals(user2));
		
		// Compare empty constructor with user with empty key (equals expected)
		user1 = new User();
		assertTrue(user1.equals(user2));
		
		// Compare empty constructor with user with not empty key (different expected)
		assertFalse(user1.equals(user3));
	}
	
	@Test
	public void isPassword(){
		User user;
		user = new User(nick, password, name, firstName, email, phone);
		user.setDate(date);
		assertTrue(user.isPassword(password));
		assertFalse(user.isPassword(password+"#"));
		
		user = new User(nick, password+"#", name, firstName, email, phone);
		user.setDate(date);
		assertFalse(user.isPassword(password));
		assertTrue(user.isPassword(password+"#"));
	}
	
	@Test
	public void cryptPrivate(){
		User user1;
		user1 = new User(nick, password, name, firstName, email, phone);
		BigInteger truePrivate = user1.getKeys().getPrivateKey();
		user1.getKeys().encryptPrivateKey(password);
		
		User user2 = new User(user1.toString());
		user2.getKeys().decryptPrivateKey(password);
		assertEquals(truePrivate, user2.getKeys().getPrivateKey());
	}
}
