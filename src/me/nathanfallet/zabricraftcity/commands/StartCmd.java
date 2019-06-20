package me.nathanfallet.zabricraftcity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class StartCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			if (ZabriCraftCity.getInstance().isPlaying()) {
				// Stop the game
				ZabriCraftCity.getInstance().stop();
			} else {
				// Start the game
				ZabriCraftCity.getInstance().start();
			}
		} else {
			sender.sendMessage("§cYou're not allowed to use this command!");
		}
		return true;
	}

}
