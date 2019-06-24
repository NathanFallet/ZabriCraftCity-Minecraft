package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;
import me.nathanfallet.zabricraftcity.utils.ZabriChunk;

public class EntityDamage implements Listener {

	// When entity damages
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		// Check world
		if (!e.getEntity().getLocation().getWorld().equals(ZabriCraftCity.getInstance().getSpawn().getWorld())) {
			return;
		}

		// Get targeted location
		Location target = e.getEntity().getLocation();
		ZabriChunk zc = new ZabriChunk(target.getChunk().getX(), target.getChunk().getZ());
		String owner = zc.getOwner();

		// Check if it can damage
		if (owner.equals("spawn") || owner.equals("null")) {
			// Cancel damages
			e.setCancelled(true);
		}
	}

}
