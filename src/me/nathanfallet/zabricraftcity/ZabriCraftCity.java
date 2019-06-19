package me.nathanfallet.zabricraftcity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.zabricraftcity.commands.BankCmd;
import me.nathanfallet.zabricraftcity.events.PlayerChat;
import me.nathanfallet.zabricraftcity.events.PlayerJoin;
import me.nathanfallet.zabricraftcity.events.PlayerQuit;
import me.nathanfallet.zabricraftcity.utils.Leaderboard;
import me.nathanfallet.zabricraftcity.utils.PlayerScoreboard;
import me.nathanfallet.zabricraftcity.utils.ZabriPlayer;

public class ZabriCraftCity extends JavaPlugin {
	
	// Store instance to get everywhere
	private static ZabriCraftCity instance;
	
	public static ZabriCraftCity getInstance() {
		return instance;
	}
	
	// Stored properties
	private Connection connection;
	private boolean playing;
	
	// Enabling the plugin
	public void onEnable() {
		// Set current instance
		instance = this;
		
		// Configuration files
		saveDefaultConfig();
		reloadConfig();
		
		// Check connection and init
		if (getConnection() != null) {
			// Initialize database structure
			try {
				Statement state = getConnection().createStatement();
				state.executeUpdate("CREATE TABLE IF NOT EXISTS `players` (`uuid` varchar(255) NOT NULL, `pseudo` varchar(255) NOT NULL, `emeralds` int(11) NOT NULL DEFAULT '0', PRIMARY KEY (`uuid`))");
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Initialize players
			for (Player player : Bukkit.getOnlinePlayers()) {
				initPlayer(player);
			}
			
			// Clear custom entities
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (e.getCustomName() != null && e.getCustomName().startsWith("§r§r")) {
						e.remove();
					}
				}
			}
			
			// Register events
			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new PlayerJoin(), this);
			pm.registerEvents(new PlayerQuit(), this);
			pm.registerEvents(new PlayerChat(), this);
			
			// Register commands
			getCommand("bank").setExecutor(new BankCmd());
			
			// Update some shown informations
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {
					// Updates
					for (Player p : Bukkit.getOnlinePlayers()) {
						// List name
						p.setPlayerListName(p.getDisplayName());
						
						// Scoreboard
						ZabriPlayer zp = new ZabriPlayer(p);
						
						ArrayList<String> lines = new ArrayList<String>();
						lines.add("§b");
						lines.add("§b§lBank: §b(/bank for more)");
						lines.add("§f" + zp.getEmeralds() + " emeralds");
						lines.add("§a");
						lines.add("§a§lTime:");
						lines.add("§fDay X - 00:00 (todo)");
						lines.add("§e");
						lines.add("§e§lPlugin by Nathan Fallet");
						
						PlayerScoreboard.get(p).update(p, lines);
					}
					
					// Update leaderboards
					for (Leaderboard leaderboard : Leaderboard.getList()) {
						leaderboard.update();
					}
				}
			}, 0, 20);
			
		}
	}
	
	// Disable
	public void onDisable() {
		// Clear objects
		Leaderboard.clear();
		PlayerScoreboard.clear();
		
		// Stop the game
		// TODO
	}
	
	// MySQL Database connection
	public Connection getConnection() {
		try {
			// If not connected, connect
			if(connection == null || connection.isClosed()){
				Class.forName("com.mysql.jdbc.Driver");
				FileConfiguration conf = getConfig();
				connection = DriverManager.getConnection("jdbc:mysql://"+conf.getString("database.host")+":"+conf.getInt("database.port")+"/"+conf.getString("database.database"),
						conf.getString("database.user"), conf.getString("database.password"));
			}
		} catch (SQLException | ClassNotFoundException e) {
			// Something wrong happened
			Bukkit.getLogger().severe("[ZabriCraftCity] Unable to connect to MySQL Database!");
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}
		return connection;
	}
	
	// Initialize a player
	public void initPlayer(Player p) {
		try {
			// Check if player is in database
			Statement state = getConnection().createStatement();
			ResultSet result = state.executeQuery("SELECT COUNT(*) as c FROM players WHERE uuid = '"+p.getUniqueId().toString()+"'");
			result.next();
			if (result.getInt("c") == 0) {
				// If not, insert it
				state.executeUpdate("INSERT INTO players (uuid, pseudo) VALUES('"+p.getUniqueId().toString()+"', '"+p.getName()+"')");
				Bukkit.broadcastMessage("§eWelcome to §6"+p.getName()+" §eon the server!");
			} else {
				// Else just update
				state.executeUpdate("UPDATE players SET pseudo = '"+p.getName()+"' WHERE uuid = '"+p.getUniqueId().toString()+"'");
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Check if the game is playing
	public boolean isPlaying() {
		return playing;
	}
	
	// Start the game
	public void start() {
		// Check if the game is not already playing
		if (!playing) {
			// Set playing
			playing = true;
			
			// Broadcast it
			Bukkit.broadcastMessage("§6Game is starting. May the best win!");
			
			// Change players game mode
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.isOp()) {
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
			
			// Reset emeralds for all players
			try {
				Statement state = getConnection().createStatement();
				state.executeUpdate("UPDATE players SET emeralds = 0");
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
