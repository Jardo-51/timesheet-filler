package com.jardoapps.timesheet.filler;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.RecordLoader;
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

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginPath)) {
			for (Path entry : stream) {
				System.out.println(entry.getFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		PluginManager pluginManager = new DefaultPluginManager(pluginPath);
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		List<TimesheetFillerExtension> plugins = pluginManager.getExtensions(TimesheetFillerExtension.class);
		System.out.println("Plugins: " + plugins.size());

		plugins.forEach(plugin -> {
			System.out.println(plugin.getName());
		});

		primaryStage.setTitle("Timesheet Filler");
		Scene scene = new Scene(new BorderPane(), 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		doProcessing(plugins);
	}

	private void doProcessing(List<TimesheetFillerExtension> plugins) throws Exception {

		RecordLoader loader = plugins.stream()
				.filter(plugin -> plugin.getName().equals("Simple Task Tracker App"))
				.findFirst()
				.get()
				.getLoader();

		List<TimesheetRecord> records = loader.loadRecords(Map.of("filePath", "/tmp/stt_records.csv"));
		records.forEach(System.out::println);
	}
}
