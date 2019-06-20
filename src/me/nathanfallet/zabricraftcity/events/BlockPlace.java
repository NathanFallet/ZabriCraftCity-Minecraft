package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.nathanfallet.zabricraftcity.utils.ZabriChunk;

public class BlockPlace implements Listener {

	// When a block is placed
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		// Get targeted location
		Location target = e.getBlock().getLocation();
		ZabriChunk zc = new ZabriChunk(target.getChunk().getX(), target.getChunk().getZ());
		String owner = zc.getOwner();
					
		// Check if can't interact with this block
		if (!owner.isEmpty() && !owner.equals(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
			// Cancel interaction
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cYou can't place a block here!");
		}
	}

}
