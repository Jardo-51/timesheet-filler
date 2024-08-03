package com.jardoapps.timesheet.plugin.stt;

import org.pf4j.Extension;
import org.pf4j.Plugin;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;

public class SimpleTimeTrackerPlugin extends Plugin {

	@Extension
	public static class SttExtension implements TimesheetFillerExtension {

		@Override
		public boolean supportsLoading() {
			return true;
		}

		@Override
		public boolean supportsTransforming() {
			return false;
		}

		@Override
		public boolean supportsSaving() {
			return false;
		}

		@Override
		public String getName() {
			return "Simple Time Tracker App";
		}

		@Override
		public String getDescription() {
			return "Loads records exported from Simple Time Tracker application.";
		}

		@Override
		public RecordLoader getLoader() {
			return new SttRecordLoader();
		}

		@Override
		public RecordTransformer getTransformer() {
			throw new UnsupportedOperationException("SimpleTimeTrackerPlugin does not support transforming.");
		}

		@Override
		public RecordSaver getSaver() {
			throw new UnsupportedOperationException("SimpleTimeTrackerPlugin does not support saving.");
		}

	}

}
