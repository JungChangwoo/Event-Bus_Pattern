package Components.Reservation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReservationComponent {
	protected ArrayList<Reservation> vReservation;
	
	public ReservationComponent(String sReservationFileName) throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sReservationFileName));
		this.vReservation = new ArrayList<Reservation>();
		while (bufferedReader.ready()) {
			String reservationInfo = bufferedReader.readLine();
			if (!reservationInfo.equals("")) this.vReservation.add(new Reservation(reservationInfo));
		}
		bufferedReader.close();
	}
	public ArrayList<Reservation> getReservationList() {
		return vReservation;
	}
	public void setReservation(ArrayList<Reservation> vReservation) {
		this.vReservation = vReservation;
	}
	public Reservation getReservation(String stuId) {
		for(int i=0; i<vReservation.size(); i++) {
			if(vReservation.get(i).match(stuId)) {
				return vReservation.get(i);
			}
		}
		return null;
	}
	public boolean isRegisteredReservation(String sSID) {
		for (int i = 0; i < this.vReservation.size(); i++) {
			if (((Reservation) this.vReservation.get(i)).match(sSID)) return true;
		}
		return false;
	}
	
}
