package com.jardoapps.timesheet.plugin.stt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;
import com.jardoapps.timesheet.plugin.api.testing.TestingPluginManager;

public class SimpleTimeTrackerPluginIT {

	/**
	 * Verify that the built plugins can be loaded correctly.
	 */
	@Test
	void testPluginLoad() {

		PluginManager pluginManager = new TestingPluginManager(Paths.get("target"));
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		List<String> pluginNames = pluginManager.getExtensions(TimesheetFillerExtension.class)
				.stream()
				.map(TimesheetFillerExtension::getName)
				.toList();

		assertThat(pluginNames).containsExactlyInAnyOrder("Simple Time Tracker App");
	}

}
