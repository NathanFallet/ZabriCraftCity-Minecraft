package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {
	
	// When player talk in chat
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// Get player
		Player p = e.getPlayer();
		
		// Set custom chat format
		e.setFormat("%s§r: %s");
		
		// If player is operator enable colors
		if (p.isOp()) {
			e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		}
		
		// Manage mentions
		e.setMessage(" "+e.getMessage()+" ");
		for (Player pls : Bukkit.getOnlinePlayers()) {
			if (e.getMessage().contains(" "+pls.getName()+" ")) {
				// play a sound and color the name
				pls.playSound(pls.getLocation(), Sound.NOTE_PIANO, 10, 1);
				e.setMessage(e.getMessage().replaceAll(" "+pls.getName()+" ", " §b@"+pls.getName()+"§r "));
			}
		}
		e.setMessage(e.getMessage().trim());
	}

}
