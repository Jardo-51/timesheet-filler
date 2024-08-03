package com.jardoapps.timesheet.plugin.stt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.ParamInfo;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.ParamType;
import com.jardoapps.timesheet.plugin.api.TimesheetFillerExtension.RecordLoader;
import com.jardoapps.timesheet.plugin.api.TimesheetRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SttRecordLoader implements RecordLoader {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final int EXPECTED_COLUMN_COUNT = 8;

	public static final String FILE_PATH_PARAM = "filePath";

	@Override
	public List<ParamInfo> getParamInfos() {

		ParamInfo fileParam = ParamInfo.builder()
				.id(FILE_PATH_PARAM)
				.type(ParamType.FILE)
				.name("Input file")
				.description("Path to the file containing the records exported from Simple Task Tracker.")
				.build();

		return List.of(fileParam);
	}

	@Override
	public List<TimesheetRecord> loadRecords(Map<String, String> params) throws Exception {

		String filePath = params.get(FILE_PATH_PARAM);
		log.info("Loading records from file: '{}'", filePath);

		List<TimesheetRecord> records = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip header line
			while ((line = reader.readLine()) != null) {
				List<String> values = splitCsvValues(line);
				records.add(createRecord(values));
			}
		}

		return records;
	}

	private List<String> splitCsvValues(String line) {

		List<String> values = new ArrayList<>(EXPECTED_COLUMN_COUNT);
		int startIndex = 0;
		boolean insideQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '"') {
				insideQuotes = !insideQuotes;
			} else if (c == ',' && !insideQuotes) {
				values.add(line.substring(startIndex, i));
				startIndex = i + 1;
			}
		}

		// Add the last value
		values.add(line.substring(startIndex));

		return values;
	}

	private TimesheetRecord createRecord(List<String> values) {
		return TimesheetRecord.builder()
				.project(values.get(0))
				.timeStarted(LocalDateTime.parse(values.get(1), DATE_TIME_FORMATTER))
				.timeEnded(LocalDateTime.parse(values.get(2), DATE_TIME_FORMATTER))
				.comment(values.get(3))
				.categories(values.get(4))
				.tags(values.get(5))
				.duration(parseDuration(values.get(6)))
				.build();
	}

	private Duration parseDuration(String durationString) {

		String[] parts = durationString.split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);

		return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
	}
}
