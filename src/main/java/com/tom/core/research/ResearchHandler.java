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

import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import com.tom.api.research.IScanningInformation;
import com.tom.api.research.Research;
import com.tom.config.Config;

public class ResearchHandler {
	private static Map<String, ResearchHandler> researchHandlerList = new HashMap<>();
	public static Logger log = LogManager.getLogger("Tom's Mod Research Handler");
	private List<Research> researchDone = new ArrayList<>();
	private List<IScanningInformation> scanInfo = new ArrayList<>();
	public final String name;
	public static final ForgeRegistry<Research> REGISTRY;
	private static final ResourceLocation loc = new ResourceLocation("tomsmod:research");
	private static final int MIN_ID = 0;
	private static final int MAX_ID = Integer.MAX_VALUE - 1;
	static {
		RegistryBuilder<Research> builder = new RegistryBuilder<>();
		builder.setName(loc);
		builder.setType(Research.class);
		builder.setIDRange(MIN_ID, MAX_ID);
		REGISTRY = (ForgeRegistry<Research>) builder.create();
	}

	public static void init() {
		log.info("Loading Research Handler...");
	}

	public static Research getResearchByID(int id) {
		if (id == -1)
			return null;
		return REGISTRY.getValue(id);
	}

	public static int getId(Research research) {
		if (research == null)
			return -1;
		return REGISTRY.getID(research);
	}

