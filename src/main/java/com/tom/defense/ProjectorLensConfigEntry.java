package com.tom.defense;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fluids.IFluidBlock;

import com.tom.api.IValidationChecker;
import com.tom.apis.TomsModUtils;
import com.tom.defense.ProjectorLensConfigEntry.CompiledProjectorLensConfigEntry.ProjectorLensUpgradeList;
import com.tom.defense.item.ItemFieldUpgrade.UpgradeType;
import com.tom.defense.item.ItemProjectorFieldType.FieldType;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.handler.WorldHandler;

public class ProjectorLensConfigEntry {
	private String name;
	private IInventory inventoryStack = new InventoryBasic("", false, 1);
	private IInventory inventoryUpgrades = new InventoryBasic("", false, 27);
	private int offsetX, offsetY, offsetZ;

	private ProjectorLensConfigEntry() {
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", getName());
		NBTTagCompound sTag = new NBTTagCompound();
		if (inventoryStack.getStackInSlot(0) != null)
			inventoryStack.getStackInSlot(0).writeToNBT(sTag);
		tag.setTag("itemStack", sTag);
		tag.setInteger("offsetX", offsetX);
		tag.setInteger("offsetY", offsetY);
		tag.setInteger("offsetZ", offsetZ);
		tag.setTag("inventory", TomsModUtils.saveAllItems(inventoryUpgrades));
	}

