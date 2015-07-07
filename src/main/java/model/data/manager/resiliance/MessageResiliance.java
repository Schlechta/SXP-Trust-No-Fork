package model.data.manager.resiliance;

import java.util.ArrayList;

import net.jxta.peer.PeerID;
import model.data.manager.Manager;
import model.data.user.User;
import model.data.user.UserMessage;
import model.network.communication.Communication;
import model.network.communication.service.MessageService;
import model.network.search.RandomPeerFinder;
import model.network.search.Search;
import model.network.search.SearchListener;

/**
 * Diffuse les messages en liste d'attente & cherche les messages qui sont destin�s a un des users.
 * @author Julien
 *
 */
public class MessageResiliance extends AbstractResiliance implements SearchListener<UserMessage> {
	
	
	
	public MessageResiliance(Manager m, Communication c) {
		super(m, c);
	}

	private void retrieveMessages() {
		Search<UserMessage> s = new Search<UserMessage>(manager.getNetwork().getGroup("messages").getDiscoveryService(),
				"receiverKey", true);
		s.addListener(this);
		for(User u : manager.getUserManager().getUsers()) {
			s.search(u.getKeys().getPublicKey().toString(16), 0, 0);
		}
		
	}
	
	private void sendMessages() {
		ArrayList<UserMessage> msgs = manager.getMessageManager().getMessages();
		
		Search<UserMessage> s = new Search<UserMessage>(manager.getNetwork().getGroup("messages").getDiscoveryService(),
				"keyId", true);
		
		for(UserMessage m : msgs) {
			m.publish(this.manager.getNetwork().getGroup("messages"));
			s.search(m.getId(), 3000, 5);
			if(s.getResults().size() < 5) {
				RandomPeerFinder rpf = new RandomPeerFinder(this.manager.getNetwork());
				rpf.findPeers(3000, 5 - s.getResults().size());
				com.sendMessage(m.toString(), MessageService.class.getSimpleName(), rpf.getResults().toArray(new PeerID[0]));
			}
		}
		
	}
	
	public void step() {
		retrieveMessages();
		sendMessages();
	}



	@Override
	public void searchEvent(UserMessage event) {
		if(event.getReceiver().getPublicKey().equals(manager.getUserManager().getCurrentUser().getKeys().getPublicKey())) {
			manager.getMessageManager().getCurrentUserConversations().addMessage(event);
		}
		else {
			manager.getMessageManager().addMessage(event);
		}
	}
}
