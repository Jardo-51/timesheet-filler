package com.jardoapps.timesheet.filler.controller;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class MainController implements Initializable {

	private final PluginConverter pluginConverter = new PluginConverter();

	private List<TimesheetFillerExtension> plugins;

	@FXML
	private ComboBox<TimesheetFillerExtension> loaderPluginsCmb;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadPlugins();
		initPluginComboboxes();
	}

	private void loadPlugins() {

		Path pluginPath = Paths.get(System.getProperty("user.home"), ".timesheet-filler", "plugins");
		PluginManager pluginManager = new DefaultPluginManager(pluginPath);
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		plugins = pluginManager.getExtensions(TimesheetFillerExtension.class);
	}

	private void initPluginComboboxes() {

		plugins.stream().filter(TimesheetFillerExtension::supportsLoading).forEach(plugin -> {
			loaderPluginsCmb.getItems().add(plugin);
		});
		loaderPluginsCmb.setConverter(pluginConverter);
	}

	private static final class PluginConverter extends StringConverter<TimesheetFillerExtension> {

		@Override
		public String toString(TimesheetFillerExtension plugin) {
			if (plugin == null) {
				return null;
			}
			return plugin.getName();
		}

		@Override
		public TimesheetFillerExtension fromString(String string) {
			return null;
		}

	}
}
