package me.nathanfallet.zabricraftcity.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nathanfallet.zabricraftcity.utils.ZabriPlayer;

public class BankCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			// Get basic informations and balance
			Player player = (Player) sender;
			ZabriPlayer zp = new ZabriPlayer(player);
			int balance = zp.getEmeralds();

			// Check sub command
			if (args.length > 0) {
				// Show player balance
				if (args[0].equalsIgnoreCase("balance")) {
					player.sendMessage("§6You have §e" + balance + " emeralds§6 on your account.");
				}
				// Deposit
				else if (args[0].equalsIgnoreCase("deposit") && args.length == 2) {
					try {
						// Parse amount
						int amount = Integer.parseInt(args[1]);

						// Check if player has enough
						if (player.getInventory().contains(Material.EMERALD, amount)) {
							// Remove from inventory and add on account
							balance += amount;
							player.getInventory().removeItem(new ItemStack(Material.EMERALD, amount));
							zp.setEmeralds(balance);
							player.sendMessage("§aYou just deposited §2" + amount
									+ " emeralds §aon your account!\n§eYou now have §6" + balance
									+ " emeralds §eon your account");
						} else {
							// Warn
							player.sendMessage("§cYou don't have enough emeralds in your inventory!");
						}
					} catch (NumberFormatException e) {
						player.sendMessage("§4" + args[1] + " §cis not a valid number!");
					}
				}
				// Retrieve
				else if (args[0].equalsIgnoreCase("retrieve") && args.length == 2) {
					try {
						// Parse amount
						int amount = Integer.parseInt(args[1]);

						// Check if player has enough
						if (balance >= amount) {
							// Remove from account and add in inventory
							balance -= amount;
							zp.setEmeralds(balance);
							player.getInventory().addItem(new ItemStack(Material.EMERALD, amount));
							player.sendMessage("§aYou just retrieved §2" + amount
									+ " emeralds §afrom your account!\n§eYou now have §6" + balance
									+ " emeralds §eon your account");
						} else {
							// Warn
							player.sendMessage("§cYou don't have enough emeralds on your account!");
						}
					} catch (NumberFormatException e) {
						player.sendMessage("§4" + args[1] + " §cis not a valid number!");
					}
				}
			} else {
				// Show help
				player.sendMessage("§e---- §6Bank §e----\n"
						+ "§6/bank balance§f: Show how many emeralds you have on your account\n"
						+ "§6/bank deposit <amount>§f: Deposit <amount> emeralds on your account\n"
						+ "§6/bank retrieve <amount>§f: Retrieve <amount> emeralds on your account\n");
			}
		} else {
			sender.sendMessage("§cOnly players can execute this command!");
		}

		return true;
	}

}
