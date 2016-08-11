package com.tom.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.tom.api.event.ItemAdvCraftedEvent;
import com.tom.api.item.ICustomCraftingHandler;
import com.tom.api.item.ICustomCraftingHandlerAdv;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.handler.WorldHandler.Action;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.network.messages.MessageMinimap;

public class EventHandler {
	public static class TileList {
		List<TileEntityTomsMod> tileList = new ArrayList<TileEntityTomsMod>();
		List<TileEntityTomsMod> tileListClient = new ArrayList<TileEntityTomsMod>();
		public void update(boolean client){
			List<TileEntityTomsMod> tileList;
			if(client){
				tileList = tileListClient;
			}else{
				tileList = this.tileList;
			}
			List<TileEntityTomsMod> invalid = new ArrayList<TileEntityTomsMod>();
			for(int i = 0;i<tileList.size();i++){
				if(tileList.get(i).isInvalid()){
					invalid.add(tileList.get(i));
				}else{
					tileList.get(i).ticked = false;
				}
			}
			tileList.removeAll(invalid);
		}
		public boolean add(TileEntityTomsMod te) {
			return tileList.add(te);
		}
		public void addClient(TileEntityTomsMod te) {
			tileListClient.add(te);
		}
	}
	public static final EventHandler instance = new EventHandler();
	public static TileList teList = new TileList();
	/*@SubscribeEvent
    public void onPlayerLoadFromFileEvent(LoadFromFile event)
    {

    }

    @SubscribeEvent
    public void onPlayerSaveToFileEvent(SaveToFile event)
    {

    }
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
    {
        if (!event.player.worldObj.isRemote)
        {

        }
    }*/
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (!event.player.worldObj.isRemote)
		{
			PlayerHandler.addPlayer(event.player.getName());
			MessageMarkerSync.sendSyncMessageTo(event.player);
		}
	}
	@SubscribeEvent
	public void onItemCraftedEvent(ItemCraftedEvent event) {
		if(event.crafting != null){
			if(event.crafting.getItem() instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler)event.crafting.getItem()).onCrafing(event.player, event.crafting, event.craftMatrix);
			else if(event.crafting.getItem() instanceof ItemBlock && ((ItemBlock)event.crafting.getItem()).block instanceof ICustomCraftingHandler)
				((ICustomCraftingHandler)((ItemBlock)event.crafting.getItem()).block).onCrafing(event.player, event.crafting, event.craftMatrix);
		}
		for(int i = 0;i<event.craftMatrix.getSizeInventory();i++){
			ItemStack s = event.craftMatrix.getStackInSlot(i);
			if(s != null){
				if(s.getItem() instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler)s.getItem()).onUsing(event.player, event.crafting, event.craftMatrix, s);
				else if(s.getItem() instanceof ItemBlock && ((ItemBlock)s.getItem()).block instanceof ICustomCraftingHandler)
					((ICustomCraftingHandler)((ItemBlock)s.getItem()).block).onCrafing(event.player, event.crafting, event.craftMatrix);
			}
		}
		if(event.crafting != null){
			Item item = event.crafting.getItem();
			if(item == Item.getItemFromBlock(CoreInit.researchTable)){
				AchievementHandler.giveAchievement(event.player, "researchTable");
			}else if(item == CoreInit.treeTap){
				AchievementHandler.giveAchievement(event.player, "treetap");
			}
		}
	}
	@SubscribeEvent
	public void onItemCraftedEvent2(ItemAdvCraftedEvent event) {
		if(event.crafting != null){
			if(event.crafting.getItem() instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv)event.crafting.getItem()).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
			else if(event.crafting.getItem() instanceof ItemBlock && ((ItemBlock)event.crafting.getItem()).block instanceof ICustomCraftingHandlerAdv)
				((ICustomCraftingHandlerAdv)((ItemBlock)event.crafting.getItem()).block).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
		}
		for(int i = 0;i<event.craftMatrix.getSizeInventory();i++){
			ItemStack s = event.craftMatrix.getStackInSlot(i);
			if(s != null){
				if(s.getItem() instanceof ICustomCraftingHandlerAdv)
					((ICustomCraftingHandlerAdv)s.getItem()).onUsingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix, s);
				else if(s.getItem() instanceof ItemBlock && ((ItemBlock)s.getItem()).block instanceof ICustomCraftingHandlerAdv)
					((ICustomCraftingHandlerAdv)((ItemBlock)s.getItem()).block).onCrafingAdv(event.player, event.crafting, event.secondStack, event.craftMatrix);
			}
		}
	}
	private EventHandler() {
		CoreInit.log.info("Loading Event Handler");
	}
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event){
		if(event.getWorld().provider.getDimension() == 0) PlayerHandler.save();
		WorldHandler.saveWorld(event.getWorld().provider.getDimension());
	}
	@SubscribeEvent
	public void breakBlockEvent(BreakEvent event){
		boolean cancel = WorldHandler.breakBlockS(event);
		if(cancel)event.setCanceled(true);
	}
	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event){
		WorldHandler.loadChunkS(event.getChunk());
	}
	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Unload event){
		WorldHandler.unloadChunkS(event.getChunk());
	}
	@SubscribeEvent
	public void dimLoad(WorldEvent.Load event){
		WorldHandler.loadWorld(event.getWorld());
	}
	@SubscribeEvent
	public void dimUnload(WorldEvent.Unload event){
		if(event.getWorld().isRemote)return;
		WorldHandler.unloadWorld(event.getWorld().provider.getDimension());
	}
	@SubscribeEvent
	public void onSpawn(WorldEvent.PotentialSpawns event){
		try{
			boolean cancel = WorldHandler.onEntitySpawning(event);
			if(cancel)event.setCanceled(true);
		}catch(Exception e){}
	}
	@SubscribeEvent
	public void placeBlock(PlaceEvent event){
		boolean cancel = WorldHandler.placeBlockS(event.getPlacedBlock(),event.getPlayer(),event.getPos());
		if(cancel)event.setCanceled(true);
	}
	@SubscribeEvent
	public void placeBlockM(MultiPlaceEvent event){
		boolean cancel = WorldHandler.placeBlockS(event.getPlacedBlock(),event.getPlayer(),event.getPos());
		if(cancel)
			event.setCanceled(true);

	}
	@SubscribeEvent
	public void tick(WorldTickEvent event){
		WorldHandler.onTick(event.world, event.phase);
	}
	@SubscribeEvent
	public void tickServer(ServerTickEvent event){
		if(event.phase == Phase.START && event.type == Type.SERVER){
			teList.update(false);
		}
	}
	@SubscribeEvent
	public void breakSpeed(BreakSpeed event){
		boolean cancel = WorldHandler.breakSpeed(event);
		if(cancel)
			event.setCanceled(true);
	}
	@SubscribeEvent
	public void interactLeftClick(PlayerInteractEvent.LeftClickBlock event){
		boolean cancel = WorldHandler.interact(event, Action.LEFT_CLICK_BLOCK);
		if(cancel)
			event.setCanceled(true);
	}
	@SubscribeEvent
	public void interact(PlayerInteractEvent.RightClickBlock event){
		boolean cancel = WorldHandler.interact(event, Action.RIGHT_CLICK_BLOCK);
		if(cancel)
			event.setCanceled(true);
	}
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		if(event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof EntityPlayer){
			if(CoreInit.isMapEnabled)NetworkHandler.sendTo(new MessageMinimap((EntityPlayer) event.getEntityLiving()), (EntityPlayerMP) event.getEntityLiving());
		}
	}
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{

	}
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (CoreInit.modids.contains(event.getModID()))
		{
			Config.updateConfig(event.isWorldRunning());
		}
	}
	@SubscribeEvent
	public void onEnderTP(EnderTeleportEvent event){
		WorldHandler.enderTeleportS(event);
	}
}
