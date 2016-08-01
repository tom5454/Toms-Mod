package com.tom.core.research.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.tom.api.research.IResearch;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;

public class ResearchHandler {
	private static List<IResearch> researchList = new ArrayList<IResearch>();
	private static Map<String, ResearchHandler> researchHandlerList = new HashMap<String,ResearchHandler>();
	//private static File researchData = null;
	//private static final String CATRGORY = "research", NAME = "data", ERRORED = "errored";
	private List<IResearch> researchDone = new ArrayList<IResearch>();
	private List<IScanningInformation> scanInfo = new ArrayList<IScanningInformation>();
	public final String name;
	//private static boolean allowSave = false;
	public static int registerResearch(IResearch research){
		researchList.add(research);
		return researchList.size()-1;
	}
	public static IResearch getResearchByID(int id){
		if(id == -1)return null;
		if(id < researchList.size())return researchList.get(id);
		else return null;
	}
	public static int getId(IResearch research){
		if(research == null)return -1;
		if(researchList.contains(research)){
			return researchList.indexOf(research);
		}else
			return -1;
	}
	public List<ResearchInformation> getAvailableResearches(){
		List<ResearchInformation> ret = new ArrayList<ResearchInformation>();
		for(IResearch r : researchList){
			if(r.isValid() && !this.researchDone.contains(r)){
				List<IResearch> parents = r.getParents();
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
		List<Integer> iList = new ArrayList<Integer>();
		for(IResearch res : researchDone){
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
		tag.setIntArray("ids", out);
		NBTTagList list = new NBTTagList();
		for(IScanningInformation i : this.scanInfo){
			NBTTagCompound t = new NBTTagCompound();
			i.writeToNBT(t);
			list.appendTag(t);
		}
		tag.setTag("scan", list);
	}
	public void readFromNBT(NBTTagCompound tag){
		int[] in = tag.getIntArray("ids");
		NBTTagList list = tag.getTagList("scan", 10);
		for(int i : in){
			IResearch res = getResearchByID(i);
			if(res != null){
				this.researchDone.add(res);
			}
		}
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
	public void markResearchComplete(IResearch research){
		if(research == null)return;
		if(!this.researchDone.contains(research))
			this.researchDone.add(research);
	}
	public static class ResearchInformation{
		private final IResearch research;
		public boolean available = true;
		public List<IScanningInformation> missing = new ArrayList<IScanningInformation>();
		public ResearchInformation(IResearch research) {
			this.research = research;
		}
		public ResearchInformation(IResearch research, boolean available) {
			this.research = research;
			this.available = available;
		}
		public IResearch getResearch() {
			return research;
		}
	}
	public List<IResearch> getResearchesCompleted(){
		return this.researchDone;
	}
	public static List<String> getResearchNames(List<IResearch> in){
		List<String> ret = new ArrayList<String>();
		for(IResearch r : in){
			ret.add(r.getName());
		}
		return ret;
	}
}
