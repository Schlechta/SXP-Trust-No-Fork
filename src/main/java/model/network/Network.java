package model.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;

public class Network implements NetworkInterface {
	
	private PeerID peerID;
	private NetworkManager networkManager;
	private HashMap<String, PeerGroup> peergroups = new HashMap<String, PeerGroup> ();
	private PeerGroup defaultGroup;
	
	
	/**
	 * Create a new P2P network, setting the port and the 
	 * folder where Jxta store the configuration and his cache.
	 * Define the peer name on the network
	 * @param port Port used by Jxta network
	 * @param folder Folder where Jxta store it need.
	 * @param peerName Peer name on the network
	 */
	public Network(int port, String folder, String peerName) {
		peerID = generatePeerID(peerName); /* Generating an unique PeerID */
		File configFile = new File("." + System.getProperty("file.separator") + peerName); /* Used by the networkManager */
		networkManager = networkManagerSetup(configFile, port, peerName);
	}

	@Override
	public PeerGroup getGroup(String group) {
		return this.peergroups.get(group);
	}

	@Override
	public void addGroup(String name) {
		ModuleImplAdvertisement mAdv = null;
		PeerGroup group = null;
		try {
			mAdv = defaultGroup.getAllPurposePeerGroupImplAdvertisement(); /* Getting the advertisement of implemented modules */
			group = defaultGroup.newGroup(generatePeerGroupID(name), mAdv, name, name); /* creating & publishing the group */
		} catch (Exception e) {
			e.printStackTrace();
		}
		group.startApp(new String[0]);
		peergroups.put(name, group);
	}

	@Override
	public void start() {
		try {
			defaultGroup = networkManager.startNetwork(); /* Starting the network and JXTA's infrastructure. */
		} catch (PeerGroupException | IOException e) {
			e.printStackTrace();
		}
		defaultGroup.getRendezVousService().setAutoStart(true, 60*1000); /* Switching to RendezVousMode if needed. Check every 60s */
		
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Generate an unique Peer ID from the peer name.
	 * @param peerName A string, generally the peer name, from the PeerID will be generated.
	 * @return the newly generated PeerID
	 */
	private PeerID generatePeerID(String peerName) {
		return IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, peerName.getBytes());
	}
	
	/**
	 * Generate an unique PeerGroup ID from the peer group name
	 * @param peerGroupName A string, generally the peer name, from the PeerID will be generated.
	 * @return the newly generated PeerID
	 */
	private PeerGroupID generatePeerGroupID(String peerGroupName) {
		return IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, peerGroupName.getBytes());
	}
	
	
	/**
	 * Setup the networkManager that will store data in configFile folder.
	 * @param configFile The file where the network manager will put or retrieve datas.
	 * @param port The port used by JXTA to communicate.
	 * @param peerName The new future peer name.
	 * @return
	 */
	private NetworkManager networkManagerSetup(File configFile, int port, String peerName) {
		NetworkManager manager = null;
		NetworkConfigurator configurator = null;
		try {
			manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, peerName, configFile.toURI()); /* Setting network */
			configurator = manager.getConfigurator(); /* Getting configurator for future tweaks */
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		/* Configuration settings */
		 configurator.setTcpPort(port);
         configurator.setTcpEnabled(true);
         configurator.setTcpIncoming(true);
         configurator.setTcpOutgoing(true);
         configurator.setUseMulticast(true);
         configurator.setPeerID(peerID);
         /*configurator.setTcpPublicAddress(IpChecker.getIp(), false); TODO set public adress to make Jxta works on internet */
         try {
			configurator.setTcpInterfaceAddress(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
         configurator.setTcpEndPort(-1);
         configurator.setTcpStartPort(-1);
		
		return manager;
	}
	
	/**
	 * Set the JXTA's logger's level
	 * @param level
	 */
	public void setLogger(Level level) {
		Logger.getLogger("net.jxta").setLevel(level);
	}
	
	public static void main(String[] args) {
		Network network = new Network(9703, ".peerTemp", "Julien");
		network.setLogger(Level.SEVERE);
		network.start();
		network.addGroup("objets");
	}
}