/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Student;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Constants.Constants.ConstantsException;
import Constants.Constants.ConstantsStudent;
import Exceptions.NullDataException;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class StudentMain {
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, InterruptedException, NullDataException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");

		StudentComponent studentsList = new StudentComponent("Students.txt");
		Event event = null;
		boolean done = false;
		while (!done) { // 1초마다 들어온 Event가 있는지 확인
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent(); // Queue에 쌓인 Event들 처리
				switch (event.getEventId()) {
				case ListStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList(studentsList)));																	// Message 전달
					break;
				case RegisterStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerStudent(studentsList, event.getMessage())));
					break;
				case DeleteStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteStudent(studentsList, event.getMessage())));
					break;
				case MakeReservations:
					printLogEvent("Get", event);
					Thread.sleep(2000);
					eventBus.sendEvent(new Event(EventId.MakeReservationsCheckPre, listStudentsRegistered(studentsList, event), "student"));
					break;
				case QuitTheSystem:
					printLogEvent("Get", event);
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	// 등록되어 있는 Student라면, StudentsList를 반환 
	private static String listStudentsRegistered(StudentComponent studentsList, Event event) throws NullDataException {
		if (studentsList.isRegisteredStudent(event.getMessage().split(" ")[0])) {
			return makeStudentList(studentsList);
		} else {
			return ConstantsStudent.FAILUREBYNOTREGISTERED;
		}
	}
	private static String registerStudent(StudentComponent studentsList, String message) {
		Student student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			studentsList.vStudent.add(student);
			return ConstantsStudent.SUCCESSREGISTER;
		} else
			return ConstantsStudent.FAILEDREGISTER;
	}

	private static String makeStudentList(StudentComponent studentsList) throws NullDataException {
		if(studentsList.vStudent.size() == 0) throw new NullDataException(ConstantsException.NULLDATAEXCEPTIONSTUDENT);
		String returnString = "";
		for (int j = 0; j < studentsList.vStudent.size(); j++) {
			returnString += studentsList.getStudentList().get(j).getString() + "\n";
		}
		return returnString;
	}

	private static String deleteStudent(StudentComponent studentsList, String message) {
		if (studentsList.isRegisteredStudent(message)) {
			if (studentsList.deleteStudent(message)) {
				return ConstantsStudent.SUCCESSDELETE;
			} else {
				return ConstantsStudent.FAILEDDELETE;
			}
		} else {
			return ConstantsStudent.FAILUREBYNOTREGISTERED;
		}
	}

	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
