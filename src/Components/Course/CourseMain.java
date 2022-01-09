/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Course;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import Constants.Constants.ConstantsCourse;
import Constants.Constants.ConstantsException;
import Exceptions.NullDataException;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class CourseMain {
	public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException, InterruptedException, NullDataException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");

		CourseComponent coursesList = new CourseComponent("Courses.txt");
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case ListCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList(coursesList)));
					break;
				case RegisterCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerCourse(coursesList, event.getMessage())));
					break;
				case DeleteCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteCourse(coursesList, event.getMessage())));
					break;
				case MakeReservations:
					printLogEvent("Get", event);
					Thread.sleep(1000);
					eventBus.sendEvent(new Event(EventId.MakeReservationsCheckPre, listCoursesRegistered(coursesList, event), "course"));
					break;
				case QuitTheSystem:
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	// 등록되어 있는 Course라면, CoursesList를 반환 
	private static String listCoursesRegistered(CourseComponent coursesList, Event event) throws NullDataException {
		if (coursesList.isRegisteredCourse(event.getMessage().split(" ")[1])) {
			return makeCourseList(coursesList);
		} else {
			return ConstantsCourse.FAILUREBYNOTREGISTERED;
		}
	}
	private static String registerCourse(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			coursesList.vCourse.add(course);
			return ConstantsCourse.SUCCESSREGISTER;
		} else
			return ConstantsCourse.FAILEDREGISTER;
	}
	private static String makeCourseList(CourseComponent coursesList) throws NullDataException {
		if(coursesList.vCourse.size() == 0) throw new NullDataException(ConstantsException.NULLDATAEXCEPTIONCOURSE);
		String returnString = "";
		for (int j = 0; j < coursesList.vCourse.size(); j++) {
			returnString += coursesList.getCourseList().get(j).getString() + "\n";
		}
		return returnString;
	}
	private static String deleteCourse(CourseComponent coursesList, String message) {
		if(coursesList.isRegisteredCourse(message)) {
			if(coursesList.deleteCourse(message)) {
				return ConstantsCourse.SUCCESSDELETE;
			} else {
				return ConstantsCourse.FAILEDDELETE;
			}
		} else {
			return ConstantsCourse.FAILUREBYNOTREGISTERED;
		}
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
