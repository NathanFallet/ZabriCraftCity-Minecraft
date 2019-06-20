package me.nathanfallet.zabricraftcity.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class Leaderboard {

	// List of leaderboards
	private static ArrayList<Leaderboard> list = new ArrayList<Leaderboard>();

	public static ArrayList<Leaderboard> getList() {
		return list;
	}

	// Clear the list
	public static void clear() {
		list.clear();
	}

	// Fetch leaderboards from database
	public static void initFromDatabase() {
		try {
			// Query
			Statement state = ZabriCraftCity.getInstance().getConnection().createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM leaderboards");
			
			// Create each object
			while (result.next()) {
				new Leaderboard(new Location(Bukkit.getWorlds().get(0), result.getDouble("x"), result.getDouble("y"),
						result.getDouble("z")));
			}
			
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Stored properties
	private Location location;
	private ArrayList<ArmorStand> armors = new ArrayList<ArmorStand>();

	// Initializer
	public Leaderboard(Location location) {
		setLocation(location);
		list.add(this);
	}

	// Remove the leaderboard
	public void remove() {
		for (ArmorStand armor : armors) {
			armor.remove();
		}
		list.remove(this);
	}

	// Get the location
	public Location getLocation() {
		return location;
	}

	// Set the location
	public void setLocation(Location location) {
		this.location = location;
	}

	// Update text
	public void update() {
		if (armors.size() != 12) {
			for (ArmorStand armor : armors) {
				armor.remove();
			}
			armors.clear();
			Location current = location.clone().add(0, (0.27 * (12)) - 1, 0);
			for (int i = 0; i < 12; i++) {
				ArmorStand armor = (ArmorStand) location.getWorld().spawnEntity(current, EntityType.ARMOR_STAND);
				armor.setVisible(false);
				armor.setGravity(false);
				armor.setBasePlate(false);
				armor.setCustomName("");
				armor.setCustomNameVisible(true);
				armors.add(armor);
				current.add(0, -0.27, 0);
			}
		}
		int i = 0;
		ArrayList<String> lines = getLines();
		for (ArmorStand armor : armors) {
			if (i == 0) {
				armor.setCustomName("§r§r§e--------- §6Leaderboard §e---------");
			} else if (i == 11) {
				armor.setCustomName("§r§r§e---------------------------");
			} else {
				armor.setCustomName("§r§r§6" + i + "." + (lines.size() >= i ? " §e" + lines.get(i - 1) : ""));
			}
			i++;
		}
	}

	// Fetch lines
	public ArrayList<String> getLines() {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			// Fetch data to MySQL Database
			Statement state = ZabriCraftCity.getInstance().getConnection().createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM players WHERE op = 0 ORDER BY emeralds DESC LIMIT 10");

			// Set lines
			while (result.next()) {
				lines.add(result.getString("pseudo") + " §6- §e" + result.getInt("emeralds") + " emeralds");
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lines;
	}

}
