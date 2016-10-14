package com.tom.api.recipes;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TomsModRecipeHelper {
	public static void addElectrolyzerRecipe(FluidStack input, int energyRequired, FluidStack output1, FluidStack output2, FluidStack output3, FluidStack output4){
		if(input != null && output1 != null && energyRequired > 0){
			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", energyRequired);
			NBTTagCompound inputTag = new NBTTagCompound();
			input.writeToNBT(inputTag);
			NBTTagCompound out1Tag = new NBTTagCompound();
			output1.writeToNBT(out1Tag);
			toSend.setBoolean("out2", output2 != null);
			toSend.setBoolean("out3", output3 != null);
			toSend.setBoolean("out4", output4 != null);
			toSend.setTag("input", inputTag);
			toSend.setTag("output1", out1Tag);
			if(output2 != null){
				NBTTagCompound tag = new NBTTagCompound();
				output2.writeToNBT(tag);
				toSend.setTag("output2", tag);
			}
			if(output3 != null){
				NBTTagCompound tag = new NBTTagCompound();
				output3.writeToNBT(tag);
				toSend.setTag("output3", tag);
			}
			if(output4 != null){
				NBTTagCompound tag = new NBTTagCompound();
				output4.writeToNBT(tag);
				toSend.setTag("output4", tag);
			}
			sendMessage(toSend,"addRecipe",0);
		}
	}
	public static void addElectrolyzerRecipe(FluidStack input, int energyRequired, FluidStack output1, FluidStack output2){
		addElectrolyzerRecipe(input,energyRequired, output1, output2, null, null);
	}
	public static void sendMessage(NBTTagCompound toSend, String message, int id){
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("msg", toSend);
		msg.setInteger("id", id);
		FMLInterModComms.sendMessage("TomsMod|Core", message, msg);
	}
	public static void addMultiblockCrafting(Block block1,Block block2, Block output){
		if(block1 != null && block2 != null && output != null){
			ResourceLocation b1 = block1.delegate.name();
			ResourceLocation b2 = block2.delegate.name();
			ResourceLocation out = output.delegate.name();
			NBTTagCompound toSend = new NBTTagCompound();
			NBTTagCompound block1NBT = new NBTTagCompound();
			NBTTagCompound block2NBT = new NBTTagCompound();
			NBTTagCompound blockONBT = new NBTTagCompound();
			block1NBT.setString("blockName", b1.getResourcePath());
			block1NBT.setString("modid", b1.getResourceDomain());
			block2NBT.setString("blockName", b2.getResourcePath());
			block2NBT.setString("modid", b2.getResourceDomain());
			blockONBT.setString("blockName", out.getResourcePath());
			blockONBT.setString("modid", out.getResourceDomain());
			toSend.setTag("block1", block1NBT);
			toSend.setTag("block2", block2NBT);
			toSend.setTag("blockOut", blockONBT);
			sendMessage(toSend,"addRecipe",1);
		}

	}
}
