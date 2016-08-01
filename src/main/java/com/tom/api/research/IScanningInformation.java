package com.tom.api.research;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IScanningInformation {
	Block getBlock();
	//List<IPropertyValue<? extends Comparable<?>>> getStateMap();
	boolean equals(IScanningInformation other);
	int getMeta();
	void writeToNBT(NBTTagCompound tag);

	public static class ScanningInformation implements IScanningInformation{
		private Block block;
		private int meta;
		//private final List<IPropertyValue<? extends Comparable<?>>> propertyList;
		@Override
		public Block getBlock() {
			return block;
		}
		public ScanningInformation(Block block, int meta) {
			this.block = block;
			this.meta = meta;
			//this.propertyList = new ArrayList<IPropertyValue<? extends Comparable<?>>>();
		}
		/*@Override
		public List<IPropertyValue<? extends Comparable<?>>> getStateMap() {
			return propertyList;
		}*/
		@Override
		public boolean equals(IScanningInformation other) {
			if(other == null)return false;
			/*if(other.getStateMap() == this.getStateMap())return true;
				if(this.getStateMap() != null && other.getStateMap() != null && other.getStateMap().size() == this.getStateMap().size()){
					boolean equals = true;
					boolean firstRun = true;
					for(IPropertyValue<? extends Comparable<?>> v : other.getStateMap()){
						firstRun = false;
						equals = equals && this.getStateMap().contains(v);
					}
					if(equals && (!firstRun)) return true;
					Map<IProperty,Comparable> map = new HashMap<IProperty, Comparable>();
					for(IPropertyValue<? extends Comparable<?>> v : other.getStateMap()){
						map.put(v.getProperty(), v.getValue());
					}
					firstRun = true;
					equals = true;
					for(IPropertyValue<? extends Comparable<?>> v : this.getStateMap()){
						firstRun = false;
						if(map.containsKey(v.getProperty())){
							equals = equals && map.get(v.getProperty()) == v.getValue();
						}else{
							equals = false;
						}
					}
					if(equals && (!firstRun)) return true;
				}*/
			return other.getBlock() == this.getBlock() && (other.getMeta() == this.getMeta() || other.getMeta() == -1 || this.getMeta() == -1);
		}
		@Override
		public int getMeta() {
			return meta;
		}
		@Override
		public void writeToNBT(NBTTagCompound tag) {
			ResourceLocation b1 = block.delegate.name();
			tag.setString("blockName", b1.getResourcePath());
			tag.setString("modid", b1.getResourceDomain());
			tag.setInteger("meta", meta);
		}
		//@Override
		public static ScanningInformation fromNBT(NBTTagCompound tag) {
			int meta = tag.getInteger("meta");
			Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("modid"), tag.getString("blockName")));
			if(block != null)return new ScanningInformation(block,meta);
			return null;
		}
		@Override
		public boolean equals(Object other) {
			if(other instanceof IScanningInformation){
				return this.equals((IScanningInformation)other);
			}
			return false;
		}

	}
	/*public static interface IPropertyValue<T extends Comparable<T>>{
		IProperty<T> getProperty();
		T getValue();
		public static class PropertyValue<T extends Comparable<T>> implements IPropertyValue<T>{
			private final IProperty<T> property;
			private final T value;

			public PropertyValue(IProperty<T> property, T value) {
				this.property = property;
				this.value = value;
			}

			@Override
			public IProperty<T> getProperty() {
				return property;
			}

			@Override
			public T getValue() {
				return value;
			}

		}
	}*/
}
