package me.nathanfallet.zabricraftcity.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class PlayerRespawn implements Listener {

	// When player respawn
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		// Set spawn location
		e.setRespawnLocation(ZabriCraftCity.getInstance().getSpawn());
	}

}
