module TimesheetFiller {

	requires org.pf4j;

	requires com.jardoapps.timesheet.plugin.api;

	requires javafx.controls;

	opens com.jardoapps.timesheet.filler to javafx.graphics;

	exports com.jardoapps.timesheet.filler;
}
