package com.tom.core.research;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

import com.google.common.collect.BiMap;

import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.api.research.Research;

public class ResearchHandler {
	//private static List<Research> researchList = new ArrayList<Research>();
	private static Map<String, ResearchHandler> researchHandlerList = new HashMap<String,ResearchHandler>();
	public static Logger log = LogManager.getLogger("Tom's Mod Research Handler");
	//private static File researchData = null;
	//private static final String CATRGORY = "research", NAME = "data", ERRORED = "errored";
	private List<Research> researchDone = new ArrayList<Research>();
	private List<IScanningInformation> scanInfo = new ArrayList<IScanningInformation>();
	public final String name;
	private static FMLControlledNamespacedRegistry<Research> iResearchRegistry;
	private static final ResourceLocation loc = new ResourceLocation("tomsmod:research");
	private static final int MIN_ID = 0;
	private static final int MAX_ID = Integer.MAX_VALUE - 1;
	public static void init(){
		log.info("Loading Research Handler...");
		RegistryBuilder<Research> builder = new RegistryBuilder<Research>();
		builder.setName(loc);
		builder.setType(Research.class);
		builder.setIDRange(MIN_ID, MAX_ID);
		iResearchRegistry = (FMLControlledNamespacedRegistry<Research>) builder.create();
		//iResearchRegistry = PersistentRegistryManager.createRegistry(loc, Research.class, new ResourceLocation("invalid"), MIN_ID, MAX_ID, true, ResearchCallbacks.INSTANCE, ResearchCallbacks.INSTANCE, ResearchCallbacks.INSTANCE);
	}
	//private static boolean allowSave = false;
	public static Research getResearchByID(int id){
		if(id == -1)return null;
		return iResearchRegistry.getObjectById(id);
	}
	public static int getId(Research research){
		if(research == null)return -1;
		return iResearchRegistry.getId(research);
	}
	public List<ResearchInformation> getAvailableResearches(){
		List<ResearchInformation> ret = new ArrayList<ResearchInformation>();
		for(Entry<ResourceLocation, Research> rE : iResearchRegistry.getEntries()){
			Research r = rE.getValue();
			if(r.isValid() && !this.researchDone.contains(r)){
				List<Research> parents = r.getParents();
				if(parents == null || this.researchDone.containsAll(parents)){
					List<IScanningInformation> requiredS = r.getRequiredScans();
					if(requiredS == null || this.scanInfo.containsAll(requiredS)){
						ret.add(new ResearchInformation(r));
					}else{
						ResearchInformation rInfo = new ResearchInformation(r,false);
						for(int i = 0;i<requiredS.size();i++){
							IScanningInformation info = requiredS.get(i);
							if(!this.scanInfo.contains(info)){
								rInfo.missing.add(info);
							}
						}
						ret.add(rInfo);
					}
				}
			}
		}
		return ret;
	}
	public static void load(String name, NBTTagCompound tag){
		//CoreInit.log.info("Loading Research Handler for...");
		/*researchHandlerList.clear();
		researchData = new File(TomsModUtils.getSavedFile(server),"research.tmcfg");
		Configuration cfg = new Configuration(researchData);*/
		//String in = cfg.get(CATRGORY, NAME, "").getString();
		//List<String> errored = new ArrayList<String>();
		//String[] erroredIn = cfg.get(CATRGORY, ERRORED, new String[]{}).getStringList();
		/*for(String e : erroredIn){
			errored.add(e);
			CoreInit.log.warn("Found an errored tag: "+e);
		}*/
		//try{
		//NBTTagCompound nbt = JsonToNBT.getTagFromJson(in);
		//if(!nbt.hasKey("name")) throw new NoSuchFieldException("missing name tag");
		researchHandlerList.put(name, fromNBT(tag.getCompoundTag("handler"), name));
		/*}catch(Exception e){
			Exception e2 = new ResearchHandlerLoadingException(e);
			e2.printStackTrace();
			CoreInit.log.error("Invalid NBTTag: "+in);
			errored.add(in);
		}
		String[] erroredOut = errored.toArray(new String[]{});
		Property p = cfg.get(CATRGORY, ERRORED, new String[]{});
		p.comment = "Invalid tags saved here.";
		p.set(erroredOut);*/
		//allowSave = true;
	}
	public static void save(String name, NBTTagCompound tag){
		//if(!allowSave)return;
		//long timeStart = System.currentTimeMillis();
		//CoreInit.log.info("Saving Research Handler Data...");
		//cfg.get(CATRGORY, NAME, new String[]{});
		//List<String> sList = new ArrayList<String>();
		//for(Entry<String, ResearchHandler> eResH : researchHandlerList.entrySet()){
		//String name = eResH.getKey();
		ResearchHandler h = getHandlerFromName(name);
		//NBTTagCompound mainTag = new NBTTagCompound();
		//mainTag.setString("name", name);
		NBTTagCompound hTag = new NBTTagCompound();
		h.writeToNBT(hTag);
		tag.setTag("handler",hTag);
		//String data = mainTag.toString();
		//sList.add(data);
		//}
		//String[] out = sList.toArray(new String[]{});
		//cfg.get(CATRGORY, NAME, "").set(data);
		//cfg.save();
		//CoreInit.log.info("Done in " + (System.currentTimeMillis() - timeStart) + " ms");
	}
	/*public static void cleanup(){
		CoreInit.log.info("Cleaning up Research Handler...");
		save();
		researchHandlerList.clear();
		allowSave = false;
	}*/
	public void writeToNBT(NBTTagCompound tag){
		/*List<Integer> iList = new ArrayList<Integer>();
		for(Research res : researchDone){
			int id = getId(res);
			if(id != -1){
				iList.add(id);
			}
		}
		Integer[] outI = iList.toArray(new Integer[]{});
		int[] out = new int[outI.length];
		for(int i = 0;i<outI.length;i++){
			out[i] = outI[i];
		}
		tag.setIntArray("ids", out);*/
		NBTTagList ids = new NBTTagList();//8
		for(Research res : researchDone){
			try{
				ids.appendTag(new NBTTagString(res.delegate.name().toString()));
			}catch(Exception e){}
		}
		tag.setTag("ids", ids);
		NBTTagList list = new NBTTagList();
		for(IScanningInformation i : this.scanInfo){
			NBTTagCompound t = new NBTTagCompound();
			i.writeToNBT(t);
			list.appendTag(t);
		}
		tag.setTag("scan", list);
	}
	public void readFromNBT(NBTTagCompound tag){
		if(tag.hasKey("ids", 11)){
			int[] in = tag.getIntArray("ids");
			for(int i : in){
				Research res = getResearchByID(i);
				if(res != null){
					this.researchDone.add(res);
				}
			}
		}else{
			NBTTagList list = tag.getTagList("ids", 8);
			for(int i = 0;i<list.tagCount();i++){
				Research res = iResearchRegistry.getObject(new ResourceLocation(list.getStringTagAt(i)));
				if(res != null){
					this.researchDone.add(res);
				}
			}
		}
		NBTTagList list = tag.getTagList("scan", 10);
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			IScanningInformation info = ScanningInformation.fromNBT(t);
			if(info != null){
				this.scanInfo.add(info);
			}
		}
	}
	public static ResearchHandler fromNBT(NBTTagCompound tag, String name){
		ResearchHandler ret = new ResearchHandler(name);
		ret.readFromNBT(tag);
		return ret;
	}
	public static ResearchHandler getHandlerFromName(String name){
		if(researchHandlerList.containsKey(name)){
			return researchHandlerList.get(name);
		}else{
			ResearchHandler h = new ResearchHandler(name);
			researchHandlerList.put(name, h);
			return h;
		}
	}
	public static class ResearchHandlerLoadingException extends Exception{
		private static final long serialVersionUID = 8689253275484614664L;
		public ResearchHandlerLoadingException(Throwable causedBy) {
			super(causedBy);
		}
	}
	private ResearchHandler(String name) {
		this.name = name;
	}
	public int addScanningInformation(List<IScanningInformation> infoList, int max){
		int count = 0;
		for(IScanningInformation info : infoList){
			if(count == max)break;
			if(!this.scanInfo.contains(info)){
				this.scanInfo.add(info);
				count++;
			}
		}
		return count;
	}
	public void markResearchComplete(Research research){
		if(research == null)return;
		if(!this.researchDone.contains(research))
			this.researchDone.add(research);
	}
	public static class ResearchInformation{
		private final Research research;
		public boolean available = true;
		public List<IScanningInformation> missing = new ArrayList<IScanningInformation>();
		public ResearchInformation(Research research) {
			this.research = research;
		}
		public ResearchInformation(Research research, boolean available) {
			this.research = research;
			this.available = available;
		}
		public Research getResearch() {
			return research;
		}
	}
	public List<Research> getResearchesCompleted(){
		return this.researchDone;
	}
	public static List<String> getResearchNames(List<Research> in){
		List<String> ret = new ArrayList<String>();
		for(Research r : in){
			ret.add(r.getUnlocalizedName());
		}
		return ret;
	}
	public static class ResearchCallbacks implements IForgeRegistry.AddCallback<Research>,IForgeRegistry.ClearCallback<Research>,IForgeRegistry.CreateCallback<Research>
	{
		public static final ResearchCallbacks INSTANCE = new ResearchCallbacks();

		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset,
				BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries) {

		}

		@Override
		public void onClear(IForgeRegistry<Research> is, Map<ResourceLocation, ?> slaveset) {

		}

		@Override
		public void onAdd(Research obj, int id, Map<ResourceLocation, ?> slaveset) {

		}

	}
}
