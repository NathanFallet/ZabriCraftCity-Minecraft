package me.nathanfallet.zabricraftcity.utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class GameProcess {

	// Stored properties
	private boolean playing;
	private int minute;
	private int hour;
	private int day;

	// Initializer
	public GameProcess() {
		// Get from file
		File f = new File("plugins/ZabriCraftCity/gameprocess.yml");
		if (!f.exists()) {
			return;
		}

		// Load vars
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		playing = config.getBoolean("playing");
		minute = config.getInt("minute");
		hour = config.getInt("hour");
		day = config.getInt("day");

		// Update time in worlds
		for (World w : Bukkit.getWorlds()) {
			w.setTime(day * 24_000 + hour * 1_200 + minute * 20);
		}
	}

	// Save values to file
	public void save() {
		File f = new File("plugins/ZabriCraftCity/gameprocess.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);

		config.set("playing", playing);
		config.set("minute", minute);
		config.set("hour", hour);
		config.set("day", day);

		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Increment time
	public void increment() {
		if (playing) {
			minute++;
			if (minute > 59) {
				minute = 0;
				hour++;
			}
			if (hour > 19) {
				hour = 0;
				day++;
			}
		}
	}

	// Check if the game is playing
	public boolean isPlaying() {
		return playing;
	}

	// Get time as string
	public String toString() {
		if (playing) {
			return "Day " + (day + 1) + " - " + (hour >= 10 ? hour : ("0" + hour)) + ":"
					+ (minute >= 10 ? minute : ("0" + minute));
		} else {
			return "Waiting...";
		}
	}

	// Start the game
	public void start() {
		// Check if the game is not already playing
		if (!playing) {
			// Set playing and time
			playing = true;
			minute = 0;
			hour = 0;
			day = 0;

			// Set world time
			for (World w : Bukkit.getWorlds()) {
				w.setTime(0);
				w.setGameRuleValue("doDaylightCycle", "true");
			}

			// Broadcast it
			Bukkit.broadcastMessage("§6§lGame is starting. May the best win!");

			// Change players game mode
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.isOp()) {
					player.setGameMode(GameMode.SURVIVAL);
				}
			}

			// Reset emeralds for all players
			try {
				Statement state = ZabriCraftCity.getInstance().getConnection().createStatement();
				state.executeUpdate("UPDATE players SET emeralds = 0");
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Stop the game
	public void stop() {
		// Check if the game is playing
		if (playing) {
			// Set playing
			playing = false;

			// Broadcast it
			Bukkit.broadcastMessage("§6§lIt's the end of the game!");

			// Change players game mode
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.isOp()) {
					player.setGameMode(GameMode.ADVENTURE);
				}
			}
		}
	}

}
