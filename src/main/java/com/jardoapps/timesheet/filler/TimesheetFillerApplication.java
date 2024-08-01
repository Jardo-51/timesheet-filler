package com.jardoapps.timesheet.filler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.RecordLoader;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.RecordSaver;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.RecordTransformer;
import com.jardoapps.timesheet.plugin.api.TimesheetRecord;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TimesheetFillerApplication extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Path pluginPath = Paths.get("/tmp/plugins");
		PluginManager pluginManager = new DefaultPluginManager(pluginPath);
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		primaryStage.setTitle("Timesheet Filler");
		Scene scene = new Scene(new BorderPane(), 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		List<TimesheetFillerExtension> plugins = pluginManager.getExtensions(TimesheetFillerExtension.class);
		doProcessing(plugins);
	}

	private void doProcessing(List<TimesheetFillerExtension> plugins) throws Exception {

		RecordLoader loader = plugins.stream()
				.filter(plugin -> plugin.getName().equals("Simple Task Tracker App"))
				.findFirst()
				.get()
				.getLoader();

		List<TimesheetRecord> records = loader.loadRecords(Map.of("filePath", "/tmp/stt_records.csv"));

		RecordTransformer transformer = plugins.stream()
				.filter(plugin -> plugin.getName().equals("Adastra"))
				.findFirst()
				.get()
				.getTransformer();

		records = transformer.transformRecords(records, Map.of());

		RecordSaver saver = plugins.stream()
				.filter(plugin -> plugin.getName().equals("Adastra"))
				.findFirst()
				.get()
				.getSaver();

		Map<String, String> params = Map.of("cookie", "dummy");
		saver.saveRecords(records, params);
	}
}
