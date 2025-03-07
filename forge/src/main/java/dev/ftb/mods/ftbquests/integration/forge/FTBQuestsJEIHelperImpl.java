package dev.ftb.mods.ftbquests.integration.forge;

import dev.ftb.mods.ftbquests.integration.jei.FTBQuestsJEIIntegration;
import dev.ftb.mods.ftbquests.integration.jei.LootCrateRecipeManagerPlugin;
import dev.ftb.mods.ftbquests.integration.jei.QuestRecipeManagerPlugin;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import static dev.ftb.mods.ftbquests.integration.FTBQuestsJEIHelper.LOOTCRATES;
import static dev.ftb.mods.ftbquests.integration.FTBQuestsJEIHelper.QUESTS;

/**
 * @author LatvianModder
 */
public class FTBQuestsJEIHelperImpl {
	public static void refresh(QuestObjectBase object) {
		int i = object == null ? 0 : object.refreshJEI();

		if (i != 0 && ModList.get().isLoaded("jei")) {
			if ((i & QUESTS) != 0) {
				refreshQuests();
			}

			if ((i & LOOTCRATES) != 0) {
				refreshLootcrates();
			}
		}
	}

	private static void refreshQuests() {
		QuestRecipeManagerPlugin.INSTANCE.refresh();
	}

	private static void refreshLootcrates() {
		LootCrateRecipeManagerPlugin.INSTANCE.refresh();
	}

	@SuppressWarnings("unused")
	public static void showRecipes(ItemStack object) {
		FTBQuestsJEIIntegration.showRecipes(object);
	}
}
