package me.nathanfallet.zabricraftcity.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.zabricraftcity.utils.ZabriChunk;
import me.nathanfallet.zabricraftcity.utils.ZabriPlayer;

public class ChunkCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			// Get basic informations and balance
			Player player = (Player) sender;
			ZabriPlayer zp = new ZabriPlayer(player);
			ZabriChunk zc = new ZabriChunk(player.getLocation().getChunk().getX(),
					player.getLocation().getChunk().getZ());
			String owner = zc.getOwner();

			// Check sub command
			if (args.length > 0) {
				// Info
				if (args[0].equalsIgnoreCase("info")) {
					if (owner.isEmpty()) {
						player.sendMessage("§eThere is nothing to know about this chunk.");
					} else if (owner.equals("null")) {
						player.sendMessage("§eYou can buy this chunk for §65 emeralds §eusing §6/chunk buy§e.");
					} else if (owner.equals("spawn")) {
						player.sendMessage("§eThis chunk is part of spawn area.");
					} else {
						ZabriPlayer zowner = new ZabriPlayer(owner);
						player.sendMessage("§eThis chunk is owned by §6" + zowner.getName() + "§e.");
					}
				}
				// Buy
				else if (args[0].equalsIgnoreCase("buy") && args.length == 1) {
					// Check is the chunk is available
					if (owner.equals("null")) {
						// Get player balance
						int balance = zp.getEmeralds();
						
						// Check if he has enough to buy it
						if (balance >= 5) {
							// Update balance and chunk owner
							balance -= 5;
							zp.setEmeralds(balance);
							zc.setOwner(player.getUniqueId().toString());
							player.sendMessage("§aYou now own this chunk!");
						} else {
							player.sendMessage("§cYou don't have enough emeralds to buy this chunk!");
						}
					} else {
						player.sendMessage("§cThis chunk is not available!");
					}
				}
				// Sell
				else if (args[0].equalsIgnoreCase("sell") && args.length == 1) {
					// Check if the player owns the chunk
					if (owner.equals(player.getUniqueId().toString())) {
						// Get player balance and update it
						int balance = zp.getEmeralds() + 5;
						zp.setEmeralds(balance);
						zc.setOwner("null");
						player.sendMessage("§aYou sold this chunk for §25 emeralds§a!");
					} else if ((owner.isEmpty() || owner.equals("spawn")) && player.isOp()) {
						// Create the chunk
						zc.setOwner("null");
						
						// Mark chunk borders
						Location zero = player.getLocation().getChunk().getBlock(0, 0, 0).getLocation();
						player.getWorld().getHighestBlockAt(zero).setType(Material.FENCE);
						player.getWorld().getHighestBlockAt(zero.add(15, 0, 0)).setType(Material.FENCE);
						player.getWorld().getHighestBlockAt(zero.add(0, 0, 15)).setType(Material.FENCE);
						player.getWorld().getHighestBlockAt(zero.add(-15, 0, 0)).setType(Material.FENCE);
						
						player.sendMessage("§aThis chunk is now available!");
					} else {
						player.sendMessage("§cYou don't own this chunk!");
					}
				}
				// Claim to spawn
				else if (args[0].equalsIgnoreCase("spawn") && player.isOp()) {
					// Create the chunk
					zc.setOwner("spawn");
					
					player.sendMessage("§aThis chunk is now part of spawn area!");
				}
			} else {
				// Show help
				player.sendMessage("§e---- §6Chunks §e----\n" + "§6/chunk info§f: Informations about this chunk\n"
						+ "§6/chunk buy§f: Buy this chunk for 5 emeralds\n"
						+ "§6/chunk sell§f: Sell this chunk for 5 emeralds (if you own it)\n");
			}
		} else {
			sender.sendMessage("§cOnly players can execute this command!");
		}

		return true;
	}

}
