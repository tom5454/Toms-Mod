package com.tom.core.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Predicate;

import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IIconRegisterRequired;
import com.tom.api.block.IRegisterRequired;
import com.tom.apis.TMLogger;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.worldgen.WorldGen.OreGenEntry;

public class BlockOre extends Block implements ICustomItemBlock, IRegisterRequired, IIconRegisterRequired{
	public final boolean dropsItself;
	public final ItemStack drop;
	protected Map<Predicate<World>, OreGenEntry> genEntryMap;
	protected List<String> postFixes;
	public PropertyInteger TYPE;
	private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();
	private int maxStates;
	public BlockOre(int y, Predicate<World> dim, int a, TMResource r, int maxStates) {
		super(setValue(maxStates), MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = true;
		drop = null;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		//r.setOre(this);
		genEntryMap = new HashMap<Predicate<World>, OreGenEntry>();
		genEntryMap.put(dim, new OreGenEntry(BlockMatcher.forBlock(Blocks.STONE), new Callable<IBlockState>(){

			@Override
			public IBlockState call() throws Exception {
				return getDefaultState();
			}

		}, y, a));
		postFixes = new ArrayList<String>();
		postFixes.add("");
	}
	public BlockOre(int y, Predicate<World> dim, int a, ItemStack drop, TMResource r, int maxStates) {
		super(setValue(maxStates), MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = false;
		this.drop = drop;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		//r.setOre(this);
		genEntryMap = new HashMap<Predicate<World>, OreGenEntry>();
		genEntryMap.put(dim, new OreGenEntry(BlockMatcher.forBlock(Blocks.STONE), new Callable<IBlockState>(){

			@Override
			public IBlockState call() throws Exception {
				return getDefaultState();
			}

		}, y, a));
		postFixes = new ArrayList<String>();
		postFixes.add("");
	}
	public BlockOre(int y, Predicate<World> dim, int a, int maxStates) {
		super(setValue(maxStates), MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = true;
		drop = null;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		//r.setOre(this);
		genEntryMap = new HashMap<Predicate<World>, OreGenEntry>();
		genEntryMap.put(dim, new OreGenEntry(BlockMatcher.forBlock(Blocks.STONE), new Callable<IBlockState>(){

			@Override
			public IBlockState call() throws Exception {
				return getDefaultState();
			}

		}, y, a));
		postFixes = new ArrayList<String>();
		postFixes.add("");
	}
	public BlockOre(int y, Predicate<World> dim, int a, Predicate<IBlockState> base, int maxStates) {
		super(setValue(maxStates), MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = true;
		drop = null;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		//r.setOre(this);
		genEntryMap = new HashMap<Predicate<World>, OreGenEntry>();
		genEntryMap.put(dim, new OreGenEntry(base, new Callable<IBlockState>(){

			@Override
			public IBlockState call() throws Exception {
				return getDefaultState();
			}

		}, y, a));
		postFixes = new ArrayList<String>();
		postFixes.add("");
	}
	public BlockOre(int y, Predicate<World> dim, int a, TMResource r){
		this(y, dim, a, r, 1);
	}
	public BlockOre(int y, Predicate<World> dim, int a, ItemStack drop, TMResource r){
		this(y, dim, a, drop, r, 1);
	}
	public BlockOre(int y, Predicate<World> dim, int a){
		this(y, dim, a, 1);
	}
	public BlockOre(int y, Predicate<World> dim, int a, Predicate<IBlockState> base){
		this(y, dim, a, base, 1);
	}
	@Override
	public void register(){
		/*Entry<Predicate<World>, Integer> dimE = new EmptyEntry<Predicate<World>, Integer>(dim);
			dimE.setValue(a);
			Entry<Integer, Entry<Predicate<World>, Integer>> genV = new EmptyEntry<Integer, Entry<Predicate<World>, Integer>>(y, dimE);*/
		//CoreInit.oreList.putAll(genEntryMap);
		for(Entry<Predicate<World>, OreGenEntry> e : genEntryMap.entrySet()){
			try {
				e.getValue().ore = e.getValue().oreInit.call();
			} catch (Exception e1) {
				throw new RuntimeException("IMPOSSIBLE ERROR OCCURRED");
			}
			ItemStack s = new ItemStack(this, 1, e.getValue().ore.getBlock().getMetaFromState(e.getValue().ore));
			String name = (new OreItemBlock(this)).getUnlocalizedName(s).substring(5);
			if(Config.enableOreGen(name)){
				List<OreGenEntry> list = CoreInit.oreList.get(e.getKey());
				if(list == null){
					list = new ArrayList<OreGenEntry>();
					CoreInit.oreList.put(e.getKey(), list);
				}
				list.add(e.getValue());
			}else{
				String msg = "[Ore Gen] Ore \'" + name + "\' is disabled.";
				Config.warnMessages.add(msg);
				TMLogger.warn(msg);
			}
		}
	}
	@Override
	public int quantityDropped(Random par1Random){
		return this.dropsItself ? 1 : this.drop.stackSize;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return this.dropsItself ? Item.getItemFromBlock(this) : this.drop.getItem();
	}
	/*@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block){
		if(!isDropItself){
			if(drop != null){
				EntityItem item = new EntityItem(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, drop);
				world.spawnEntityInWorld(item);
			}
			this.dropXpOnBlockBreak(world, pos, 10);
		}
		super.breakBlock(world, pos, block);
	}*/

	/**
	 * Get the quantity dropped based on the given fortune level
	 */
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(this.getBlockState().getValidStates().iterator().next(), random, fortune))
		{
			int i = random.nextInt(fortune + 2) - 1;

			if (i < 0)
			{
				i = 0;
			}

			return this.quantityDropped(random) * (i + 1);
		}
		else
		{
			return this.quantityDropped(random);
		}
	}

	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 */
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
	}
	/*@Override
    public int getExpDrop(net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        IBlockState state = world.getBlockState(pos);
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this))
        {
            /*int i = 0;

            if (this == Blocks.coal_ore)
            {
                i = MathHelper.getRandomIntegerInRange(rand, 0, 2);
            }
            else if (this == Blocks.diamond_ore)
            {
                i = MathHelper.getRandomIntegerInRange(rand, 3, 7);
            }
            else if (this == Blocks.emerald_ore)
            {
                i = MathHelper.getRandomIntegerInRange(rand, 3, 7);
            }
            else if (this == Blocks.lapis_ore)
            {
                i = MathHelper.getRandomIntegerInRange(rand, 2, 5);
            }
            else if (this == Blocks.quartz_ore)
            {
                i = MathHelper.getRandomIntegerInRange(rand, 2, 5);
            }*/

	/*return MathHelper.getRandomIntegerInRange(rand, 2, 6);
        }
        return 0;
    }*/

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.dropsItself ? getMetaFromState(state) : this.drop.getMetadata();
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(this, 1, getMetaFromState(state));
	}
	@Override
	public BlockOre setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		return this;
	}
	public BlockOre addExtraState(Predicate<World> dim, int y, int a, String postFix){
		addExtraState(dim, a, y, BlockMatcher.forBlock(Blocks.STONE), postFix);
		return this;
	}
	public BlockOre addExtraState(Predicate<World> dim, int y, int a, Predicate<IBlockState> base, String postFix){
		final int id = postFixes.size();
		genEntryMap.put(dim, new OreGenEntry(base, new Callable<IBlockState>(){

			@Override
			public IBlockState call() throws Exception {
				return getStateFromMeta(id);
			}

		}, y, a));
		postFixes.add("."+postFix);
		return this;
	}
	public static class OreItemBlock extends ItemBlock{
		public final BlockOre block;
		public OreItemBlock(BlockOre block) {
			super(block);
			this.block = block;
			setHasSubtypes(true);
		}
		@Override
		public String getUnlocalizedName(ItemStack stack) {
			int meta = stack.getMetadata();
			return super.getUnlocalizedName(stack) + (block.postFixes.size() > meta ? block.postFixes.get(meta) : "");
		}
		@Override
		public int getMetadata(int damage) {
			return damage % block.maxStates;
		}
	}
	@Override
	public ItemBlock createItemBlock() {
		return new OreItemBlock(this);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		for(int i = 0;i<postFixes.size();i++){
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return true;
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return maxStates == 1 ? 0 : state.getValue(TYPE);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return maxStates == 1 ? getDefaultState() : getDefaultState().withProperty(TYPE, meta % maxStates);
	}
	@Override
	protected BlockStateContainer createBlockState() {
		if(TYPE == null){
			Integer i = threadLocal.get();
			if(i == null)i = 1;
			TYPE = PropertyInteger.create("type", 0, i == 1 ? i : i - 1);
			maxStates = i;
		}
		return maxStates > 1 ? new BlockStateContainer(this, TYPE) : new BlockStateContainer(this, new IProperty[0]);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons() {
		Item item = Item.getItemFromBlock(this);
		CoreInit.registerRender(item, 0);
		if(postFixes.size() > 1){
			String type = CoreInit.getNameForItem(item).replace("|", "");
			for(int i = 1;i<postFixes.size();i++){
				CoreInit.registerRender(item, i, type + postFixes.get(i));
			}
		}
	}
	public static Material setValue(int max){
		threadLocal.set(Math.min(Math.max(max, 1), 15));
		return Material.ROCK;
	}
}
