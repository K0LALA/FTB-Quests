package com.feed_the_beast.ftbquests;

import com.feed_the_beast.ftblib.events.FTBLibPreInitRegistryEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent;
import com.feed_the_beast.ftblib.lib.data.AdminPanelAction;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftbquests.block.BlockQuest;
import com.feed_the_beast.ftbquests.block.ItemBlockQuest;
import com.feed_the_beast.ftbquests.block.TileQuest;
import com.feed_the_beast.ftbquests.net.MessageEditQuests;
import com.feed_the_beast.ftbquests.net.MessageResetProgress;
import com.feed_the_beast.ftbquests.quest.ServerQuestList;
import com.feed_the_beast.ftbquests.util.FTBQuestsTeamData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBQuests.MOD_ID)
public class FTBQuestsEventHandler
{
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockQuest(FTBQuests.MOD_ID, "quest_block"));
		GameRegistry.registerTileEntity(TileQuest.class, new ResourceLocation(FTBQuests.MOD_ID, "quest_block"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemBlockQuest(FTBQuests.QUEST_BLOCK));
	}

	@SubscribeEvent
	public static void onFTBLibPreInitRegistry(FTBLibPreInitRegistryEvent event)
	{
		FTBLibPreInitRegistryEvent.Registry registry = event.getRegistry();
		registry.registerServerReloadHandler(new ResourceLocation(FTBQuests.MOD_ID, "config"), reloadEvent -> FTBQuestsConfig.sync());

		registry.registerAdminPanelAction(new AdminPanelAction(FTBQuests.MOD_ID, "edit", GuiIcons.BOOK_RED, 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBQuestsCommon.PERM_EDIT));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
				new MessageEditQuests().sendTo(player.getPlayer());
			}
		});

		registry.registerAdminPanelAction(new AdminPanelAction(FTBQuests.MOD_ID, "reset_progress", GuiIcons.BOOK_RED.combineWith(GuiIcons.CLOSE), 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBQuestsCommon.PERM_RESET_PROGRESS));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data0)
			{
				ServerQuestList.INSTANCE.shouldSendUpdates = false;
				for (ForgeTeam team : player.team.universe.getTeams())
				{
					FTBQuestsTeamData.get(team).reset();
				}

				player.team.universe.clearCache();
				new MessageResetProgress(player.team.getName()).sendTo(player.getPlayer());
				ServerQuestList.INSTANCE.shouldSendUpdates = true;
			}
		});
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		ServerQuestList.INSTANCE.sync(event.getPlayer().getPlayer());
	}

	@SubscribeEvent
	public static void onUniverseLoaded(UniverseLoadedEvent.Pre event)
	{
		if (!ServerQuestList.load())
		{
			FTBQuests.LOGGER.error("Failed to load quests!");
		}
	}
}