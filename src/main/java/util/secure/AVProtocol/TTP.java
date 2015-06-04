package util.secure.AVProtocol;

import java.util.ArrayList;

/**
 * set TTP
 * number n paticipant, k min, set participant 
 * @author sarah
 *
 */

public class TTP {

	private int n ;
	private int k ;
	private ArrayList<ParticipantEx> Participants = new ArrayList <ParticipantEx>();
	
	public int getN() {
		return n;
	}
	
	public void setN(int n) {
		this.n = n;
	}
	
	public int getK() {
		return k;
	}
	
	public void setK(int k) {
		this.k = k;
	}
	
	public ArrayList<ParticipantEx> getParticipants() {
		return Participants;
	}
	
	public ParticipantEx getParticipant( int index) {
		return Participants.get(index);
	}
	
	public void setParticipants(ArrayList<ParticipantEx> Participants) {
		Participants = Participants;
	}
	
	public void addParticipant(ParticipantEx Participant) {
		Participants.add(Participant);
	}
	
}