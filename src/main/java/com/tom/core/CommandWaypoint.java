package com.tom.core;

import java.util.ArrayList;
import java.util.List;

import mapwriterTm.map.Marker.RenderType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import com.tom.apis.TomsModUtils;

public class CommandWaypoint extends CommandBase {
	private final boolean isServer;
	public CommandWaypoint(boolean isServer) {
		this.isServer = isServer;
	}
	@Override
	public String getCommandName() {
		return isServer ? "tm_server_waypoint" : "tm_waypoint";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		if(isServer)return "commands.tmWaypoint.serverUsage";
		else 		return "commands.tmWaypoint.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	/**
	 tm_waypoint add Home main 124 73 204 0 Player973 textures/blocks/stone 0xffff0000 true normal icon
	 */

	@Override
	public void execute(MinecraftServer server, ICommandSender cs,
			String[] args) throws CommandException {
		if(args.length < (isServer ? 2 : 3)){
			if(args.length == 0){
				if(isServer)throw new WrongUsageException("commands.tmWaypoint.serverUsage");
				else throw new WrongUsageException("commands.tmWaypoint.usage");
			}else{
				String func = args[0].toLowerCase();
				if(func.equalsIgnoreCase("add")){
					if(isServer)throw new WrongUsageException("commands.tmWaypoint.usage.addServer");
					else throw new WrongUsageException("commands.tmWaypoint.usage.add");
				}else if(func.equalsIgnoreCase("remove")){
					if(isServer)throw new WrongUsageException("commands.tmWaypoint.usage.removeServer");
					else throw new WrongUsageException("commands.tmWaypoint.usage.remove");
				}else{
					if(isServer)throw new WrongUsageException("commands.tmWaypoint.serverUsage");
					else throw new WrongUsageException("commands.tmWaypoint.usage");
				}
			}
		}
		String func = args[0].toLowerCase();
		if(func.equalsIgnoreCase("add")){
			if(args.length > (isServer ? 6 : 7)){
				String name = args[1];
				String group = args[2];
				//int colorB;
				//int colorBA;
				//String icon = "", beamTexture;
				//boolean bordered;
				//boolean borderedA;
				int index = isServer ? 6 : 7;
				EntityPlayerMP player = !isServer ? getPlayer(server, cs, args[index]) : null;
				//RenderType beam;
				//RenderType label;
				//boolean reloadable;
				BlockPos pos = parseBlockPos(cs, args, 3, false);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				int dim = args[6].equals("~") ? cs.getEntityWorld().provider.getDimension() : parseInt(args[6]);
				String icon = args.length > 8 ? args[index + 1] : "";
				int color = args.length > 9 ? parseInt(args[index + 2],16) : 0xff0000;
				boolean reloadable = args.length > 10 ? args[index + 3] != "false" : true;
				RenderType beam = args.length > 11 ? RenderType.fromString(args[index + 4]) : RenderType.NORMAL;
				RenderType label = args.length > 12 ? RenderType.fromString(args[index + 5]) : RenderType.NORMAL;
				String beamTexture = args.length > 13 ? args[index + 6] : "normal";
				//colorB = /*args.length > index+1 ? Integer.parseInt(args[index],16) : 0xff000000;*/0xff000000;
				//colorBA = /*args.length > index+2 ? Integer.parseInt(args[index+1],16) : 0xffffffff*/0xffffffff;
				//bordered = /*args.length > index+3 ? args[index+2].equalsIgnoreCase("true") : */!isIcon;
				//borderedA = /*args.length > index+4 ? args[index+3].equalsIgnoreCase("true") :*/ true;
				if(isServer){
					Minimap.createTexturedWayPointServer(group, x, y, z, dim, name, icon, color, beam, label, reloadable, beamTexture);
					cs.addChatMessage(new TextComponentString(TextFormatting.GREEN+"Success"));
				}else{
					if(player != null){
						Minimap.sendWaypointCreation(group, x, y, z, dim, name, icon, color, beam, label, reloadable, beamTexture, player);
						cs.addChatMessage(new TextComponentString(TextFormatting.GREEN+"Success"));
					}else{
						throw new PlayerNotFoundException();
					}
				}
			}else{
				if(isServer)throw new WrongUsageException("commands.tmWaypoint.usage.addServer");
				else throw new WrongUsageException("commands.tmWaypoint.usage.add");
			}
		}else if(func.equalsIgnoreCase("remove")){
			String name = args[1];
			String group = args[2];
			String player = args[3];
			if(isServer){
				Minimap.deleteWayPointServer(group, name);
			}else{
				EntityPlayer p = cs.getEntityWorld().getPlayerEntityByName(player);
				if(p != null){
					Minimap.sendWaypointRemove(group, name, (EntityPlayerMP) p);
				}else{
					throw new PlayerNotFoundException();
				}
			}
		}else{
			if(isServer)throw new WrongUsageException("commands.tmWaypoint.serverUsage");
			else throw new WrongUsageException("commands.tmWaypoint.usage");
		}
	}
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, "add", "remove") : (args.length > 3 && args.length <= 6 ? getTabCompletionCoordinate(args, 1, pos) : (args.length == 7 ? TomsModUtils.getStringList(((Integer)sender.getEntityWorld().provider.getDimension()).toString()) : (args.length == (isServer ? 11 : 13) || args.length == 12 ? getListOfStringsMatchingLastWord(args, RenderType.getStringList()) : new ArrayList<String>())));
	}
}
