package me.nathanfallet.zabricraftcity.events;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.nathanfallet.zabricraftcity.utils.ZabriChunk;
import me.nathanfallet.zabricraftcity.utils.ZabriPlayer;

public class PlayerMove implements Listener {

	// When player move
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		// Get chunks
		Chunk from = e.getFrom().getChunk();
		Chunk to = e.getTo().getChunk();
		
		// If the player changed of chunk
		if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
			// Get values
			ZabriChunk zfrom = new ZabriChunk(from.getX(), from.getZ());
			ZabriChunk zto = new ZabriChunk(to.getX(), to.getZ());
			
			// Get owners
			String fromowner = zfrom.getOwner();
			String toowner = zto.getOwner();
			
			// Check if owner is different
			if (!fromowner.equals(toowner)) {
				// Check from
				if (!fromowner.isEmpty() && !fromowner.equals("null")) {
					ZabriPlayer zfromowner = new ZabriPlayer(fromowner);
					e.getPlayer().sendMessage("§eYou left §6"+zfromowner.getName()+"§e's chunk.");
				}
				
				// Check to
				if (!toowner.isEmpty()) {
					// Player can buy this chunk
					if (toowner.equals("null")) {
						e.getPlayer().sendMessage("§eYou can buy this chunk for §65 emeralds §eusing §6/chunk buy§e.");
					}
					// Already has an owner
					else {
						ZabriPlayer ztoowner = new ZabriPlayer(toowner);
						e.getPlayer().sendMessage("§eYou entered §6"+ztoowner.getName()+"§e's chunk.");
					}
				}
			}
		}
	}

}
