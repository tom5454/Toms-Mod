package com.tom.core.block;

import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.tom.apis.EmptyEntry;
import com.tom.apis.TMLogger;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;

public class BlockOre extends Block {
	private final int dim;
	private final int a;
	private final int y;
	public final boolean dropsItself;
	public final ItemStack drop;
	public BlockOre(int y, int dim, int a, TMResource r) {
		super(Material.ROCK, MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = true;
		drop = null;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		//r.setOre(this);
		this.a = a;
		this.y = y;
		this.dim = dim;
	}
	public BlockOre(int y, int dim, int a, ItemStack drop, TMResource r) {
		super(Material.ROCK, MapColor.GRAY);
		this.setCreativeTab(CoreInit.tabTomsModMaterials);
		dropsItself = false;
		this.drop = drop;
		this.setHardness(8.0F);
		this.setResistance(30.0F);
		this.a = a;
		this.y = y;
		this.dim = dim;
		//r.setOre(this);
	}
	public BlockOre register(){
		if(Config.enableOreGen(getUnlocalizedName().substring(5))){
			Entry<Integer, Integer> dimE = new EmptyEntry<Integer, Integer>(dim);
			dimE.setValue(a);
			Entry<Integer, Entry<Integer, Integer>> genV = new EmptyEntry<Integer, Entry<Integer, Integer>>(y,dimE);
			CoreInit.oreList.put(this.getDefaultState(), genV);
		}else{
			String msg = "[Ore Gen] Ore '" + getUnlocalizedName().substring(5) + "' is disabled.";
			Config.warnMessages.add(msg);
			TMLogger.warn(msg);
		}
		return this;
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
		return this.dropsItself ? 0 : this.drop.getMetadata();
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(this);
	}
	@Override
	public Block setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		register();
		return this;
	}
}
