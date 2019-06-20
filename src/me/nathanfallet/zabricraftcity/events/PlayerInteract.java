package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.nathanfallet.zabricraftcity.utils.ZabriChunk;

public class PlayerInteract implements Listener {

	// When player interacts
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		// Check if interaction is linked with a block
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			// Get targeted location
			Location target = e.getClickedBlock().getLocation();
			ZabriChunk zc = new ZabriChunk(target.getChunk().getX(), target.getChunk().getZ());
			String owner = zc.getOwner();
			
			// Check if can't interact with this block
			if (!owner.isEmpty() && !owner.equals(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
				// Cancel interaction
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cYou can't interact with this block here!");
			}
		}
	}

}
