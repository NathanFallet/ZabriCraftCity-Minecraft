package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.zabricraftcity.utils.ZabriChunk;

public class BlockBreak implements Listener {

	// When a block is broken
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		// Get targeted location
		Location target = e.getBlock().getLocation();
		ZabriChunk zc = new ZabriChunk(target.getChunk().getX(), target.getChunk().getZ());
		String owner = zc.getOwner();

		// Check if can't interact with this block
		if (!owner.isEmpty() && !owner.equals(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
			// Cancel interaction
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cYou can't break a block here!");
		}
	}

}
