package model.network.communication.service;

import model.manager.Manager;
import net.jxta.endpoint.Message;
import net.jxta.peer.PeerID;

public class TransmitAccountService extends Service<Manager> {

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	
	
	
	@Override
	public Manager handleMessage(Message m) {
		Manager manager = new Manager(new String(m.getMessageElement("content").getBytes(true)), null);
		//TODO publish des donnees recue
		return manager;
	}

	@Override
	/**
	 * Transmit accounts data to others peers.
	 */
	public void sendMessage(Manager m, PeerID ...ids) {
		/* TODO chercher X peer et leur envoyer le manager */
		Message message = new Message();
		sender.sendMessage(m.toString(),this.getServiceName(), ids);
	}

}