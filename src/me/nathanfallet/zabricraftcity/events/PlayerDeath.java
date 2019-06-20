package me.nathanfallet.zabricraftcity.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.nathanfallet.zabricraftcity.utils.ZabriPlayer;

public class PlayerDeath implements Listener {

	// When a player dies
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		// Get player and its informations
		ZabriPlayer zp = new ZabriPlayer(e.getEntity());
		int balance = zp.getEmeralds();
		boolean change = balance > 0;
		
		// Change balance if required
		if (change) {
			zp.setEmeralds(balance - 1);
			e.getEntity().sendMessage("§eYou lost 1 emerald!");
			
			// Check if killer is player
			if (e.getEntity().getKiller() != null) {
				// Update his balance
				ZabriPlayer killer = new ZabriPlayer(e.getEntity().getKiller());
				int killerbalance = killer.getEmeralds() + 1;
				killer.setEmeralds(killerbalance);
				e.getEntity().getKiller().sendMessage("§eYou won 1 emerald!");
			}
		}
	}

}
