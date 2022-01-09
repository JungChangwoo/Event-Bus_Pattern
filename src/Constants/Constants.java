package Constants;

public class Constants {
	public class ConstantsCourse{
		public static final String SUCCESSREGISTER = "This course is successfully added.";
		public static final String FAILEDREGISTER = "This course is already registered.";
		public static final String SUCCESSDELETE = "This course is successfully deleted.";
		public static final String FAILEDDELETE= "The course deletion is failed.";
		public static final String FAILUREBYNOTREGISTERED= "This course is not registered.";
	}
	public class ConstantsStudent{
		public static final String SUCCESSREGISTER = "This student is successfully added.";
		public static final String FAILEDREGISTER = "This student is already registered.";
		public static final String SUCCESSDELETE = "This student is successfully deleted.";
		public static final String FAILEDDELETE= "The student deletion is failed.";
		public static final String FAILUREBYNOTREGISTERED= "This student is not registered.";
	}
	public class ConstantsReservation{
		public static final String SUCCESS = "This Reservation is successfully added.";
		public static final String FAILED = "This Reservation is already registered";
		public static final String FAILUREBYPREREQUISITE = "This Student is not completed prerequisiteCourses";
	}
	public class ConstantsException{
		public static final String NULLDATAEXCEPTIONRESERVATION = "=========== The Reservation Data is Null ===========";
		public static final String NULLDATAEXCEPTIONSTUDENT = "=========== The Student Data is Null ===========";
		public static final String NULLDATAEXCEPTIONCOURSE = "=========== The Course Data is Null ===========";
	}
}
