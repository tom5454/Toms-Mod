package com.tom.handler;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import com.tom.core.CoreInit;
import com.tom.recipes.handler.ElectrolyzerRecipesHandler;
import com.tom.recipes.handler.MultiblockCrafterRecipeHandler;

public class IMCHandler {
	public static void receive(IMCMessage msg) throws Exception{
		if(msg.key.equalsIgnoreCase("addRecipe")){
			if(msg.isNBTMessage()) {
				NBTTagCompound nbt = msg.getNBTValue();
				if(nbt != null){
					int id = nbt.getInteger("id");
					NBTTagCompound tag = nbt.getCompoundTag("msg");
					if(id == 0){
						ElectrolyzerRecipesHandler.add(FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("input")), tag.getInteger("energy"),
								FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("output1")),
								tag.getBoolean("out2") ? FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("output2")) : null,
										tag.getBoolean("out3") ? FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("output3")) : null,
												tag.getBoolean("out4") ? FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("output4")) : null);
						log().debug("Electrolyzer Recipe Added");
					}else if(id == 1){
						NBTTagCompound b1 = tag.getCompoundTag("block1");
						NBTTagCompound b2 = tag.getCompoundTag("block2");
						NBTTagCompound bO = tag.getCompoundTag("blockOut");
						Block block1 = findBlock(b1.getString("modid"), b1.getString("blockName"));
						Block block2 = findBlock(b2.getString("modid"), b2.getString("blockName"));
						Block blockOut = findBlock(bO.getString("modid"), bO.getString("blockName"));
						MultiblockCrafterRecipeHandler.add(block1, block2, blockOut);
						log().debug("Multiblock Crafter Recipe Added");
					}else{
						log().error(String.format("Mod %s sent an unregistered recipe mode message. Report this to the mod author.", msg.getSender()));
					}
				}else{
					log().error(String.format("Mod %s sent a null NBT message! Report this to the mod author.", msg.getSender()));
				}
			}else{
				log().error(String.format("Mod %s sent a non-NBT message, where an NBT message was expected. Report this to the mod author.", msg.getSender()));
			}
			/*}else if(msg.key.equalsIgnoreCase("glass")){
			if(msg.isNBTMessage()) {
				NBTTagCompound nbt = msg.getNBTValue();
				if(nbt != null){
					Block block1 = findBlock(nbt.getString("modid"), nbt.getString("blockName"));
					GlobalFields.glassBlocks.put(block1, nbt.getFloat("t"));
				}else{
					log().error(String.format("Mod %s sent a null NBT message! Report this to the mod author.", msg.getSender()));
				}
			}else{
				log().error(String.format("Mod %s sent a non-NBT message, where an NBT message was expected. Report this to the mod author.", msg.getSender()));
			}*/
		}else{
			log().error(String.format("Mod %s sent an unregistered message. Report this to the mod author.", msg.getSender()));
		}
	}
	private static Block findBlock(String modid, String name) {
		return Block.REGISTRY.getObject(new ResourceLocation(modid, name));
	}
	private static Logger log(){
		return CoreInit.log;
	}
}
