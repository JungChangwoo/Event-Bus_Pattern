package Components.Reservation;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Reservation {
	protected String studentId;
	protected ArrayList<String> reservatedCoursesList = new ArrayList<String>();;

	public Reservation(String inputString) {
		StringTokenizer stringTokenizer = new StringTokenizer(inputString);
		this.studentId = stringTokenizer.nextToken();
		while (stringTokenizer.hasMoreTokens()) {
			this.reservatedCoursesList.add(stringTokenizer.nextToken());
		}
	}
	public boolean match(String studentId) {
		return this.studentId.equals(studentId);
	}
	public ArrayList<String> getReservatedCoursesList() {
		return this.reservatedCoursesList;
	}
	public boolean addReservatedCoursesList(String courseId) {
		if(registeredReservation(courseId)) return false;
		this.reservatedCoursesList.add(courseId);
		return true;
	}
	private boolean registeredReservation(String courseId) {
		for(int i=0; i<this.reservatedCoursesList.size(); i++) {
			if(this.reservatedCoursesList.get(i).equals(courseId))
				return true;
		}
		return false;
	}
	public String getString() {
		String stringReturn = this.studentId;
		for (int i = 0; i < this.reservatedCoursesList.size(); i++) 
			stringReturn += " " + this.reservatedCoursesList.get(i).toString();
		return stringReturn;
	}
}
