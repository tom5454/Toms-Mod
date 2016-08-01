package com.tom.energy.item;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.multipart.PartDuct;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.apis.TomsModUtils;

import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class Multimeter extends Item {

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileEntity te = worldIn.getTileEntity(pos);
		if((!worldIn.isRemote) && te != null){
			ITextComponent informationMsg = new TextComponentTranslation("tomsMod.chat.multimeterInformation");
			List<ITextComponent> chatText = new ArrayList<ITextComponent>();
			if(te instanceof IEnergyStorageTile){
				IEnergyStorageTile s = (IEnergyStorageTile) te;
				List<EnergyType> eL = s.getValidEnergyTypes();
				if(eL != null){
					for(int i = 0;i<eL.size();i++){
						EnergyType c = eL.get(i);
						double energyStored = s.getEnergyStored(side, c);
						int maxEnergyStored = s.getMaxEnergyStored(side, c);
						chatText.add(new TextComponentTranslation("tomsMod.chat.energyStored",new TextComponentString(c.toString()).setStyle(new Style().setColor(c.getColor())),energyStored, maxEnergyStored));
						//chatText.add(new TextComponentString((energyStored / maxEnergyStored * 100) + "%"));
					}
				}
				if(te instanceof ICustomMultimeterInformation){
					chatText = ((ICustomMultimeterInformation)te).getInformation(chatText);
				}
				if(te instanceof ITileFluidHandler){
					ITileFluidHandler f = (ITileFluidHandler) te;
					net.minecraftforge.fluids.capability.IFluidHandler infoA = f.getTankOnSide(side);
					int i2 = 1;
					if(infoA != null){
						for(IFluidTankProperties info : infoA.getTankProperties()){
							chatText.add(new TextComponentTranslation("tomsMod.chat.tank",i2).setStyle(new Style().setColor(TextFormatting.GOLD)));
							if(info != null && info.getContents() != null){
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidName",new TextComponentTranslation(info.getContents().getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidStored",new TextComponentString(""+info.getContents().amount).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)),new TextComponentString(""+info.getCapacity()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
							}else{
								chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.empty").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true))));
							}
							i2 = i2 + 1;
						}
					}
				}
				TomsModUtils.sendNoSpam(playerIn, sortMessages(chatText, informationMsg, new TextComponentTranslation(te.getBlockType().getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
			}else if(te instanceof ITileFluidHandler){
				ITileFluidHandler f = (ITileFluidHandler) te;
				net.minecraftforge.fluids.capability.IFluidHandler infoA = f.getTankOnSide(side);
				int i2 = 1;
				if(infoA != null){
					for(IFluidTankProperties info : infoA.getTankProperties()){
						chatText.add(new TextComponentTranslation("tomsMod.chat.tank",i2).setStyle(new Style().setColor(TextFormatting.GOLD)));
						if(info != null && info.getContents() != null){
							chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidName",new TextComponentTranslation(info.getContents().getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
							chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidStored",new TextComponentString(""+info.getContents().amount).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)),new TextComponentString(""+info.getCapacity()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
						}else{
							chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.empty").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true))));
						}
						i2 = i2 + 1;
					}
					if(te instanceof ICustomMultimeterInformation){
						chatText = ((ICustomMultimeterInformation)te).getInformation(chatText);
					}
					TomsModUtils.sendNoSpam(playerIn, sortMessages(chatText, informationMsg, new TextComponentTranslation(te.getBlockType().getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
				}
			}else if(te instanceof ICustomMultimeterInformation){
				TomsModUtils.sendNoSpam(playerIn, sortMessages(((ICustomMultimeterInformation)te).getInformation(chatText), informationMsg, new TextComponentTranslation(te.getBlockType().getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
			}else{
				IMultipartContainer container = MultipartHelper.getPartContainer(worldIn,pos);
				if (container == null) {
					return EnumActionResult.FAIL;
				}
				ISlottedPart partP = container.getPartInSlot(PartSlot.CENTER);
				if(partP instanceof PartDuct){
					PartDuct<?> part = (PartDuct<?>) partP;
					if(part instanceof IEnergyStorageTile){
						IEnergyStorageTile s = (IEnergyStorageTile) part;
						List<EnergyType> eL = s.getValidEnergyTypes();

						for(int i = 0;i<eL.size();i++){
							EnergyType c = eL.get(i);
							chatText.add(new TextComponentTranslation("tomsMod.chat.energyStored",new TextComponentString(c.toString()).setStyle(new Style().setColor(c.getColor())),s.getEnergyStored(side, c),s.getMaxEnergyStored(side, c)));
						}
						if(part instanceof ICustomMultimeterInformation){
							chatText = ((ICustomMultimeterInformation)part).getInformation(chatText);
						}
						if(part instanceof ITileFluidHandler){
							ITileFluidHandler f = (ITileFluidHandler) part;
							net.minecraftforge.fluids.capability.IFluidHandler infoA = f.getTankOnSide(side);
							int i2 = 1;
							if(infoA != null){
								for(IFluidTankProperties info : infoA.getTankProperties()){
									chatText.add(new TextComponentTranslation("tomsMod.chat.tank",i2).setStyle(new Style().setColor(TextFormatting.GOLD)));
									if(info != null && info.getContents() != null){
										chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidName",new TextComponentTranslation(info.getContents().getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
										chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidStored",new TextComponentString(""+info.getContents().amount).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)),new TextComponentString(""+info.getCapacity()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
									}else{
										chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.empty").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true))));
									}
									i2 = i2 + 1;
								}
							}
						}
						TomsModUtils.sendNoSpam(playerIn, sortMessages(chatText, informationMsg, new TextComponentTranslation(part.pick.getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
					}else if(part instanceof ITileFluidHandler){
						ITileFluidHandler f = (ITileFluidHandler) part;
						net.minecraftforge.fluids.capability.IFluidHandler infoA = f.getTankOnSide(side);
						int i2 = 1;
						if(infoA != null){
							for(IFluidTankProperties info : infoA.getTankProperties()){
								chatText.add(new TextComponentTranslation("tomsMod.chat.tank",i2).setStyle(new Style().setColor(TextFormatting.GOLD)));
								if(info != null && info.getContents() != null){
									chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidName",new TextComponentTranslation(info.getContents().getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
									chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.fluidStored",new TextComponentString(""+info.getContents().amount).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)),new TextComponentString(""+info.getCapacity()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))));
								}else{
									chatText.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.empty").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true))));
								}
								i2 = i2 + 1;
							}
							if(part instanceof ICustomMultimeterInformation){
								chatText = ((ICustomMultimeterInformation)part).getInformation(chatText);
							}
							TomsModUtils.sendNoSpam(playerIn, sortMessages(chatText, informationMsg, new TextComponentTranslation(part.pick.getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
						}
					}else if(part instanceof ICustomMultimeterInformation){
						TomsModUtils.sendNoSpam(playerIn, sortMessages(((ICustomMultimeterInformation)part).getInformation(chatText), informationMsg, new TextComponentTranslation(part.pick.getUnlocalizedName()+".name")).toArray(new ITextComponent[]{}));
					}
				}else{
					return EnumActionResult.FAIL;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	protected static List<ITextComponent> sortMessages(List<ITextComponent> in, ITextComponent header, ITextComponent name){
		List<ITextComponent> ret = new ArrayList<ITextComponent>();
		ret.add(header);
		ret.add(new TextComponentTranslation("tomsMod.chat.tabulator",new TextComponentTranslation("tomsMod.chat.name",name.setStyle(new Style().setColor(TextFormatting.GOLD)))));
		for(ITextComponent c : in){
			ret.add(new TextComponentTranslation("tomsMod.chat.tabulator",c));
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
