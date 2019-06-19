package me.nathanfallet.zabricraftcity.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.nathanfallet.zabricraftcity.utils.PlayerScoreboard;

public class PlayerQuit implements Listener {
	
	// When a player quit the server
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		// Set a beautiful welcome message
		e.setQuitMessage("§c[-] "+(e.getPlayer().isOp() ? "§c" : "§2")+e.getPlayer().getName()+" §ais going offline!");
		
		// Kill scoreboard
		PlayerScoreboard.get(e.getPlayer()).kill();
	}

}
