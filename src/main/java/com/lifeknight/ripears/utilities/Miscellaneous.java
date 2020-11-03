package com.lifeknight.ripears.utilities;

import com.lifeknight.ripears.gui.hud.EnhancedHudText;
import com.lifeknight.ripears.mod.Core;
import com.lifeknight.ripears.variables.SmartCycle;
import com.lifeknight.ripears.variables.SmartNumber;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.util.EnumChatFormatting.*;

public class Miscellaneous {
	public static final String CHROMA_STRING =
			RED + "C" +
					GOLD + "h" +
					YELLOW + "r" +
					GREEN + "o" +
					BLUE + "m" +
					LIGHT_PURPLE + "a";
	public static int getRandomIntBetweenRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static boolean getRandomTrueOrFalse() {
		return getRandomDoubleBetweenRange(0, 1) == 1;
	}

	public static double getRandomDoubleBetweenRange(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static EnumChatFormatting getEnumChatFormatting(String formattedName) {
		switch (formattedName) {
			case "Red":
				return RED;
			case "Gold":
				return GOLD;
			case "Yellow":
				return YELLOW;
			case "Green":
				return GREEN;
			case "Aqua":
				return AQUA;
			case "Blue":
				return BLUE;
			case "Light Purple":
				return LIGHT_PURPLE;
			case "Dark Red":
				return DARK_RED;
			case "Dark Green":
				return DARK_GREEN;
			case "Dark Aqua":
				return DARK_AQUA;
			case "Dark Blue":
				return DARK_BLUE;
			case "Dark Purple":
				return DARK_PURPLE;
			case "White":
				return WHITE;
			case "Gray":
				return GRAY;
			case "Dark Gray":
				return DARK_GRAY;
		}
		return BLACK;
	}

	public static void createEnhancedHudTextDefaultPropertyVariables() {
		new SmartNumber.SmartFloat("Default HUD Text Scale", "HUD", 1.0F, 0.1F, 2.0F, "Change the scale of all HUD Text (this will change their current value; you can change them individually after).") {
			@Override
			public void onSetValue() {
				for (EnhancedHudText enhancedHudText : EnhancedHudText.textToRender) {
					enhancedHudText.setScale(this.getValue());
				}
			}
		}.setiCustomDisplayString(objects -> {
			float value = (float) objects[0];
			return "Default HUD Text Scale: " + (int) (value * 100) + "%";
		});
		new SmartCycle("Default HUD Text Separator", "HUD", Arrays.asList(">", ":", "|", "-", "[]"), "Change the separator of all HUD Text (this will change their current value; you can change them individually after).") {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : EnhancedHudText.textToRender) {
					enhancedHudText.setSeparator(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default HUD Text Separator: " + YELLOW + this.getCurrentValueString();
			}
		};
		new SmartCycle("Default HUD Text Prefix Color", "HUD", Arrays.asList(
				"Red",
				"Gold",
				"Yellow",
				"Green",
				"Aqua",
				"Blue",
				"Light Purple",
				"Dark Red",
				"Dark Green",
				"Dark Aqua",
				"Dark Blue",
				"Dark Purple",
				"White",
				"Gray",
				"Dark Gray",
				"Black",
				"Chroma"
		), 12, "Change the prefix color of all HUD Text (this will change their current value; you can change them individually after).") {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : EnhancedHudText.textToRender) {
					enhancedHudText.setPrefixColor(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default HUD Text Prefix Color: " + (this.getValue() == 16 ? CHROMA_STRING : getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString());
			}
		};
		new SmartCycle("Default HUD Text Content Color", "HUD", Arrays.asList(
				"Red",
				"Gold",
				"Yellow",
				"Green",
				"Aqua",
				"Blue",
				"Light Purple",
				"Dark Red",
				"Dark Green",
				"Dark Aqua",
				"Dark Blue",
				"Dark Purple",
				"White",
				"Gray",
				"Dark Gray",
				"Black",
				"Chroma"
		), 12, "Change the content color of all HUD Text (this will change their current value; you can change them individually after).") {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : EnhancedHudText.textToRender) {
					enhancedHudText.setContentColor(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default HUD Text Content Color: " + (this.getValue() == 16 ? CHROMA_STRING : getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString());
			}
		};
	}

	public static double getModVersionDoubleValue() {
		String version = Core.MOD_VERSION;
		String afterDecimal = version.substring(version.indexOf(".") + 1).replace(".", "");
		return Integer.parseInt(version.substring(0, version.indexOf("."))) + Integer.parseInt(afterDecimal) / 10.0;
	}
	
	public static Date dateFromString(String dateString) throws Exception {
		String monthSection = dateString.substring(0, dateString.indexOf("/"));
		int month = Integer.parseInt(monthSection);
		String daySection = dateString.substring(dateString.indexOf("/") + 1, dateString.lastIndexOf("/"));
		int day = Integer.parseInt(daySection);
		String yearSection = dateString.substring(dateString.lastIndexOf("/") + 1);
		int year = yearSection.length() == 2 ? Integer.parseInt("20" + yearSection) : Integer.parseInt(yearSection);

		if (month < 1 || month > 12 || day < 1 || day > 31 || year < 1000 || year > 9999) {
			throw new IllegalArgumentException(String.format("Invalid input for log files between date range: (%s)", dateString));
		} else {
			String fullString = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
			Date date = new Date();
			date.setTime(LocalDate.parse(fullString).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
			return date;
		}
	}

	public static void info(String info, Object... data) {
		FMLLog.info(Core.MOD_NAME + " > " + info, data);
	}

	public static void warn(String warn, Object... data) {
		FMLLog.log(Level.WARN, Core.MOD_NAME + " > " + warn, data);
	}

	public static void error(String error, Object... data) {
		FMLLog.log(Level.ERROR, Core.MOD_NAME + " > " + error, data);
	}
}
