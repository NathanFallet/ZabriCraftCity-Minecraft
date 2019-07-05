package me.nathanfallet.zabricraftcity.events;

import java.util.List;

import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Career;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;
import me.nathanfallet.zabricraftcity.utils.ItemUtils;

public class PlayerInteractEntity implements Listener {

	// When a player interact with an entity
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		// Check if entity is a Villager
		if (!(e.getRightClicked() instanceof Villager)) {
			return;
		}
		
		if (!ZabriCraftCity.getInstance().isPlaying()) {
			e.getPlayer().sendMessage("§cVillagers are not available at this time of game!");
			return;
		}

		// Get villager informations
		Villager v = (Villager) e.getRightClicked();
		Career c = v.getCareer();

		// Get recipe list
		List<MerchantRecipe> list = ItemUtils.getRecipes(c);

		// Set custom merchant
		if (!list.isEmpty()) {
			v.setRecipes(list);
		}
	}

}
