package com.tom.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.block.Block;
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
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.google.common.collect.ImmutableList;

import com.tom.api.event.ItemAdvCraftedEvent;
import com.tom.api.item.ICustomCraftingHandler;
import com.tom.api.item.ICustomCraftingHandlerAdv;
import com.tom.api.item.ICustomCraftingHandlerAdv.CraftingErrorException;
import com.tom.api.research.Research;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.research.ResearchHandler;
import com.tom.handler.TMWorldHandler.Action;
import com.tom.lib.Configs;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageMarker;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.util.TMLogger;

public class EventHandler {
	public static final EventHandler instance = new EventHandler();
	// public static Set<Block> woods = new HashSet<>();
	public static Set<Item> disabledItems = new HashSet<>();

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			MessageMarkerSync.sendSyncMessageTo(event.player);
		}
	}
	@SubscribeEvent
	public void onItemCraftedEvent(ItemCraftedEvent event) {
		if (event.crafting != null) {
			if (event.crafting.getItem() instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler) event.crafting.getItem()).onCrafing(event.player, event.crafting, event.craftMatrix);
			else if (event.crafting.getItem() instanceof ItemBlock && ((ItemBlock) event.crafting.getItem()).getBlock() instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler) ((ItemBlock) event.crafting.getItem()).getBlock()).onCrafing(event.player, event.crafting, event.craftMatrix);
		}
		for (int i = 0;i < event.craftMatrix.getSizeInventory();i++) {
			ItemStack s = event.craftMatrix.getStackInSlot(i);
			if (s != null) {
				if (s.getItem() instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler) s.getItem()).onUsing(event.player, event.crafting, event.craftMatrix, s);
				else if (s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler) ((ItemBlock) s.getItem()).getBlock()).onCrafing(event.player, event.crafting, event.craftMatrix);
			}
		}
		if (event.crafting != null) {
			Item item = event.crafting.getItem();
			if (item == Item.getItemFromBlock(CoreInit.researchTable)) {
				//TODO: AchievementHandler.giveAchievement(event.player, "researchTable");
			} else if (item == CoreInit.treeTap) {
				//TODO: AchievementHandler.giveAchievement(event.player, "treetap");
			}
		}
	}

	@SubscribeEvent
	public void onItemCraftedEvent2(ItemAdvCraftedEvent event) {
		if (event.crafting != null) {
			if (event.crafting.getItem() instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv) event.crafting.getItem()).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
			else if (event.crafting.getItem() instanceof ItemBlock && ((ItemBlock) event.crafting.getItem()).getBlock() instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv) ((ItemBlock) event.crafting.getItem()).getBlock()).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
		}
		try {
			for (int i = 0;i < event.craftMatrix.getSizeInventory();i++) {
				ItemStack s = event.craftMatrix.getStackInSlot(i);
				if (s != null) {
					if (s.getItem() instanceof ICustomCraftingHandlerAdv)
						((ICustomCraftingHandlerAdv) s.getItem()).onUsingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix, s);
					else if (s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof ICustomCraftingHandlerAdv)
						((ICustomCraftingHandlerAdv) ((ItemBlock) s.getItem()).getBlock()).onUsingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix, s);
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
	public void breakBlockEvent(BreakEvent event) {
		boolean cancel = TMWorldHandler.breakBlockS(event);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void placeBlock(PlaceEvent event) {
		boolean cancel = TMWorldHandler.placeBlockS(event.getPlacedBlock(), event.getPlayer(), event.getPos());
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void placeBlockM(MultiPlaceEvent event) {
		boolean cancel = TMWorldHandler.placeBlockS(event.getPlacedBlock(), event.getPlayer(), event.getPos());
		if (cancel)
			event.setCanceled(true);

	}

	public static boolean profile = false;
	public static int key = -1;

	@SubscribeEvent
	public void breakSpeed(BreakSpeed event) {
		boolean cancel = TMWorldHandler.breakSpeed(event);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void interactLeftClick(PlayerInteractEvent.LeftClickBlock event) {
		boolean cancel = TMWorldHandler.interact(event, Action.LEFT_CLICK_BLOCK);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void interact(PlayerInteractEvent.RightClickBlock event) {
		boolean cancel = TMWorldHandler.interact(event, Action.RIGHT_CLICK_BLOCK);
		if (cancel)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof EntityPlayer) {
			//if (CoreInit.isMapEnabled)
			NetworkHandler.sendTo(new MessageMarker((EntityPlayer) event.getEntityLiving()), (EntityPlayerMP) event.getEntityLiving());
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
		TMWorldHandler.enderTeleportS(event);
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

	@SubscribeEvent
	public void attachCapabilitiesItem(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject() instanceof ItemStack && event.getObject().getItem() == Items.GLASS_BOTTLE) {
			event.addCapability(new ResourceLocation("tomsmod:resin"), new FluidHandlerItemStack(event.getObject(), 1000) {
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
	@SubscribeEvent
	public void remapItem(MissingMappings<Item> event){
		TMLogger.info("Remapping items...");
		filterMappings(event.getAllMappings(), e -> {
			remap(ForgeRegistries.ITEMS, e);
		});
	}
	@SubscribeEvent
	public void remapBlock(MissingMappings<Block> event){
		TMLogger.info("Remapping blocks...");
		filterMappings(event.getAllMappings(), e -> {
			remap(ForgeRegistries.BLOCKS, e);
		});
	}
	@SubscribeEvent
	public void remapResearch(MissingMappings<Research> event){
		TMLogger.info("Remapping researches...");
		filterMappings(event.getAllMappings(), e -> {
			remap(ResearchHandler.REGISTRY, e);
		});
	}
	private <T extends IForgeRegistryEntry<T>> void filterMappings(ImmutableList<Mapping<T>> in, Consumer<Mapping<T>> action){
		in.stream().filter(e -> e.key.getResourceDomain().startsWith(Configs.ModidL) && (e.key.getResourceDomain().contains("|") || e.key.getResourcePath().contains("blend"))).forEach(action);
	}
	private <T extends IForgeRegistryEntry<T>> void remap(IForgeRegistry<T> reg, Mapping<T> e){
		ResourceLocation loc = new ResourceLocation(e.key.getResourceDomain().replace("|", "").replace("blender", "bender").replace("blending", "bending"), e.key.getResourcePath());
		T val = reg.getValue(loc);
		if(val != null)e.remap(val);
		else TMLogger.error("No registry entry found for " + loc.toString());
	}
}
