package com.tom.energy.item;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.multipart.PartDuct;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.core.CoreInit;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.storage.multipart.PartStorageNetworkCable;
import com.tom.util.TomsModUtils;

import mcmultipart.RayTraceHelper;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IMultipartContainerBlock;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;

public class Multimeter extends Item {

	@SuppressWarnings("deprecation")
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity te = worldIn.getTileEntity(pos);
		if ((!worldIn.isRemote) && te != null) {
			IBlockState state = worldIn.getBlockState(pos);
			ITextComponent informationMsg = new TextComponentTranslation("tomsMod.chat.multimeterInformation");
			List<ITextComponent> chatText = new ArrayList<>();
			if (te instanceof IEnergyStorageTile) {
				IEnergyStorageTile s = (IEnergyStorageTile) te;
				List<EnergyType> eL = s.getValidEnergyTypes();
				if (eL != null) {
					for (int i = 0;i < eL.size();i++) {
						EnergyType c = eL.get(i);
						double energyStored = s.getEnergyStored(side, c);
						long maxEnergyStored = s.getMaxEnergyStored(side, c);
						chatText.add(new TextComponentTranslation("tomsMod.chat.energyStored", new TextComponentString(c.toString()).setStyle(new Style().setColor(c.getColor())), energyStored, maxEnergyStored));
						// chatText.add(new TextComponentString((energyStored /
						// maxEnergyStored * 100) + "%"));
					}
				}
			}
			if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
				IFluidHandler infoA = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
				int i2 = 1;
				if (infoA != null) {
					IFluidTankProperties[] props = infoA.getTankProperties();
					if (props != null) {
						for (IFluidTankProperties info : props) {
							chatText.add(new TextComponentTranslation("tomsMod.chat.tank", i2).setStyle(new Style().setColor(TextFormatting.GOLD)));
							if (info != null && info.getContents() != null) {
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.fluidName", new TextComponentTranslation(info.getContents().getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.fluidStored", new TextComponentString("" + info.getContents().amount).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)), new TextComponentString("" + info.getCapacity()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
							} else {
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.empty").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true))));
							}
							i2 = i2 + 1;
						}
					}
				}
			}
			if (te instanceof ICustomMultimeterInformation) {
				chatText = ((ICustomMultimeterInformation) te).getInformation(chatText);
			}
			if (te instanceof PartStorageNetworkCable) {
				PartStorageNetworkCable c = (PartStorageNetworkCable) te;
				chatText.add(new TextComponentString(c.getGrid().getData().toString()));
				chatText.add(new TextComponentString(c.getGrid().getData().getPowerCache().toString()));
			}
			if (CoreInit.isDebugging && te instanceof IGridDevice) {
				IGridDevice<?> c = (IGridDevice<?>) te;
				chatText.add(new TextComponentString("" + c.isMaster()));
			}
			if (CoreInit.isDebugging)
				chatText.add(new TextComponentString("Invalid: " + te.isInvalid()));
			if (worldIn.getBlockState(pos).getBlock() instanceof IMultipartContainerBlock) {
				IMultipartContainer container = MultipartHelper.getContainer(worldIn, pos).orElse(null);
				if (container == null) { return EnumActionResult.FAIL; }
				IPartInfo partPi = container.get(EnumCenterSlot.CENTER).orElse(null);
				if (partPi != null) {
					IMultipartTile partP = partPi.getTile();
					if (partP instanceof PartDuct) {
						PartDuct<?> part = (PartDuct<?>) partP;
						if (CoreInit.isDebugging && part instanceof PartStorageNetworkCable) {
							PartStorageNetworkCable c = (PartStorageNetworkCable) part;
							chatText.add(new TextComponentString(c.getGrid().getData().toString()));
							chatText.add(new TextComponentString(c.getGrid().getData().getPowerCache().toString()));
						}
						if (part instanceof IEnergyStorageTile) {
							IEnergyStorageTile s = (IEnergyStorageTile) part;
							List<EnergyType> eL = s.getValidEnergyTypes();

							for (int i = 0;i < eL.size();i++) {
								EnergyType c = eL.get(i);
								chatText.add(new TextComponentTranslation("tomsMod.chat.energyStored", new TextComponentString(c.toString()).setStyle(new Style().setColor(c.getColor())), s.getEnergyStored(side, c), s.getMaxEnergyStored(side, c)));
							}
						}
						if (part instanceof ICustomMultimeterInformation) {
							chatText = ((ICustomMultimeterInformation) part).getInformation(chatText);
						}
					}
					if (CoreInit.isDebugging && partP instanceof IGridDevice) {
						IGridDevice<?> c = (IGridDevice<?>) partP;
						chatText.add(new TextComponentString("" + c.isMaster()));
					}
					if (CoreInit.isDebugging && partP instanceof TileEntity)
						chatText.add(new TextComponentString("Invalid: " + ((TileEntity) partP).isInvalid()));
				}
			}
			if (chatText.isEmpty())
				return EnumActionResult.FAIL;
			Pair<Vec3d, Vec3d> vec = RayTraceHelper.getRayTraceVectors(playerIn);
			ItemStack pick = state.getBlock().getPickBlock(state, state.getBlock().collisionRayTrace(state, worldIn, pos, vec.getKey(), vec.getValue()), worldIn, pos, playerIn);
			TomsModUtils.sendNoSpam(playerIn, sortMessages(chatText, informationMsg, pick.getTextComponent()).toArray(new ITextComponent[]{}));
		}
		return EnumActionResult.SUCCESS;
	}

	protected static List<ITextComponent> sortMessages(List<ITextComponent> in, ITextComponent header, ITextComponent name) {
		List<ITextComponent> ret = new ArrayList<>();
		ret.add(header);
		name.getStyle().setColor(TextFormatting.GOLD);
		ret.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.name", name)));
		for (ITextComponent c : in) {
			ret.add(new TextComponentTranslation("tomsMod.chat.tabulator", c));
		}
		return ret;
	}
	/*boolean gen = false;
	boolean injector = false;
	if(te instanceof TileEntityGenerator){
		TileEntityGenerator g = (TileEntityGenerator) te;
		if(g.fuelStack != null){
			if(g.fuel > 0){
				chatText = new ITextComponent[eL.size()+3];
			}else{
				chatText = new ITextComponent[eL.size()+1];
			}
		}else if(g.fuel > 0){
			chatText = new ITextComponent[eL.size()+2];
		}
		gen = true;
	}else if(te instanceof TileEntityFusionInjector){
		chatText = new ITextComponent[eL.size()+1];
	}*/
	/*if(gen){
	TileEntityGenerator g = (TileEntityGenerator) te;//itemstack.getTextComponent()
	if(g.fuelStack != null){
		if(g.fuel > 0){
			chatText[chatText.length-3] = new TextComponentTranslation("tomsMod.chat.burnTime",g.fuel);
			chatText[chatText.length-2] = new TextComponentTranslation("tomsMod.chat.currentlyBurning",g.currentlyBurning != null ? g.currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na"));
			chatText[chatText.length-1] = new TextComponentTranslation("tomsMod.chat.inventory",g.fuelStack != null ? g.fuelStack.getTextComponent() : new TextComponentTranslation("tomsMod.na"));
		}else{
			chatText[chatText.length-1] = new TextComponentTranslation("tomsMod.chat.inventory",g.fuelStack != null ? g.fuelStack.getTextComponent() : new TextComponentTranslation("tomsMod.na"));
		}
	}else if(g.fuel > 0){
		chatText[chatText.length-2] = new TextComponentTranslation("tomsMod.chat.burnTime",g.fuel);
		chatText[chatText.length-1] = new TextComponentTranslation("tomsMod.chat.currentlyBurning",g.currentlyBurning != null ? g.currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na"));
	}
	//chatText[chatText.length-1] = new TextComponentTranslation("tomsMod.chat.burnTime",g.fuel);
	}*/
}
