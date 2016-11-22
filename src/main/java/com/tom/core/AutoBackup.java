package com.tom.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ZipperUtil;

import com.google.common.base.Stopwatch;

import com.tom.config.Config;
import com.tom.lib.Configs;

public class AutoBackup extends Thread{
	public static final Logger log = LogManager.getLogger(Configs.ModName + "] [Backup System");
	private File in, out, logFile, cfg;
	private int year, tModCount, aModCount;
	private boolean runServerCommands = true;
	private static volatile int ticksBeforeStart = 250;
	private static volatile int nextTime = Config.backupSchedule;
	private static volatile Stopwatch stopwatch = Stopwatch.createUnstarted();
	private static volatile AutoBackup running;
	private static volatile AutoBackupCache cached;
	public static void createBackup(boolean bg, boolean executeServerCmd, String extra){
		if(isRunning())return;
		try{
			log.info("Starting Backup, Server may lag a bit.");
			AutoBackup b = createBackupIns(createCache(), extra);
			if(executeServerCmd){
				FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), "/save-off");
				FMLCommonHandler.instance().getMinecraftServerInstance().saveAllWorlds(false);
				FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), createTellraw("\"translate\":\"tomsMod.backupStarted\""));
				FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new TextComponentTranslation("tomsMod.backupStarted"));
			}
			b.runServerCommands = executeServerCmd;
			if(bg){
				running = b;
				b.start();
			}else{
				b.run_p();
			}
		}catch(Exception e){
			log.error("Backup Failed", e);
		}
	}
	protected static AutoBackup createBackupIns(AutoBackupCache c, String extra){
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		File backupFolderC = new File(c.backupFolder, c.folder + File.separator + year);
		File backupFolderC_MD = new File(backupFolderC, month + File.separator + day);
		final AutoBackup b = new AutoBackup();
		b.logFile = new File(backupFolderC, "backupLog.log");
		b.year = year;
		b.setName("Tom's Mod World Backup Thread");
		b.setDaemon(true);
		String out;
		if(extra == null){
			out = String.format("%s-%2$tY%2$tm%2$td-%2$tH%2$tM%2$tS.zip", c.folder, System.currentTimeMillis());
		}else{
			out = String.format("%s-%2$tY%2$tm%2$td-%2$tH%2$tM%2$tS", c.folder, System.currentTimeMillis()) + "-" + extra + ".zip";
		}
		b.out = new File(backupFolderC_MD, out);
		b.in = new File(c.saves, c.folder);
		b.cfg = c.cfg;
		b.tModCount = Loader.instance().getModList().size();
		b.aModCount = Loader.instance().getActiveModList().size();
		return b;
	}
	private static AutoBackupCache createCache(){
		if(cached != null){
			AutoBackupCache c = cached;
			cached = null;
			return c;
		}
		AutoBackupCache c = new AutoBackupCache();
		c.backupFolder = FMLCommonHandler.instance().getMinecraftServerInstance().getFile("tm_backup");
		c.folder = FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();
		c.cfg = FMLCommonHandler.instance().getMinecraftServerInstance().getFile("config");
		c.saves = FMLCommonHandler.instance().getSavesDirectory();
		return c;
	}
	protected static void createAndStoreCache(){
		cached = createCache();
	}

	@Override
	public void run() {
		run_p();
	}
	protected void run_p(){
		PrintWriter log = null;
		File cfgOut = new File(in, "config.zip");
		File modlistOut = new File(in, "modlist.txt");
		try{
			if(logFile.exists()){
				String f = logFile.getAbsolutePath();
				File logFileOld = new File(f + ".tmp");
				logFile.renameTo(logFileOld);
				logFile = new File(f);
				logFile.createNewFile();
				BufferedReader reader = null;
				try{
					reader = new BufferedReader(new FileReader(logFileOld));
					log = new PrintWriter(new FileWriter(logFile));
					String line = reader.readLine();
					if(line != null){
						if(!line.startsWith("#")){
							log.println("# Backup Log: " + year);
						}
						while(line != null){
							log.println(line);
							line = reader.readLine();
						}
					}
					IOUtils.closeQuietly(reader);
					logFileOld.delete();
				}catch(IOException e){
					IOUtils.closeQuietly(reader);
					e.printStackTrace();
				}
			}else{
				if(!logFile.getParentFile().exists())logFile.getParentFile().mkdirs();
				logFile.createNewFile();
				log = new PrintWriter(new FileWriter(logFile));
				log.println("# Backup Log: " + year);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		boolean success = false;
		try{
			if(cfgOut.exists())cfgOut.delete();
			if(modlistOut.exists())modlistOut.delete();
			ZipperUtil.zip(cfg, cfgOut);
			PrintWriter modids = null;
			try{
				modids = new PrintWriter(new FileWriter(modlistOut));
				modids.println("# Mod List " + String.format("%d mod%s loaded, %d mod%s active", tModCount, tModCount!=1 ? "s" :"", aModCount, aModCount!=1 ? "s" :"" ));
				modids.println(" Minecraft version: " + Loader.instance().getMCVersionString());
				modids.println(" Forge version: " + ForgeVersion.getVersion());
				List<ModContainer> modlist = Loader.instance().getModList();
				for(int i = 0;i<modlist.size();i++){
					ModContainer mc = modlist.get(i);
					modids.println("  " + mc.getModId() + "{" + mc.getVersion() + "} [" + mc.getName() + "] (" + mc.getSource().getName() + (Loader.instance().getModState(mc) != ModState.DISABLED ? ")" : ") [Disabled]"));
				}
				IOUtils.closeQuietly(modids);
			}catch(IOException e){
				IOUtils.closeQuietly(modids);
				e.printStackTrace();
			}
			AutoBackup.log.info("Zipped Config");
			if(!out.getParentFile().exists())out.getParentFile().mkdirs();
			ZipperUtil.zip(in, out);
			success = true;
			cfgOut.delete();
			modlistOut.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(success){
			if(log != null)log.println("S " + out.getName());
			if(runServerCommands){
				FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), createTellraw("\"translate\":\"tomsMod.backupSuccess\",\"with\":[" + (Config.backupSchedule / 60) + "]"));
				FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new TextComponentTranslation("tomsMod.backupSuccess", Config.backupSchedule / 60));
			}
			AutoBackup.log.info("Automatic Backup Success." + (runServerCommands ? " Next backup: " + Config.backupSchedule / 60 + " minutes later." : ""));
			nextTime = Config.backupSchedule;
			stopwatch.reset().start();
		}else{
			if(log != null)log.println("F " + out.getName());
			if(runServerCommands){
				FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), createTellraw("\"translate\":\"tomsMod.backupFailed\",\"with\":[5]"));
				FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new TextComponentTranslation("tomsMod.backupFailed", 5));
			}
			AutoBackup.log.info("Automatic Backup Failed." + (runServerCommands ? " Retry: 5 minutes later." : ""));
			nextTime = 5 * 60;
			stopwatch.reset().start();
		}
		IOUtils.closeQuietly(log);
		if(runServerCommands)FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), "/save-on");
	}
	public static boolean isRunning(){
		return running != null && running.isAlive();
	}
	private static String createTellraw(String text){
		return "/tellraw @a {\"text\":\"[\", \"extra\":[{\"text\":\"Tom's Mod Backup\",\"color\":\"dark_purple\"}, {\"text\":\"] \"}, {"+ text + "}]}";
	}
	public static void startBackup(String name, boolean bg) throws CommandException{
		if(isRunning())throw new CommandException("tomsMod.backupAlreadyRunning");
		FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), createTellraw("\"translate\":\"tomsMod.backupStartedBy\",\"with\":[\""+name+"\"]"));
		FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new TextComponentTranslation("tomsMod.backupStartedBy", name));
		createBackup(bg, true, null);
	}

	public static boolean tick() {
		if(ticksBeforeStart > 0){
			ticksBeforeStart = Math.max(ticksBeforeStart - 1, 0);
			if(ticksBeforeStart < 1)stopwatch.start();
		}else{
			long time = stopwatch.elapsed(TimeUnit.SECONDS);
			if(time >= nextTime){
				return true;
			}else{
				return false;
			}
		}
		return ticksBeforeStart < 1;
	}
	public static void resetTimer(){
		stopwatch.reset();
		if(!Config.enableInitialBackup)ticksBeforeStart = 200;
	}
	public static class AutoBackupCache {
		private File saves;
		private File cfg;
		private String folder;
		private File backupFolder;
	}
}
