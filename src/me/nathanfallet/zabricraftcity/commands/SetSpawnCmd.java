package me.nathanfallet.zabricraftcity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class SetSpawnCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && sender.isOp()) {
			Player p = (Player) sender;
			p.sendMessage("§aSpawn location set!");
			ZabriCraftCity.getInstance().setSpawn(p.getLocation());
		} else {
			sender.sendMessage("§cYou're not allowed to use this command!");
		}
		return true;
	}

}
