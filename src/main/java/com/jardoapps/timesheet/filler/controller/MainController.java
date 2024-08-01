package com.jardoapps.timesheet.filler.controller;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.ParamInfo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class MainController implements Initializable {

	private final PluginConverter pluginConverter = new PluginConverter();

	private List<TimesheetFillerExtension> plugins;

	@FXML
	private ComboBox<TimesheetFillerExtension> loaderPluginsCmb;

	@FXML
	private VBox loaderParamsVbx;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadPlugins();
		initPluginComboboxes();
	}

	public void onLoaderSelected() {
		TimesheetFillerExtension selectedPlugin = loaderPluginsCmb.getSelectionModel().getSelectedItem();
		List<ParamInfo> paramInfos = selectedPlugin.getLoader().getParamInfos();
		createParamComponents(loaderParamsVbx, paramInfos, "loader-param-");
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

	private void createParamComponents(VBox vBox, List<ParamInfo> paramInfos, String idPrefix) {
		vBox.getChildren().clear();
		for (ParamInfo paramInfo : paramInfos) {
			Label label = new Label(paramInfo.getName() + ":");
			Node valueComponent = createParamValueComponent(paramInfo, idPrefix);
			vBox.getChildren().addAll(label, valueComponent);
		}
	}

	private Node createParamValueComponent(ParamInfo paramInfo, String idPrefix) {

		Node component = switch (paramInfo.getType()) {
			case STRING, NUMBER -> new TextField();
			case FILE -> new TextField(); // TODO: nicer component for files
			case DATE -> new DatePicker();
			case MULTILINE_STRING -> new TextArea();
			case SECRET_STRING -> new PasswordField();
			case BOOLEAN -> new CheckBox();
			default -> throw new IllegalArgumentException("Unsupported parameter type:" + paramInfo.getType());
		};

		component.setId(idPrefix + paramInfo.getId());
		return component;
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