	public List<ResearchInformation> getAvailableResearches() {
		List<ResearchInformation> ret = new ArrayList<>();
		for (Entry<ResourceLocation, Research> rE : REGISTRY.getEntries()) {
			Research r = rE.getValue();
			if (r.isValid() && !this.researchDone.contains(r)) {
				List<Research> parents = r.getParents();
				if (parents == null || this.researchDone.containsAll(parents)) {
					List<IScanningInformation> requiredS = r.getRequiredScans();
					if (requiredS == null || this.scanInfo.containsAll(requiredS) || Config.disableScanning) {
						ret.add(new ResearchInformation(r));
					} else {
						ResearchInformation rInfo = new ResearchInformation(r, false);
						for (int i = 0;i < requiredS.size();i++) {
							IScanningInformation info = requiredS.get(i);
							if (!this.scanInfo.contains(info)) {
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

	public static void load(String name, NBTTagCompound tag) {
		// CoreInit.log.info("Loading Research Handler for...");
		/*researchHandlerList.clear();
		researchData = new File(TomsModUtils.getSavedFile(server),"research.tmcfg");
		Configuration cfg = new Configuration(researchData);*/
		// String in = cfg.get(CATRGORY, NAME, "").getString();
		// List<String> errored = new ArrayList<String>();
		// String[] erroredIn = cfg.get(CATRGORY, ERRORED, new
		// String[]{}).getStringList();
		/*for(String e : erroredIn){
			errored.add(e);
			CoreInit.log.warn("Found an errored tag: "+e);
		}*/
		// try{
		// NBTTagCompound nbt = JsonToNBT.getTagFromJson(in);
		// if(!nbt.hasKey("name")) throw new NoSuchFieldException("missing name
		// tag");
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
		// allowSave = true;
	}

	public static void save(String name, NBTTagCompound tag) {
		// if(!allowSave)return;
		// long timeStart = System.currentTimeMillis();
		// CoreInit.log.info("Saving Research Handler Data...");
		// cfg.get(CATRGORY, NAME, new String[]{});
		// List<String> sList = new ArrayList<String>();
		// for(Entry<String, ResearchHandler> eResH :
		// researchHandlerList.entrySet()){
		// String name = eResH.getKey();
		ResearchHandler h = getHandlerFromName(name);
		// NBTTagCompound mainTag = new NBTTagCompound();
		// mainTag.setString("name", name);
		NBTTagCompound hTag = new NBTTagCompound();
		h.writeToNBT(hTag);
		tag.setTag("handler", hTag);
		// String data = mainTag.toString();
		// sList.add(data);
		// }
		// String[] out = sList.toArray(new String[]{});
		// cfg.get(CATRGORY, NAME, "").set(data);
		// cfg.save();
		// CoreInit.log.info("Done in " + (System.currentTimeMillis() -
		// timeStart) + " ms");
	}

	/*public static void cleanup(){
		CoreInit.log.info("Cleaning up Research Handler...");
		save();
		researchHandlerList.clear();
		allowSave = false;
	}*/
	public void writeToNBT(NBTTagCompound tag) {
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
		NBTTagList ids = new NBTTagList();// 8
		for (Research res : researchDone) {
			try {
				ids.appendTag(new NBTTagString(res.delegate.name().toString()));
			} catch (Exception e) {
			}
		}
		tag.setTag("ids", ids);
		NBTTagList list = new NBTTagList();
		for (IScanningInformation i : this.scanInfo) {
			if (i != null) {
				NBTTagCompound t = new NBTTagCompound();
				IScanningInformation.Handler.writeToNBT(t, i);
				list.appendTag(t);
			}
		}
		tag.setTag("scan", list);
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("ids", 11)) {
			int[] in = tag.getIntArray("ids");
			for (int i : in) {
				Research res = getResearchByID(i);
				if (res != null) {
					this.researchDone.add(res);
				}
			}
		} else {
			NBTTagList list = tag.getTagList("ids", 8);
			for (int i = 0;i < list.tagCount();i++) {
				Research res = REGISTRY.getValue(new ResourceLocation(list.getStringTagAt(i)));
				if (res != null) {
					this.researchDone.add(res);
				}
			}
		}
		NBTTagList list = tag.getTagList("scan", 10);
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound t = list.getCompoundTagAt(i);
			IScanningInformation info = IScanningInformation.Handler.fromNBT(t);
			if (info != null) {
				this.scanInfo.add(info);
			}
		}
	}

	public static ResearchHandler fromNBT(NBTTagCompound tag, String name) {
		ResearchHandler ret = new ResearchHandler(name);
		ret.readFromNBT(tag);
		return ret;
	}

	public static ResearchHandler getHandlerFromName(String name) {
		if (researchHandlerList.containsKey(name)) {
			return researchHandlerList.get(name);
		} else {
			ResearchHandler h = new ResearchHandler(name);
			researchHandlerList.put(name, h);
			return h;
		}
	}

	public static class ResearchHandlerLoadingException extends Exception {
		private static final long serialVersionUID = 8689253275484614664L;

		public ResearchHandlerLoadingException(Throwable causedBy) {
			super(causedBy);
		}
	}

	private ResearchHandler(String name) {
		this.name = name;
	}

	public int addScanningInformation(List<IScanningInformation> infoList, int max) {
		int count = 0;
		for (IScanningInformation info : infoList) {
			if (count == max)
				break;
			if (!this.scanInfo.contains(info)) {
				this.scanInfo.add(info);
				count++;
			}
		}
		return count;
	}

	public void markResearchComplete(Research research) {
		if (research == null)
			return;
		if (!this.researchDone.contains(research))
			this.researchDone.add(research);
	}

	public static class ResearchInformation {
		private final Research research;
		public boolean available = true;
		public List<IScanningInformation> missing = new ArrayList<>();

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

	public List<Research> getResearchesCompleted() {
		return this.researchDone;
	}

	public static List<String> getResearchNames(List<Research> in) {
		List<String> ret = new ArrayList<>();
		for (Research r : in) {
			ret.add(r.getUnlocalizedName());
		}
		return ret;
	}

	public boolean isCompleted(Research research) {
		if (research == null)
			return false;
		return researchDone.contains(research);
	}

	public static boolean isCompleted(ResearchHandler handler, Research research) {
		if (handler == null)
			return false;
		if (research == null)
			return false;
		return handler.isCompleted(research);
	}

	public static Research getResearchByName(String string) {
		return REGISTRY.getValue(new ResourceLocation(string));
	}

	public static List<Research> getAllResearches() {
		return new ArrayList<>(REGISTRY.getValuesCollection());
	}

	public void removeResearch(Research r) {
		researchDone.remove(r);
	}

	public void clearScans() {
		scanInfo.clear();
	}
}
