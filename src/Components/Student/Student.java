/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Student;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Components.Course.Course;

public class Student {
	protected String studentId;
	protected String name;
	protected String department;
	protected ArrayList<String> completedCoursesList = new ArrayList<String>();;

	public Student(String inputString) {
		StringTokenizer stringTokenizer = new StringTokenizer(inputString);
		this.studentId = stringTokenizer.nextToken();
		this.name = stringTokenizer.nextToken();
		this.name = this.name + " " + stringTokenizer.nextToken();
		this.department = stringTokenizer.nextToken();
		while (stringTokenizer.hasMoreTokens()) {
			this.completedCoursesList.add(stringTokenizer.nextToken());
		}
	}
	public boolean match(String studentId) {
		return this.studentId.equals(studentId);
	}
	public String getName() {
		return this.name;
	}
	public ArrayList<String> getCompletedCourses() {
		return this.completedCoursesList;
	}
	public String getString() {
		String stringReturn = this.studentId + " " + this.name + " " + this.department;
		for (int i = 0; i < this.completedCoursesList.size(); i++) 
			stringReturn += " " + this.completedCoursesList.get(i).toString();
		return stringReturn;
	}
	public boolean checkprerequisiteCourses(Course course) {
		ArrayList<String> prerequisiteCoursesList = course.getPrerequisiteCoursesList();
		for(int i=0; i<prerequisiteCoursesList.size(); i++) {
			boolean is = false;
			for(int j=0; j<completedCoursesList.size(); j++) {
				if(prerequisiteCoursesList.get(i).equals(completedCoursesList.get(j))) {
					is = true;
				}
			}
			if(is == false) {
				return false;
			}
		}
		return true;
	}
}
