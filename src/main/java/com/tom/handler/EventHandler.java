package com.tom.handler;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.tom.api.event.ItemAdvCraftedEvent;
import com.tom.api.item.ICustomCraftingHandler;
import com.tom.api.item.ICustomCraftingHandlerAdv;
import com.tom.api.item.ICustomCraftingHandlerAdv.CraftingErrorException;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.handler.WorldHandler.Action;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.network.messages.MessageMinimap;

public class EventHandler {
	public static final EventHandler instance = new EventHandler();
	// public static Set<Block> woods = new HashSet<>();
	public static Set<Item> disabledItems = new HashSet<>();

	/*@SubscribeEvent
	public void onPlayerLoadFromFileEvent(LoadFromFile event)
	{
	
	}
	
	@SubscribeEvent
	public void onPlayerSaveToFileEvent(SaveToFile event)
	{
	
	}*/
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		if (!event.player.world.isRemote) {
			PlayerHandler.playerLogOut(event.player);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			PlayerHandler.playerLogIn(event.player);
			MessageMarkerSync.sendSyncMessageTo(event.player);
		}
	}

	@SubscribeEvent
	public void onItemCraftedEvent(ItemCraftedEvent event) {
		if (event.crafting != null) {
			if (event.crafting.getItem() instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler) event.crafting.getItem()).onCrafing(event.player, event.crafting, event.craftMatrix);
			else if (event.crafting.getItem() instanceof ItemBlock && ((ItemBlock) event.crafting.getItem()).block instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler) ((ItemBlock) event.crafting.getItem()).block).onCrafing(event.player, event.crafting, event.craftMatrix);
		}
		for (int i = 0;i < event.craftMatrix.getSizeInventory();i++) {
			ItemStack s = event.craftMatrix.getStackInSlot(i);
			if (s != null) {
				if (s.getItem() instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler) s.getItem()).onUsing(event.player, event.crafting, event.craftMatrix, s);
				else if (s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).block instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler) ((ItemBlock) s.getItem()).block).onCrafing(event.player, event.crafting, event.craftMatrix);
			}
		}
		if (event.crafting != null) {
			Item item = event.crafting.getItem();
			if (item == Item.getItemFromBlock(CoreInit.researchTable)) {
				AchievementHandler.giveAchievement(event.player, "researchTable");
			} else if (item == CoreInit.treeTap) {
				AchievementHandler.giveAchievement(event.player, "treetap");
			}
		}
	}

	@SubscribeEvent
	public void onItemCraftedEvent2(ItemAdvCraftedEvent event) {
		if (event.crafting != null) {
			if (event.crafting.getItem() instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv) event.crafting.getItem()).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
			else if (event.crafting.getItem() instanceof ItemBlock && ((ItemBlock) event.crafting.getItem()).block instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv) ((ItemBlock) event.crafting.getItem()).block).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
		}
		try {
			for (int i = 0;i < event.craftMatrix.getSizeInventory();i++) {
				ItemStack s = event.craftMatrix.getStackInSlot(i);
				if (s != null) {
					if (s.getItem() instanceof ICustomCraftingHandlerAdv)
						((ICustomCraftingHandlerAdv) s.getItem()).onUsingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix, s);
					else if (s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).block instanceof ICustomCraftingHandlerAdv)
						((ICustomCraftingHandlerAdv) ((ItemBlock) s.getItem()).block).onUsingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix, s);
				}
			}
		} catch (CraftingErrorException e) {
			event.setCanceled(true);
			event.errorMsg = e.getTextComponent();
		}
	}

	private EventHandler() {
		CoreInit.log.info("Loading Event Handler");
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		if (event.getWorld().provider.getDimension() == 0)
			PlayerHandler.save();
		WorldHandler.saveWorld(event.getWorld().provider.getDimension());
	}

	@SubscribeEvent
	public void breakBlockEvent(BreakEvent event) {
		boolean cancel = WorldHandler.breakBlockS(event);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		WorldHandler.loadChunkS(event.getChunk());
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Unload event) {
		WorldHandler.unloadChunkS(event.getChunk());
	}

	@SubscribeEvent
	public void dimLoad(WorldEvent.Load event) {
		WorldHandler.loadWorld(event.getWorld());
	}

	@SubscribeEvent
	public void dimUnload(WorldEvent.Unload event) {
		if (event.getWorld().isRemote)
			return;
		WorldHandler.unloadWorld(event.getWorld().provider.getDimension());
	}

	@SubscribeEvent
	public void onSpawn(WorldEvent.PotentialSpawns event) {
		try {
			boolean cancel = WorldHandler.onEntitySpawning(event);
			if (cancel)
				event.setCanceled(true);
		} catch (Exception e) {
		}
	}

	@SubscribeEvent
	public void placeBlock(PlaceEvent event) {
		boolean cancel = WorldHandler.placeBlockS(event.getPlacedBlock(), event.getPlayer(), event.getPos());
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void placeBlockM(MultiPlaceEvent event) {
		boolean cancel = WorldHandler.placeBlockS(event.getPlacedBlock(), event.getPlayer(), event.getPos());
		if (cancel)
			event.setCanceled(true);

	}

	@SubscribeEvent
	public void tick(WorldTickEvent event) {
		WorldHandler.onTick(event.world, event.phase);
	}

	public static boolean profile = false;
	public static int key = -1;

	@SubscribeEvent
	public void tickServer(ServerTickEvent event) {
		if (event.phase == Phase.START && event.type == net.minecraftforge.fml.common.gameevent.TickEvent.Type.SERVER) {
			PlayerHandler.update(Phase.START);
		} else if (event.phase == Phase.END && event.type == net.minecraftforge.fml.common.gameevent.TickEvent.Type.SERVER) {
			PlayerHandler.update(Phase.END);
		}
	}

	@SubscribeEvent
	public void breakSpeed(BreakSpeed event) {
		boolean cancel = WorldHandler.breakSpeed(event);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void interactLeftClick(PlayerInteractEvent.LeftClickBlock event) {
		boolean cancel = WorldHandler.interact(event, Action.LEFT_CLICK_BLOCK);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void interact(PlayerInteractEvent.RightClickBlock event) {
		boolean cancel = WorldHandler.interact(event, Action.RIGHT_CLICK_BLOCK);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof EntityPlayer) {
			if (CoreInit.isMapEnabled)
				NetworkHandler.sendTo(new MessageMinimap((EntityPlayer) event.getEntityLiving()), (EntityPlayerMP) event.getEntityLiving());
		}
		/*try{
			ResearchHandler h = ResearchHandler.getHandlerFromName("Player31");
			for(int i = 0;i<58;i++){
				h.markResearchComplete(ResearchHandler.getResearchByID(i));
			}
		}catch(Exception e){}*/
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {

	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (CoreInit.modids.contains(event.getModID())) {
			Config.updateConfig(event.isWorldRunning());
		}
	}

	@SubscribeEvent
	public void onEnderTP(EnderTeleportEvent event) {
		WorldHandler.enderTeleportS(event);
	}

	@SubscribeEvent
	public void onHoe(UseHoeEvent event) {
		if (!event.getCurrent().isEmpty()) {
			if (disabledItems.contains(event.getCurrent().getItem()))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onAttack(AttackEntityEvent event) {
		if (!event.getEntityLiving().getHeldItemMainhand().isEmpty()) {
			if (disabledItems.contains(event.getEntityLiving().getHeldItemMainhand().getItem())) {
				event.setCanceled(true);
				if (event.getTarget().canBeAttackedWithItem()) {
					if (!event.getTarget().hitByEntity(event.getEntity())) {
						event.getEntityPlayer().setLastAttackedEntity(event.getTarget());
						event.getEntityPlayer().world.playSound((EntityPlayer) null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, event.getEntityPlayer().getSoundCategory(), 1.0F, 1.0F);
						event.getTarget().attackEntityFrom(DamageSource.causePlayerDamage(event.getEntityPlayer()), event.getEntityPlayer().getCooledAttackStrength(1F));
						event.getEntityPlayer().addExhaustion(0.1F);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onHarvest(HarvestCheck event) {
		if (!event.getEntityLiving().getHeldItemMainhand().isEmpty()) {
			if (disabledItems.contains(event.getEntityLiving().getHeldItemMainhand().getItem()))
				event.setCanHarvest(false);
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void attachCapabilitiesItem(AttachCapabilitiesEvent.Item event) {
		if (event.getItem() == Items.GLASS_BOTTLE) {
			event.addCapability(new ResourceLocation("tomsmod:resin"), new FluidHandlerItemStack(event.getItemStack(), 1000) {
				@Override
				protected void setFluid(FluidStack fluid) {
					if (fluid.getFluid() == CoreInit.resin.get()) {
						container = CraftingMaterial.BOTTLE_OF_RESIN.getStackNormal();
					} else if (fluid.getFluid() == CoreInit.concentratedResin.get()) {
						container = CraftingMaterial.BOTTLE_OF_CONCENTRATED_RESIN.getStackNormal();
					}
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluid) {
					return (fluid.getFluid() == CoreInit.resin.get() || fluid.getFluid() == CoreInit.concentratedResin.get()) && fluid.amount == 1000;
				}

				@Override
				public boolean canFillFluidType(FluidStack fluid) {
					return (fluid.getFluid() == CoreInit.resin.get() || fluid.getFluid() == CoreInit.concentratedResin.get()) && fluid.amount == 1000;
				}
			});
		}
	}
	/*@SubscribeEvent
	public void tryHarvest(HarvestCheck event){
		if(Config.enableHardModeStarting){
			if(woods.contains(event.getTargetBlock().getBlock())){
				if(event.getEntityPlayer().getHeldItemMainhand().getItem().getHarvestLevel(event.getEntityPlayer().getHeldItemMainhand(), "axe", event.getEntityPlayer(), event.getTargetBlock()) > 0){
					event.setCanHarvest(true);
				}else{
					event.setCanHarvest(false);
				}
			}
		}
	}*/
}
