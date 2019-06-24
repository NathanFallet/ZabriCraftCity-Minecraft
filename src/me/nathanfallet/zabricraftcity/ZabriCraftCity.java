package me.nathanfallet.zabricraftcity;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.zabricraftcity.commands.BankCmd;
import me.nathanfallet.zabricraftcity.commands.ChunkCmd;
import me.nathanfallet.zabricraftcity.commands.SetSpawnCmd;
import me.nathanfallet.zabricraftcity.commands.SpawnCmd;
import me.nathanfallet.zabricraftcity.commands.StartCmd;
import me.nathanfallet.zabricraftcity.events.BlockBreak;
import me.nathanfallet.zabricraftcity.events.BlockPlace;
import me.nathanfallet.zabricraftcity.events.CreatureSpawn;
import me.nathanfallet.zabricraftcity.events.EntityDamage;
import me.nathanfallet.zabricraftcity.events.EntityExplode;
import me.nathanfallet.zabricraftcity.events.PlayerChat;
import me.nathanfallet.zabricraftcity.events.PlayerDeath;
import me.nathanfallet.zabricraftcity.events.PlayerInteract;
import me.nathanfallet.zabricraftcity.events.PlayerJoin;
import me.nathanfallet.zabricraftcity.events.PlayerMove;
import me.nathanfallet.zabricraftcity.events.PlayerQuit;
import me.nathanfallet.zabricraftcity.events.PlayerRespawn;
import me.nathanfallet.zabricraftcity.utils.GameProcess;
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
	private GameProcess process;
	private Location spawn;

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
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `players` (`uuid` varchar(255) NOT NULL, `pseudo` varchar(255) NOT NULL, `emeralds` int(11) NOT NULL DEFAULT '0', `op` tinyint(1) NOT NULL default '0', PRIMARY KEY (`uuid`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `chunks` (`x` int(11) NOT NULL, `z` int(11) NOT NULL, `owner` varchar(255) NOT NULL, PRIMARY KEY (`x`, `z`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `leaderboards` (`x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, PRIMARY KEY (`x`, `y`, `z`))");
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Load game process
			process = new GameProcess();

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

			// Initialize leaderboards
			Leaderboard.initFromDatabase();

			// Register events
			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new BlockBreak(), this);
			pm.registerEvents(new BlockPlace(), this);
			pm.registerEvents(new CreatureSpawn(), this);
			pm.registerEvents(new EntityDamage(), this);
			pm.registerEvents(new EntityExplode(), this);
			pm.registerEvents(new PlayerChat(), this);
			pm.registerEvents(new PlayerDeath(), this);
			pm.registerEvents(new PlayerInteract(), this);
			pm.registerEvents(new PlayerJoin(), this);
			pm.registerEvents(new PlayerMove(), this);
			pm.registerEvents(new PlayerQuit(), this);
			pm.registerEvents(new PlayerRespawn(), this);

			// Register commands
			getCommand("spawn").setExecutor(new SpawnCmd());
			getCommand("setspawn").setExecutor(new SetSpawnCmd());
			getCommand("bank").setExecutor(new BankCmd());
			getCommand("chunk").setExecutor(new ChunkCmd());
			getCommand("start").setExecutor(new StartCmd());

			// Update some shown informations
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					// Increment timer and get string
					process.increment();
					String time = process.toString();

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
						lines.add("§f" + time);
						lines.add("§e");
						lines.add("§ezabricraftcity.nathanfallet.me");

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

		// Save the game
		process.save();
	}

	// MySQL Database connection
	public Connection getConnection() {
		try {
			// If not connected, connect
			if (connection == null || connection.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				FileConfiguration conf = getConfig();
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + conf.getString("database.host") + ":" + conf.getInt("database.port") + "/"
								+ conf.getString("database.database"),
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
			// Insert or update player informations
			PreparedStatement state = getConnection().prepareStatement(
					"INSERT INTO players (uuid, pseudo, op) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE pseudo = ?, op = ?");
			state.setString(1, p.getUniqueId().toString());
			state.setString(2, p.getName());
			state.setBoolean(3, p.isOp());
			state.setString(4, p.getName());
			state.setBoolean(5, p.isOp());
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Check if the game is playing
	public boolean isPlaying() {
		return process.isPlaying();
	}

	// Start the game
	public void start() {
		process.start();
	}

	// Stop the game
	public void stop() {
		process.stop();
	}

	// Get spawn
	public Location getSpawn() {
		// Check if spawn is already loaded
		if (spawn != null) {
			return spawn.clone();
		}
		
		// Else load if from file
		File f = new File("plugins/ZabriCraftCity/spawn.yml");
		if (!f.exists()) {
			spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
			return spawn.clone();
		}

		// Read file content
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		spawn = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"),
				config.getDouble("y"), config.getDouble("z"));
		spawn.setYaw(config.getLong("yaw"));
		spawn.setPitch(config.getLong("pitch"));

		return spawn.clone();
	}

	// Set spawn
	public void setSpawn(Location l) {
		// Set spawn
		spawn = l;
		
		// Load file
		File f = new File("plugins/ZabriCraftCity/spawn.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);

		// Set values
		config.set("world", l.getWorld().getName());
		config.set("x", l.getX());
		config.set("y", l.getY());
		config.set("z", l.getZ());
		config.set("yaw", l.getYaw());
		config.set("pitch", l.getPitch());

		// Save file
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
