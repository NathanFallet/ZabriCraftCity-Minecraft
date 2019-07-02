package me.nathanfallet.zabricraftcity.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Career;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class PlayerInteractEntity implements Listener {

	// When a player interact with an entity
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		// Check if entity is a Villager
		if (!(e.getRightClicked() instanceof Villager)) {
			return;
		}

		// Get villager informations
		Villager v = (Villager) e.getRightClicked();
		Career c = v.getCareer();

		// Create a recipe list
		List<MerchantRecipe> list = new ArrayList<>();

		// Depending of career, fill it
		switch (c) {
		
		// FARMER villagers
		case FARMER:
			// 16 WHEAT for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.WHEAT, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 POTATO_ITEM for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.POTATO_ITEM, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 CARROT_ITEM for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.CARROT_ITEM, 16), new ItemStack(Material.EMERALD, 1)));

			// 9 PUMPKIN for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PUMPKIN, 9), new ItemStack(Material.EMERALD, 1)));

			// 9 MELON_BLOCK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.MELON_BLOCK, 9), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// CLERIC villagers
		case CLERIC:
			// 32 ROTTEN_FLESH for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.ROTTEN_FLESH, 32), new ItemStack(Material.EMERALD, 1)));
			
			// 9 GOLD_INGOT for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.GOLD_INGOT, 9), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// LIBRARIAN villagers
		case LIBRARIAN:
			// 32 PAPER for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PAPER, 32), new ItemStack(Material.EMERALD, 1)));
			
			// 8 BOOK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.BOOK, 8), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// LEATHERWORKER villagers
		case LEATHERWORKER:
			// 10 LEATHER for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.LEATHER, 10), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// FLETCHER villagers
		case FLETCHER:
			// 16 STRING for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.STRING, 16), new ItemStack(Material.EMERALD, 1)));
			
			// 16 ARROW for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.ARROW, 16), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// BUTCHER villagers
		case BUTCHER:
			// 16 PORK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PORK, 16), new ItemStack(Material.EMERALD, 1)));
			
			// 16 RAW_CHICKEN for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.RAW_CHICKEN, 16), new ItemStack(Material.EMERALD, 1)));
			
			// 32 COAL for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.COAL, 32), new ItemStack(Material.EMERALD, 1)));
			
			// Exit
			break;
			
		// If nothing from previous cases
		default:
			// Stop here because we have no custom recipe
			return;
			
		}

		// Set custom merchant
		v.setRecipes(list);
	}

	// Create a custom recipe
	public MerchantRecipe createMerchantRecipe(ItemStack from, ItemStack to) {
		MerchantRecipe r = new MerchantRecipe(to, Integer.MAX_VALUE);
		r.addIngredient(from);
		return r;
	}

}
