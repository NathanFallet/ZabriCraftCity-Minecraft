package me.nathanfallet.zabricraftcity.utils;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PlayerScoreboard {
	
	// Map of scoreboards
	private static ArrayList<PlayerScoreboard> list = new ArrayList<PlayerScoreboard>();
	
	// Get full list
	public static ArrayList<PlayerScoreboard> getList(){
		return list;
	}
	
	// Clear the list
	public static void clear() {
		for (PlayerScoreboard current : list) {
			current.kill();
		}
	}
	
	// Get for uuid
	public static PlayerScoreboard get(Player player) {
		for (PlayerScoreboard current : list) {
			if (current.getUUID().equals(player.getUniqueId())) {
				return current;
			}
		}
		
		return new PlayerScoreboard(player);
	}

	// Stored properties
	private Objective objective;
	private ArrayList<String> lastLines = new ArrayList<String>();
	private UUID uuid;

	// Initializer
	public PlayerScoreboard(Player player) {
		this.uuid = player.getUniqueId();
		list.add(this);
	}

	// Getters
	public Objective getObjective() {
		return objective;
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public boolean isActive() {
		return (objective != null);
	}

	// Update lines
	public void update(Player player, ArrayList<String> newLines) {
		if (objective == null) {
			objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("zabricraftcity",
					"dummy");
			objective.setDisplayName("§6§lZabriCraft City");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		for (int pos = 0; pos < newLines.size(); pos++) {
			if (lastLines != null && lastLines.size() > pos && lastLines.get(pos) != null
					&& !lastLines.get(pos).equals(newLines.get(pos))) {
				objective.getScoreboard().resetScores(lastLines.get(pos));
			}
			objective.getScore(newLines.get(pos)).setScore(newLines.size() - pos);
		}
		lastLines = newLines;
		player.setScoreboard(objective.getScoreboard());
	}

	// Kill the scoreboard
	public void kill() {
		objective.unregister();
		objective = null;
		lastLines.clear();
		list.remove(this);
	}

}
