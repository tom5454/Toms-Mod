package com.tom.defense.item;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IIconRegisterRequired;
import com.tom.api.energy.ItemEnergyContainer;
import com.tom.api.item.IConfigurator;
import com.tom.api.item.ISwitch;
import com.tom.api.item.IWrench;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.handler.ConfiguratorHandler;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.handler.WrenchHandler;

public class ItemMultiTool extends ItemEnergyContainer implements IWrench, ISwitch, IConfigurator, IIconRegisterRequired {
	private static final double CONFIGURATOR_USAGE = 0.1;
	private static final double TRANSPORTER_USAGE = 500;
	public ItemMultiTool() {
		super(10000, 140);
		this.setCreativeTab(DefenseInit.tabTomsModDefense);
		this.setUnlocalizedName("tm.multitool");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
	}

	@Override
	public boolean isWrench(ItemStack is, EntityPlayer player) {
		return MultiToolType.get(is.getItemDamage()) == MultiToolType.WRENCH;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack)+"."+MultiToolType.get(stack.getItemDamage()).name;
	}
	public static enum MultiToolType{
		WRENCH("wrench"),
		SWITCH("switch"),
		TRANSPORTER("transporter"),
		ENCODER("writer"),
		CONFIGURATOR("configurator"),
		;
		public static final MultiToolType[] VALUES = values();
		private final String name;
		private MultiToolType(String name) {
			this.name = name;
		}
		public static MultiToolType get(int index){
			return VALUES[index % VALUES.length];
		}
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		double energy = this.getEnergyStored(stack);
		double per = energy * 100 / capacity;
		int p = MathHelper.floor_double(per);
		tooltip.add(I18n.format("tomsMod.tooltip.charge") + ": "+this.getMaxEnergyStored(stack)+"/"+energy+" "+p+"%");
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1-this.getPercentStored(stack);
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(stack.getTagCompound() != null && stack.getTagCompound().hasKey("isInCreativeTabIcon"));
	}
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab,
			List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		ItemStack is = new ItemStack(itemIn, 1, 0);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("Energy", capacity);
		is.setTagCompound(tag);
		subItems.add(is);
	}
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(this.isWrench(stack, playerIn)){
			return WrenchHandler.use(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, hand) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		}else if(this.isConfigurator(stack, playerIn)){
			if(hand == EnumHand.MAIN_HAND)return ConfiguratorHandler.openConfigurator(stack, playerIn, worldIn, pos) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			else
				TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), stack.getUnlocalizedName()+".name", "tomsMod.invalidHandUseMain");
		}else if(MultiToolType.get(stack.getItemDamage()) == MultiToolType.TRANSPORTER){
			if(!worldIn.isRemote){
				IBlockState blockState = worldIn.getBlockState(pos);
				if(blockState != null && blockState.getBlock() == DefenseInit.blockForce){
					TileEntity tile = worldIn.getTileEntity(pos);
					if(tile instanceof TileEntityForceField){
						TileEntityForceField f = (TileEntityForceField) tile;
						if(f.ownerPos != null){
							TileEntity te = worldIn.getTileEntity(f.ownerPos);
							if(te instanceof TileEntityForceFieldProjector){
								TileEntityForceFieldProjector p = (TileEntityForceFieldProjector) te;
								BlockPos secPos = p.getSecurityStationPos();
								if(secPos != null){
									TileEntity teS = worldIn.getTileEntity(secPos);
									if(teS instanceof ISecurityStation){
										ISecurityStation s = (ISecurityStation) teS;
										if(s.canPlayerAccess(AccessType.FIELD_TRANSPORT, playerIn)){
											//TomsModUtils.sendAccessDeniedMessageToWithTag(playerIn, "item.tm.efficiencyUpgrade.name");
											teleportPlayer(playerIn, pos, side, stack);
										}else{
											TomsModUtils.sendAccessDeniedMessageTo(playerIn, "tomsMod.chat.fieldSecurity");
										}
									}else{
										//TomsModUtils.sendAccessDeniedMessageToWithTag(playerIn, "item.tm.efficiencyUpgrade.name");
										teleportPlayer(playerIn, pos, side, stack);
									}
								}else{
									//TomsModUtils.sendAccessDeniedMessageToWithTag(playerIn, "item.tm.efficiencyUpgrade.name");
									teleportPlayer(playerIn, pos, side, stack);
								}
							}
						}
					}
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	private void teleportPlayer(EntityPlayer playerIn, BlockPos pos, EnumFacing side, ItemStack stack) {
		if(this.getEnergyStored(stack) >= TRANSPORTER_USAGE){
			BlockPos pos2 = pos.offset(side.getOpposite(), side == EnumFacing.UP ? 3 : 2);
			IBlockState state = playerIn.worldObj.getBlockState(pos2);
			if(state == null || state.getBlock() == null || state.getBlock().isPassable(playerIn.worldObj, pos2)){
				TomsModUtils.sendAccessGrantedMessageToWithExtraInformation(playerIn, stack.getUnlocalizedName()+".name", "tomsMod.transmissionC");
				EntityPlayerMP mp = (EntityPlayerMP) playerIn;
				mp.connection.setPlayerLocation(pos2.getX()+0.5, pos2.getY()+0.5, pos2.getZ()+0.5, playerIn.rotationYaw, playerIn.rotationPitch);
				if(!playerIn.capabilities.isCreativeMode)this.extractEnergy(stack, TRANSPORTER_USAGE, false);
			}else{
				TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), stack.getUnlocalizedName()+".name", "tomsMod.detectedObstacle");
			}
		}else{
			TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), stack.getUnlocalizedName()+".name", "tomsMod.notEnoughPower");
		}
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn,
			World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(playerIn.isSneaking())itemStackIn.setItemDamage(MultiToolType.get(itemStackIn.getItemDamage()+1).ordinal());
		else{
			if(!worldIn.isRemote){
				if(MultiToolType.get(itemStackIn.getItemDamage()) == MultiToolType.ENCODER)
					if(hand == EnumHand.MAIN_HAND)
						playerIn.openGui(CoreInit.modInstance, GuiIDs.multitoolWriter.ordinal(), worldIn, 0,0,0);
					else
						TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), itemStackIn.getUnlocalizedName()+".name", "tomsMod.invalidHandUseMain");
			}
		}
		//playerIn.posX += 10;
		//playerIn.posZ += 10;
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public boolean isSwitch(ItemStack stack, EntityPlayer player) {
		return MultiToolType.get(stack.getItemDamage()) == MultiToolType.SWITCH;
	}

	@Override
	public boolean isConfigurator(ItemStack stack, EntityPlayer player) {
		return MultiToolType.get(stack.getItemDamage()) == MultiToolType.CONFIGURATOR;
	}

	@Override
	public boolean use(ItemStack stack, EntityPlayer player, boolean simulate) {
		double extracted = this.extractEnergy(stack, CONFIGURATOR_USAGE, simulate);
		return extracted == CONFIGURATOR_USAGE;
	}
	public void setOpenGuiNextTick(ItemStack stack, NBTTagCompound tag){
		if(stack.getTagCompound() == null)stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setBoolean("openGui", true);
		if(tag != null)stack.getTagCompound().setTag("openGuiExtra", tag);
		//System.out.println(tag);
		//new Throwable().printStackTrace();
	}
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {
		if(!worldIn.isRemote && entityIn instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entityIn;
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().getBoolean("openGui")){
					stack.getTagCompound().setBoolean("openGui", false);
					stack.getTagCompound().setBoolean("hasExtra", true);
					if(isSelected){
						player.openGui(CoreInit.modInstance, GuiIDs.projectorLensConfigMain.ordinal(), worldIn, 0,0,0);
					}
				}
			}
		}
	}
	public ItemStack getLensStack(ItemStack is, EntityPlayer player){
		NBTTagCompound compound = is.hasTagCompound() ? is.getTagCompound() : new NBTTagCompound();
		ItemStack[] stack = new ItemStack[3];
		NBTTagList list = compound.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < stack.length)
			{
				stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		ItemStack lens = stack[2];
		if(lens != null && is.getTagCompound() != null){
			if(lens.getTagCompound() == null)lens.setTagCompound(new NBTTagCompound());
			lens.getTagCompound().setTag("extra", is.getTagCompound().getCompoundTag("openGuiExtra"));
			lens.getTagCompound().setBoolean("hasExtra", is.getTagCompound().getBoolean("hasExtra"));
			is.getTagCompound().setBoolean("hasExtra", false);
		}
		/*if(lens == null && player != null && player.worldObj != null && player.worldObj.isRemote && player.openContainer != null){
			Slot slot = player.openContainer.getSlot(2);
			if(slot != null){
				lens = slot.getStack();
			}
		}*/
		return lens;
	}
	public void setLensStack(ItemStack is, ItemStack lensStack){
		NBTTagCompound compound = is.hasTagCompound() ? is.getTagCompound() : new NBTTagCompound();
		ItemStack[] stack = new ItemStack[3];
		NBTTagList list = compound.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < stack.length)
			{
				stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		stack[2] = lensStack;
		list = new NBTTagList();
		for(int j = 0;j<stack.length;j++){
			if(stack[j] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[j].writeToNBT(tag);
				tag.setByte("Slot", (byte) j);
				list.appendTag(tag);
			}
		}
		if(!is.hasTagCompound())is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setTag("inventory", list);
	}

	@Override
	public void registerIcons() {
		CoreInit.registerRender(this, 0, "tomsmoddefense:"+getUnlocalizedName(new ItemStack(this,1,0)).substring(5));
		CoreInit.registerRender(this, 1, "tomsmoddefense:"+getUnlocalizedName(new ItemStack(this,1,1)).substring(5));
		CoreInit.registerRender(this, 2, "tomsmoddefense:"+getUnlocalizedName(new ItemStack(this,1,2)).substring(5));
		CoreInit.registerRender(this, 3, "tomsmoddefense:"+getUnlocalizedName(new ItemStack(this,1,3)).substring(5));
		CoreInit.registerRender(this, 4, "tomsmoddefense:"+getUnlocalizedName(new ItemStack(this,1,4)).substring(5));
	}
}
