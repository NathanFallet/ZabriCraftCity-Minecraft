package me.nathanfallet.zabricraftcity.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode implements Listener {

	// When an entity explodes
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		e.setCancelled(true);
	}

}
