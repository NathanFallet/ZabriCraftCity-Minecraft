package me.nathanfallet.zabricraftcity.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class SpawnCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1 && sender.isOp()) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p != null && p.isOnline()) {
				p.sendMessage("§6Teleporting...");
				p.teleport(ZabriCraftCity.getInstance().getSpawn());
			}
		} else if (sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage("§6Teleporting...");
			p.teleport(ZabriCraftCity.getInstance().getSpawn());
		}
		return true;
	}

}