	public static ProjectorLensConfigEntry fromNBT(NBTTagCompound tag) {
		ProjectorLensConfigEntry e = new ProjectorLensConfigEntry();
		e.setName(tag.getString("name"));
		e.inventoryStack.setInventorySlotContents(0, TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("itemStack")));
		TomsModUtils.loadAllItems(tag.getTagList("inventory", 10), e.inventoryUpgrades);
		e.offsetX = tag.getInteger("offsetX");
		e.offsetY = tag.getInteger("offsetY");
		e.offsetZ = tag.getInteger("offsetZ");
		return e;
	}

	public void writeToClientNBTPacket(NBTTagCompound tag) {
		tag.setString("name", getName());
		NBTTagCompound sTag = new NBTTagCompound();
		if (inventoryStack.getStackInSlot(0) != null)
			inventoryStack.getStackInSlot(0).writeToNBT(sTag);
		tag.setTag("itemStack", sTag);
		tag.setInteger("oX", offsetX);
		tag.setInteger("oY", offsetY);
		tag.setInteger("oZ", offsetZ);
	}

	public static ProjectorLensConfigEntry fromNBTClient(NBTTagCompound tag) {
		ProjectorLensConfigEntry e = new ProjectorLensConfigEntry();
		e.setName(tag.getString("name"));
		e.inventoryStack.setInventorySlotContents(0, TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("itemStack")));
		e.inventoryUpgrades.clear();
		e.offsetX = tag.getInteger("oX");
		e.offsetY = tag.getInteger("oY");
		e.offsetZ = tag.getInteger("oZ");
		return e;
	}

	public static ProjectorLensConfigEntry createNew() {
		ProjectorLensConfigEntry e = new ProjectorLensConfigEntry();
		e.setName("New");
		e.offsetX = 0;
		e.offsetY = 0;
		e.offsetZ = 0;
		return e;
	}

	public void dropItems(EntityPlayer player) {
		this.dropMainItem(player);
		this.dropUpgrades(player);
	}

	public void dropUpgrades(EntityPlayer player) {
		for (int i = 0;i < inventoryUpgrades.getSizeInventory();i++) {
			ItemStack s = inventoryUpgrades.getStackInSlot(i);
			if (!s.isEmpty())
				player.dropItem(s, false);
		}
		inventoryUpgrades.clear();
	}

	public void dropMainItem(EntityPlayer player) {
		if (!inventoryStack.getStackInSlot(0).isEmpty()) {
			player.dropItem(inventoryStack.getStackInSlot(0), false);
			inventoryStack.clear();
		}
	}

	public String getName() {
		return name;
	}

	public ItemStack getStack() {
		return inventoryStack.getStackInSlot(0);
	}

	public IInventory getInventory(boolean isUpgradeInventory) {
		return isUpgradeInventory ? inventoryUpgrades : inventoryStack;
	}

	public FieldType getFieldType() {
		if (!inventoryStack.getStackInSlot(0).isEmpty() && inventoryStack.getStackInSlot(0).getItem() == DefenseInit.projectorFieldType) { return FieldType.get(inventoryStack.getStackInSlot(0).getItemDamage()); }
		return null;
	}

	public IInventory getUpgrades() {
		return inventoryUpgrades;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getOffsetZ() {
		return offsetZ;
	}

	public void setOffsetZ(int offsetZ) {
		this.offsetZ = offsetZ;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CompiledProjectorLensConfigEntry compile() {
		CompiledProjectorLensConfigEntry c = new CompiledProjectorLensConfigEntry();
		c.offsetX = offsetX;
		c.offsetY = offsetY;
		c.offsetZ = offsetZ;
		c.fieldType = getFieldType();
		c.xSize = inventoryUpgrades.getStackInSlot(0) != null ? inventoryUpgrades.getStackInSlot(0).getCount() : 0;
		c.ySize = inventoryUpgrades.getStackInSlot(2) != null ? inventoryUpgrades.getStackInSlot(2).getCount() : 0;
		c.zSize = inventoryUpgrades.getStackInSlot(1) != null ? inventoryUpgrades.getStackInSlot(1).getCount() : 0;
		c.upgrades = new ProjectorLensUpgradeList[6];
		for (int i = 0;i < 6;++i) {
			ProjectorLensUpgradeList list = new ProjectorLensUpgradeList();
			for (int j = 0;j < 4;++j) {
				ItemStack stack = inventoryUpgrades.getStackInSlot(j + i * 4 + 3);
				if (stack != null && stack.getItem() == DefenseInit.fieldUpgrade) {
					UpgradeType t = UpgradeType.get(stack.getItemDamage());
					switch (t) {
					case BREAK_BLOCK:
						list.hasBlockBreakingUpgrade = true;
						break;
					case FUSION:
						list.hasFieldFusion = true;
						break;
					case SPONGE:
						list.hasSponge = true;
						break;
					case ZAPPER:
						list.hasZapper = true;
						break;
					default:
						break;
					}
				}
			}
			c.upgrades[i] = list;
		}
		c.extraNBTData = !this.inventoryStack.getStackInSlot(0).isEmpty() && inventoryStack.getStackInSlot(0).getTagCompound() != null ? inventoryStack.getStackInSlot(0).getTagCompound().getInteger("extra") : 0;
		return c;
	}

	public static class CompiledProjectorLensConfigEntry {
		private int offsetX, offsetY, offsetZ, xSize, ySize, zSize, extraNBTData;
		private FieldType fieldType;
		private ProjectorLensUpgradeList[] upgrades;

		public int getOffsetX() {
			return offsetX;
		}

		public void setOffsetX(int offsetX) {
			this.offsetX = offsetX;
		}

		public int getOffsetY() {
			return offsetY;
		}

		public void setOffsetY(int offsetY) {
			this.offsetY = offsetY;
		}

		public int getOffsetZ() {
			return offsetZ;
		}

		public void setOffsetZ(int offsetZ) {
			this.offsetZ = offsetZ;
		}

		public int getXSize() {
			return xSize;
		}

		public void setXSize(int xSize) {
			this.xSize = xSize;
		}

		public int getYSize() {
			return ySize;
		}

		public void setYSize(int ySize) {
			this.ySize = ySize;
		}

		public int getZSize() {
			return zSize;
		}

		public void setZSize(int zSize) {
			this.zSize = zSize;
		}

		public ProjectorLensUpgradeList[] getUpgrades() {
			return upgrades;
		}

		public FieldType getFieldType() {
			return fieldType;
		}

		public BuiltProjectorLensConfigEntry build(BlockPos posMiddle, BlockPos master) {
			if (this.fieldType != null) {
				BlockPos posStart = posMiddle.add(offsetX, offsetY, offsetZ);
				BlockPos posStop = posStart.add(xSize, ySize, zSize);
				AxisAlignedBB bb = new AxisAlignedBB(posStart, posStop);
				BuiltProjectorLensConfigEntry e = new BuiltProjectorLensConfigEntry();
				// Entry<Entry<AxisAlignedBB,ProjectorLensUpgradeList[]>,List<FieldBlockType>>
				// entry = new
				// EmptyEntry<Entry<AxisAlignedBB,ProjectorLensUpgradeList[]>,List<FieldBlockType>>(new
				// EmptyEntry<AxisAlignedBB, ProjectorLensUpgradeList[]>(bb,
				// upgrades));
				e.bounds = bb;
				e.upgrades = upgrades;
				Iterable<BlockPos> blockPosList = BlockPos.getAllInBox(posStart, posStop);
				List<FieldBlockType> blockList = new ArrayList<>();
				for (BlockPos pos : blockPosList) {
					int[] oArray = fieldType.getWall(pos, posStart, posStop, extraNBTData);
					int o = oArray[1];
					int uId = oArray[0];
					ProjectorLensUpgradeList l = upgrades[MathHelper.abs(uId - 1)];
					FieldBlockType b = new FieldBlockType(l.hasZapper ? 2 : (fieldType == FieldType.CONTAINMENT ? 1 : 0), pos, master, l, o == 1, o == 0);
					blockList.add(b);
				}
				// entry.setValue(blockList);
				e.blocks = blockList;
				// e.extraNBTData = extraNBTData;
				return e;
			}
			return null;
		}

		public static class ProjectorLensUpgradeList {
			private boolean hasFieldFusion, hasZapper, hasBlockBreakingUpgrade, hasSponge;

			public boolean hasFieldFusion() {
				return hasFieldFusion;
			}

			public boolean hasZapper() {
				return hasZapper;
			}

			public boolean hasSponge() {
				return hasSponge;
			}

			public boolean hasBlockBreakingUpgrade() {
				return hasBlockBreakingUpgrade;
			}
		}

		private CompiledProjectorLensConfigEntry() {
		}
	}

	public static class BuiltProjectorLensConfigEntry {
		private AxisAlignedBB bounds;
		private ProjectorLensUpgradeList[] upgrades;
		private List<FieldBlockType> blocks;

		// private int extraNBTData;
		public AxisAlignedBB getBounds() {
			return bounds;
		}

		public void setBounds(AxisAlignedBB bounds) {
			this.bounds = bounds;
		}

		public ProjectorLensUpgradeList[] getUpgrades() {
			return upgrades;
		}

		public void setUpgrades(ProjectorLensUpgradeList[] upgrades) {
			this.upgrades = upgrades;
		}

		public List<FieldBlockType> getBlocks() {
			return blocks;
		}

		public void setBlocks(List<FieldBlockType> blocks) {
			this.blocks = blocks;
		}

		public boolean hasFieldFusion() {
			for (int i = 0;i < upgrades.length;i++) {
				if (upgrades[i].hasFieldFusion)
					return true;
			}
			return false;
		}
	}

	public static class CompiledProjectorConfig {
		private List<FieldBlockType> blockList;
		private List<CompiledProjectorLensConfigEntry> entryList = new ArrayList<>();
		private BlockPos masterPos;
		private AxisAlignedBB box;
		private boolean valid = true;

		public int build(World world) {
			int energy = 0;
			for (int i = 0;i < blockList.size();i++) {
				FieldBlockType t = blockList.get(i);
				energy += t.getEnergy();
				if (t.placeBlock(world)) {
					energy += t.getEnergy();
				}
			}
			return energy;
		}

		public void destroy(World world) {
			for (int i = 0;i < blockList.size();i++) {
				blockList.get(i).destroy(world);
			}
			valid = false;
		}

		public void compile(BlockPos pos) {
			List<BuiltProjectorLensConfigEntry> blockList = new ArrayList<>();
			List<BuiltProjectorLensConfigEntry> blockListF = new ArrayList<>();
			List<BuiltProjectorLensConfigEntry> blockListN = new ArrayList<>();
			AxisAlignedBB box = null;
			// List<BlockPos> blackListedPositions = new ArrayList<BlockPos>();
			for (int i = 0;i < entryList.size();i++) {
				BuiltProjectorLensConfigEntry b = entryList.get(i).build(pos, masterPos);
				blockList.add(b);
				if (box == null)
					box = b.bounds.expand(0, 0, 0);
				else
					box = addBox(box, b.bounds);
				// bounds.add(b.bounds);
				if (b.hasFieldFusion())
					blockListF.add(b);
				else
					blockListN.add(b);
			}
			this.blockList = new ArrayList<>();
			/*for(int i = 0;i<blockListF.size();i++){
				BuiltProjectorLensConfigEntry e = blockListF.get(i);
				List<BuiltProjectorLensConfigEntry> intersectsWith = getIntersects(blockList, e.bounds);
				if(!intersectsWith.isEmpty()){
					for(int j = 0;j<intersectsWith.size();j++){
						Iterable<BlockPos> blockPosListO = TomsModUtils.getAllBlockPosInBounds(e.bounds);
						BuiltProjectorLensConfigEntry in = intersectsWith.get(j);
						Iterable<BlockPos> blockPosListN = TomsModUtils.getAllBlockPosInBounds(in.bounds);
						for(BlockPos posO : blockPosListO){
							if(in.bounds.isVecInside(new Vec3(posO))){
								//blackListedPositions.add(posO);
								in.blocks.remove(new FieldBlockTypeComparator(posO));
							}
						}
						for(BlockPos posO : blockPosListN){
							if(e.bounds.isVecInside(new Vec3(posO))){
								//if(!blackListedPositions.contains(posO))blackListedPositions.add(posO);
								e.blocks.remove(new FieldBlockTypeComparator(posO));
							}
						}
					}
				}
			}*/
			for (int i = 0;i < blockList.size();i++) {
				BuiltProjectorLensConfigEntry e = blockList.get(i);
				/*for(int j = 0;j<e.blocks.size();i++){
					FieldBlockType f = e.blocks.get(j);
					if(!blackListedPositions.contains(f.pos))this.blockList.add(f);
				}*/
				this.blockList.addAll(e.blocks);
			}
			this.box = box;
			/*Entry<AxisAlignedBB,ProjectorLensUpgradeList[]> cutE = mainE.getKey();
				List<FieldBlockType> list = mainE.getValue();
				bounds.add(cutE.getKey());*/
		}

		private AxisAlignedBB addBox(AxisAlignedBB normal, AxisAlignedBB toAdd) {
			double minX = normal.minX, minY = normal.minY, minZ = normal.minZ;
			double maxX = normal.maxX, maxY = normal.maxY, maxZ = normal.maxZ;
			minX = Math.min(minX, toAdd.minX);
			minY = Math.min(minY, toAdd.minY);
			minZ = Math.min(minZ, toAdd.minZ);
			maxX = Math.min(maxX, toAdd.maxX);
			maxY = Math.min(maxY, toAdd.maxY);
			maxZ = Math.min(maxZ, toAdd.maxZ);
			return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}

		public void addPart(CompiledProjectorLensConfigEntry entry) {
			entryList.add(entry);
		}

		public static CompiledProjectorConfig compile(List<ProjectorLensConfigEntry> list, BlockPos to, BlockPos master, int dim) {
			final CompiledProjectorConfig c = new CompiledProjectorConfig();
			if (list.isEmpty()) {
				CompiledProjectorLensConfigEntry e = ProjectorLensConfigEntry.createNew().compile();
				e.fieldType = FieldType.WALL;
				c.addPart(e);
			} else {
				for (ProjectorLensConfigEntry e : list)
					c.addPart(e.compile());
			}
			c.masterPos = master;
			c.compile(to);
			WorldHandler.registerNoTeleportZone(dim, new IValidationChecker() {

				@Override
				public boolean isValid() {
					return c.valid;
				}
			}, c.box.expand(2, 2, 2));
			return c;
		}

		@SuppressWarnings("unused")
		private static List<BuiltProjectorLensConfigEntry> getIntersects(List<BuiltProjectorLensConfigEntry> list, AxisAlignedBB bounds) {
			List<BuiltProjectorLensConfigEntry> intersectsWith = new ArrayList<>();
			for (int i = 0;i < list.size();i++) {
				BuiltProjectorLensConfigEntry e = list.get(i);
				if (e.bounds.intersects(bounds)) {
					intersectsWith.add(e);
				}
			}
			return intersectsWith;
		}

		public boolean contains(BlockPos pos) {
			return blockList.contains(pos);
		}
	}

	public static class FieldBlockType {
		private final int meta;
		private final BlockPos pos, masterPos;
		private final boolean onlyEffect;
		private final ProjectorLensUpgradeList effect;
		private final boolean isInValid;

		public FieldBlockType(int meta, BlockPos pos, BlockPos masterPos, ProjectorLensUpgradeList effect, boolean onlyEffect, boolean isInValid) {
			this.meta = meta;
			this.masterPos = masterPos;
			this.pos = pos;
			this.onlyEffect = onlyEffect;
			this.effect = effect;
			this.isInValid = isInValid;
		}

		public boolean placeBlock(World world) {
			return place(world, false);
		}

		private boolean place(World world, boolean isSecondTry) {
			if (this.isInValid)
				return false;
			IBlockState oldState = world.getBlockState(pos);
			if (!this.onlyEffect) {
				if (oldState == null) {
					put(world);
					return true;
				} else if (oldState.getBlock() == null) {
					put(world);
					return true;
				} else if (oldState.getBlock().isReplaceable(world, pos)) {
					put(world);
					return true;
				} else if (effect.hasSponge && !isSecondTry) {
					if (oldState != null && (oldState.getBlock() instanceof BlockStaticLiquid || oldState.getBlock() instanceof IFluidBlock)) {
						world.setBlockToAir(pos);
					}
					return this.place(world, true);
				} else if (effect.hasBlockBreakingUpgrade && !isSecondTry && oldState.getBlock() != DefenseInit.blockForce) {
					TomsModUtils.breakBlockWithDrops(world, pos);
					return this.place(world, true);
				} else
					return false;
			} else if (effect.hasSponge) {
				if (oldState != null && (oldState.getBlock() instanceof BlockLiquid || oldState.getBlock() instanceof IFluidBlock)) {
					world.setBlockToAir(pos);
				}
				return true;
			} else if (effect.hasBlockBreakingUpgrade && oldState.getBlock() != DefenseInit.blockForce) {
				TomsModUtils.breakBlockWithDrops(world, pos);
				return true;
			} else
				return false;
		}

		private void put(World world) {
			@SuppressWarnings("deprecation")
			IBlockState state = DefenseInit.blockForce.getStateFromMeta(meta);
			world.setBlockState(pos, state, 3);
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileEntityForceField) {
				TileEntityForceField te = (TileEntityForceField) tile;
				te.ownerPos = masterPos;
				te.update(world);
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other == this)
				return true;
			if (other instanceof BlockPos)
				return ((BlockPos) other).equals(pos);
			if (other instanceof FieldBlockTypeComparator)
				return ((FieldBlockTypeComparator) other).equals(pos);
			if (!(other instanceof FieldBlockType))
				return false;
			FieldBlockType o = (FieldBlockType) other;
			return o.pos.equals(pos) && o.masterPos.equals(masterPos) && o.meta == meta;
		}

		public int getEnergy() {
			return meta == 2 ? 2 : 1;
		}

		public void destroy(World world) {
			if (this.isInValid)
				return;
			IBlockState oldState = world.getBlockState(pos);
			if (oldState != null && oldState.getBlock() == DefenseInit.blockForce && oldState.getBlock().getMetaFromState(oldState) == meta) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof TileEntityForceField) {
					TileEntityForceField te = (TileEntityForceField) tile;
					if (masterPos.equals(te.ownerPos))
						te.breakBlock();
				}
			}
		}

		public static class FieldBlockTypeComparator {
			private BlockPos pos;

			public FieldBlockTypeComparator(BlockPos pos) {
				this.pos = pos;
			}

			@Override
			public boolean equals(Object other) {
				if (other == this)
					return true;
				if (other instanceof BlockPos)
					return ((BlockPos) other).equals(pos);
				if (!(other instanceof FieldBlockType))
					return false;
				FieldBlockType o = (FieldBlockType) other;
				return o.equals(other);
			}
		}
	}
}
