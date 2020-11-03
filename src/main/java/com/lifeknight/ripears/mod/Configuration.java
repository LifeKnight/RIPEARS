package com.lifeknight.ripears.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lifeknight.ripears.utilities.Chat;
import com.lifeknight.ripears.utilities.Miscellaneous;
import com.lifeknight.ripears.variables.SmartVariable;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Configuration {
	private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private JsonObject configurationAsJsonObject = new JsonObject();
	private final File file;

	public Configuration(String id) {
		this.file = new File("config/" + id + ".json");
		if (this.configExists()) {
			this.updateVariablesFromConfiguration();
		}
		this.updateConfigurationFromVariables();
	}
	
	public Configuration() {
		this(Core.MOD_ID);
	}

	private void updateVariablesFromConfiguration() {
		this.getConfigurationContent();
		for (int i = 0; i < SmartVariable.getVariables().size(); i++) {
			SmartVariable smartVariable = SmartVariable.getVariables().get(i);
			if (smartVariable.isStoreValue()) {
				try {
					smartVariable.setValueFromJson(this.configurationAsJsonObject);
				} catch (Exception exception) {
					Chat.queueChatMessageForConnection(EnumChatFormatting.RED + "An error occurred while extracting the value of \"" + smartVariable.getName() + "\" from the config; the value will be interpreted as " + smartVariable.getValue() + ".");
				}
			}
		}
	}

	public void updateConfigurationFromVariables() {
		JsonObject configAsJsonReplacement = new JsonObject();

		List<String> groups = new ArrayList<>();

		for (SmartVariable smartVariable : SmartVariable.getVariables()) {
			if (!groups.contains(smartVariable.getGroupForConfiguration()) && smartVariable.isStoreValue()) {
				groups.add(smartVariable.getGroupForConfiguration());
			}
		}

		for (String group : groups) {
			JsonObject jsonObject = new JsonObject();
			for (SmartVariable smartVariable : SmartVariable.getVariables()) {
				if (smartVariable.isStoreValue() && smartVariable.getGroupForConfiguration().equals(group)) {
					smartVariable.appendToJson(jsonObject);
				}
			}
			configAsJsonReplacement.add(group, jsonObject);
		}

		this.configurationAsJsonObject = configAsJsonReplacement;

		this.writeToConfigurationFile();
	}

	private boolean configExists() {
		try {
			return !this.file.createNewFile();
		} catch (Exception exception) {
			Miscellaneous.error("An error occurred while attempting to check for the configuration file's existence: %s", exception.getMessage());
			return false;
		}
	}

	private void writeToConfigurationFile() {
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));

			writer.write(GSON.toJson(this.configurationAsJsonObject));

			writer.close();
		} catch (Exception exception) {
			Miscellaneous.error("Could not write to configuration file: %s", exception.getMessage());
		}
	}

	private void getConfigurationContent() {
		try {
			Scanner reader = new Scanner(this.file);
			StringBuilder configContent = new StringBuilder();

			while (reader.hasNextLine()) {
				if (configContent.length() == 0) {
					configContent.append(reader.nextLine());
				} else configContent.append(System.getProperty("line.separator")).append(reader.nextLine());
			}

			reader.close();

			this.configurationAsJsonObject = new JsonParser().parse(configContent.toString()).getAsJsonObject();
		} catch (Exception exception) {
			Miscellaneous.error("Could not read configuration file: %s", exception.getMessage());
		}
	}

	public JsonObject getConfigurationAsJsonObject() {
		return this.configurationAsJsonObject;
	}
}
