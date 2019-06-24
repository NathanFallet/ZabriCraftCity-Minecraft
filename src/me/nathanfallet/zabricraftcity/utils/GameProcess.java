package me.nathanfallet.zabricraftcity.utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
			// Increment minute
			minute++;
			
			// If 60 minutes, increment hour
			if (minute > 59) {
				minute = 0;
				hour++;
			}
			
			// If 20 hours, increment day
			if (hour > 19) {
				hour = 0;
				day++;
				
				// If day is multiple of 3, pop a chest
				if (day % 3 == 0) {
					popRandomChest();
				}
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

	// Pop a random chest
	public void popRandomChest() {
		// Initializations
		Location location;
		Random random = new Random();

		// Determine a safe location
		while (true) {
			// Create a random location
			location = ZabriCraftCity.getInstance().getSpawn().add(random.nextDouble() * 1000,
					-random.nextDouble() * 30, random.nextDouble() * 1000);
			ZabriChunk zc = new ZabriChunk(location.getChunk().getX(), location.getChunk().getZ());

			// Check if chunk is safe
			if (zc.getOwner().isEmpty()) {
				break;
			}
		}

		// Create the chest
		location.getBlock().setType(Material.CHEST);
		Chest chest = (Chest) location.getBlock().getState();

		// Fill with random items
		ChestItem[] available = { new ChestItem(Material.ROTTEN_FLESH, 50, 16), new ChestItem(Material.STRING, 50, 16),
				new ChestItem(Material.SULPHUR, 50, 16), new ChestItem(Material.BONE, 50, 16),
				new ChestItem(Material.ARROW, 50, 16), new ChestItem(Material.PORK, 50, 16),
				new ChestItem(Material.RAW_CHICKEN, 50, 16), new ChestItem(Material.RAW_BEEF, 50, 16),
				new ChestItem(Material.WHEAT, 50, 16), new ChestItem(Material.POTATO_ITEM, 50, 16),
				new ChestItem(Material.CARROT_ITEM, 50, 16), new ChestItem(Material.LEATHER, 30, 8),
				new ChestItem(Material.IRON_INGOT, 30, 8), new ChestItem(Material.GOLD_INGOT, 20, 8),
				new ChestItem(Material.DIAMOND, 10, 4), new ChestItem(Material.EMERALD, 5, 2) };

		for (ChestItem item : available) {
			// Check probability
			if (random.nextInt(100) < item.getProbability()) {
				// Set randomly in chest
				chest.getInventory().setItem(random.nextInt(27),
						new ItemStack(item.getMaterial(), random.nextInt(item.getAmount())));
			}
		}

		// Broadcast chest location
		Bukkit.broadcastMessage("§6§lA chest just appeared at " + location.getBlockX() + " / " + location.getBlockY()
				+ " / " + location.getBlockZ() + ". Be the first to find it!");
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
