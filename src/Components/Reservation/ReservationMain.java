package Components.Reservation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Components.Course.CourseComponent;
import Components.Student.StudentComponent;
import Constants.Constants.ConstantsCourse;
import Constants.Constants.ConstantsException;
import Constants.Constants.ConstantsReservation;
import Constants.Constants.ConstantsStudent;
import Exceptions.NullDataException;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class ReservationMain {
	static ReservationComponent reservationsList;
	static StudentComponent studentsList = null;
	static CourseComponent coursesList = null;

	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, NullDataException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** Reservation Main(ID:" + componentId + ") is successfully registered. \n");

		reservationsList = new ReservationComponent("Reservations.txt");
		Event event = null;
		String[] messages = null;
		boolean done = false;
		while (!done) { // 1초마다 들어온 Event가 있는지 확인
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent(); // Queue에 쌓인 Event들 처리
				switch (event.getEventId()) {
				case ListReservations:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeReservationList(reservationsList)));
					break;
				case MakeReservations:
					printLogEvent("Get", event);
					messages = event.getMessage().split(" "); // StudentId, CourseId 저장
					break;
				case MakeReservationsCheckPre:
					printLogEvent("Get", event);
					MakeListsAndCheckReady(eventBus, event, messages);
					break;
				default:
					break;
				}
			}
		}
	}

	private static void MakeListsAndCheckReady(RMIEventBus eventBus, Event event, String[] messages) throws RemoteException, IOException {
		boolean isReady = makeLists(eventBus, event, messages);
		if(isReady) {
			checkPrerequisiteAndMakeReservation(eventBus, event, messages);
			studentsList = null;
			coursesList = null;
		}
	}
	private static boolean makeLists(RMIEventBus eventBus, Event event, String[] messages) throws RemoteException, IOException {
		if (event.getSender().equals("student")) {
			makeStudentsList(eventBus, event);
			if (coursesList != null) {
				return true;
			}
		} else if (event.getSender().equals("course")) {
			makeCoursesList(eventBus, event);
			if (studentsList != null) {
				return true;
			}
		}
		return false;
	}
	private static void makeCoursesList(RMIEventBus eventBus, Event event) throws RemoteException, IOException {
		if (event.getMessage().equals(ConstantsCourse.FAILUREBYNOTREGISTERED)) {
			studentsList = null;
			coursesList = null;
			eventBus.sendEvent(new Event(EventId.ClientOutput, event.getMessage()));
		} else {
			coursesList = new CourseComponent(event);
		}
	}
	private static void makeStudentsList(RMIEventBus eventBus, Event event) throws RemoteException, IOException {
		if (event.getMessage().equals(ConstantsStudent.FAILUREBYNOTREGISTERED)) {
			studentsList = null;
			coursesList = null;
			eventBus.sendEvent(new Event(EventId.ClientOutput, event.getMessage()));
		} else {
			studentsList = new StudentComponent(event);
		}
	}
	private static String makeReservationList(ReservationComponent reservationsList) throws NullDataException {
		if(reservationsList.vReservation.size() == 0) throw new NullDataException(ConstantsException.NULLDATAEXCEPTIONRESERVATION);
		String returnString = "";
		for (int j = 0; j < reservationsList.vReservation.size(); j++) {
			returnString += reservationsList.getReservationList().get(j).getString() + "\n";
		}
		return returnString;
	}
	private static void checkPrerequisiteAndMakeReservation(RMIEventBus eventBus, Event event, String[] messages) throws RemoteException, IOException {
		// 선수과목 체크
		if (studentsList.getStudent(messages[0]).checkprerequisiteCourses(coursesList.getCourse(messages[1]))) {
			// 처음 수강신청한 것인지 아닌지
			if (reservationsList.isRegisteredReservation(messages[0])) {
				// addReservatedCoursesList를 할 때, 이미 등록된 Reservation이라면 => Failed 
				if(reservationsList.getReservation(messages[0]).addReservatedCoursesList(messages[1])) {
					eventBus.sendEvent(new Event(EventId.ClientOutput, ConstantsReservation.SUCCESS));
				} else {
					eventBus.sendEvent(new Event(EventId.ClientOutput, ConstantsReservation.FAILED));
				}
			} else {
				Reservation reservation = new Reservation(messages[0] + " " + messages[1]);
				if(reservationsList.vReservation.add(reservation)) {
					eventBus.sendEvent(new Event(EventId.ClientOutput, ConstantsReservation.SUCCESS));
				} else {
					eventBus.sendEvent(new Event(EventId.ClientOutput, ConstantsReservation.FAILED));
				}
			}
			;
		} else {
			eventBus.sendEvent(new Event(EventId.ClientOutput, ConstantsReservation.FAILUREBYPREREQUISITE));
		}

	}
	private static void printLogEvent(String comment, Event event) {
		if(event.getSender() != null) {
			System.out.println(
					"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage()+" sender: " + event.getSender());
		} else {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
		}
	}
	
}
