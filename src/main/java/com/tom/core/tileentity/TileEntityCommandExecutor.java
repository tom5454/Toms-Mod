package com.tom.core.tileentity;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.map.MapHandler;
import com.tom.lib.api.map.RenderType;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;
import com.tom.util.TomsModUtils;

public class TileEntityCommandExecutor extends TileEntityTomsMod implements ITMCompatPeripheral {

	@Override
	public String getType() {
		return "CommandExecutor";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"help", "getServerStats", "waypoints", "getPlayerNBT", "setPlayerNBT", "getNewNBTTagCompound", "getNewNBTTagList", "getNewNBTIntArray"};
	}

	@Override
	public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
		if (method == 0) {
			return new Object[]{"Advanced Command Block is an extended command block", this.getMethodNames()[2] + "(player,item/block name,slot,[amount],[tag in JSON format]) Replace " + "item in the player's slot", this.getMethodNames()[3] + " returns information about the server. Returns Is " + "Singleplayer, Current Player Count, " + "Is Hardcore, Build Limit, Level Name", this.getMethodNames()[4] + "(String[] arguments) runs the waypoint " + "editor interface, Arguments = " + "Arguments in the command", this.getMethodNames()[5] + "(player) get the player's " + "NBTTagCompound", this.getMethodNames()[6] + "(player,nbt string) sets the player's " + "NBT", this.getMethodNames()[7] + "() Returns a new instance of the " + "NBTTagCompound", this.getMethodNames()[8] + "() Returns a new " + "instance of the NBTTagList", this.getMethodNames()[9] + "() " + "Returns a new instance of the NBTTagIntArray"};
		} else if (method == 1) {
			MinecraftServer s = TomsModUtils.getServer();
			if (s != null) {
				String f = s.getFile("a").getAbsolutePath();
				String folder = f.substring(0, f.length() - 1) + (s.isSinglePlayer() ? "saves\\" + s.getFolderName() : s.getFolderName());
				boolean mode = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : false) : false;
				if (mode)
					return new Object[]{s.isSinglePlayer(), s.getCurrentPlayerCount(), s.isHardcore(), s.getBuildLimit(), s.getFolderName(), folder};
				else
					return new Object[]{s.isSinglePlayer(), s.getCurrentPlayerCount(), s.isHardcore(), s.getBuildLimit(), s.getFolderName()};
			}
		} else if (method == 2) {
			String[] args = new String[a.length];
			for (int i = 0;i < a.length;i++) {
				// System.out.println(a[i]);
				args[i] = a[i].toString();
			}
			if (args.length < 3) { throw new LuaException("Not Enough Arguments"); }
			String func = args[0].toLowerCase(Locale.ROOT);
			if (func.equalsIgnoreCase("add") && args.length > 7) {
				String name = args[1];
				String group = args[2];
				int x;
				int y;
				int z;
				int dim;
				int color = 0;
				// int colorB;
				// int colorBA;
				String icon = "", beamTexture = "";
				// boolean bordered;
				// boolean borderedA;
				String player = args[7];
				RenderType beam;
				RenderType label;
				boolean reloadable;
				try {
					x = Integer.parseInt(args[3]);
					y = Integer.parseInt(args[4]);
					z = Integer.parseInt(args[5]);
					dim = Integer.parseInt(args[6]);
					icon = args.length > 8 ? args[8] : "";
					color = args.length > 9 ? Integer.parseInt(args[9], 16) : 0xff0000;
					reloadable = args.length > 10 ? args[10] != "false" : true;
					beam = args.length > 11 ? RenderType.fromString(args[11]) : RenderType.NORMAL;
					label = args.length > 12 ? RenderType.fromString(args[12]) : RenderType.NORMAL;
					beamTexture = args.length > 13 ? args[13] : "normal";
				} catch (NumberFormatException e) {
					throw new LuaException("Invalid numbers");
				}
				EntityPlayer p = this.world.getPlayerEntityByName(player);
				if (p != null) {
					MapHandler.sendWaypointCreation(group, x, y, z, dim, name, icon, color, beam, label, reloadable, beamTexture, (EntityPlayerMP) p);
					// cs.addChatMessage(new
					// ChatComponentText(EnumChatFormatting.GREEN+"Success"));
				}
			} else if (func.equalsIgnoreCase("remove")) {
				String name = args[1];
				String group = args[2];
				String player = args[3];
				EntityPlayer p = world.getPlayerEntityByName(player);
				if (p != null) {
					MapHandler.sendWaypointRemove(group, name, (EntityPlayerMP) p);
				}
			}
		} else if (method == 3) {
			if (a.length > 0 && a[0] instanceof String) {
				EntityPlayer player = world.getPlayerEntityByName((String) a[0]);
				if (player != null) {
					NBTTagCompound pTag = new NBTTagCompound();
					player.writeToNBT(pTag);
					return new Object[]{new LuaNBTTagCompound(pTag)};
				}
			}
		} else if (method == 4) {
			if (a.length > 1 && a[0] instanceof String && a[1] instanceof String) {
				final EntityPlayer player = world.getPlayerEntityByName((String) a[0]);
				if (player != null) {
					NBTTagCompound pTag = new NBTTagCompound();
					player.writeToNBT(pTag);
					NBTBase tagB = new NBTTagCompound();
					try {
						tagB = JsonToNBT.getTagFromJson((String) a[1]);
					} catch (NBTException e) {
						throw new LuaException("Invalid NBT " + e.toString());
					}
					if (tagB instanceof NBTTagCompound) {
						NBTTagCompound tag = (NBTTagCompound) tagB;
						Set<String> tSet = tag.getKeySet();
						for (String v : tSet) {
							NBTBase c = tag.getTag(v);
							pTag.setTag(v, c);
						}
						player.readFromNBT(pTag);
						TomsModUtils.getServer().addScheduledTask(new Runnable() {

							@Override
							public void run() {
								player.inventoryContainer.detectAndSendChanges();
							}
						});
					} else {
						throw new LuaException("Invalid NBT");
					}
				}
			}
		} else if (method == 5)
			return new Object[]{new LuaNBTTagCompound()};
		else if (method == 6)
			return new Object[]{new LuaNBTTagList()};
		else if (method == 7) {
			int size = a.length > 0 ? (a[0] instanceof Double ? MathHelper.floor((Double) a[0]) : 3) : 3;
			return new Object[]{new LuaNBTTagIntArray(size)};
		}
		return null;
	}

	public static class LuaNBTTagCompound implements ITMLuaObject {
		final NBTTagCompound base;

		public LuaNBTTagCompound() {
			this.base = new NBTTagCompound();
		}

		public LuaNBTTagCompound(NBTTagCompound in) {
			this.base = in;
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"export", "getNumber", "getString", "setString", "setNumber", "setTagList", "getTagList", "setTagCompound", "getTagCompound", "getIntArray", "setIntArray", "getBoolean", "setBoolean"};
		}

		@Override
		public Object[] call(IComputer context, String m, Object[] a) throws LuaException {
			int method = Arrays.binarySearch(getMethodNames(), m);
			if (method == 0)
				return new Object[]{this.toString()};
			else if (method == 1) {
				if (a.length > 0) {
					int type = a.length > 1 ? (a[1] instanceof Double ? MathHelper.floor((Double) a[1]) : 0) : 0;
					String n = a[0].toString();
					if (type == 0) {
						return new Object[]{base.getDouble(n)};
					} else if (type == 1) {
						return new Object[]{base.getInteger(n)};
					} else if (type == 2) {
						return new Object[]{base.getFloat(n)};
					} else if (type == 3) {
						return new Object[]{base.getByte(n)};
					} else if (type == 4) {
						return new Object[]{base.getLong(n)};
					} else if (type == 5) {
						return new Object[]{base.getShort(n)};
					} else
						throw new LuaException("Invalid type");
				}
			} else if (method == 2) {
				if (a.length > 0) {
					String n = a[0].toString();
					return new Object[]{base.getString(n)};
				}
			} else if (method == 3) {
				if (a.length > 1) {
					String n = a[0].toString();
					String v = a[1].toString();
					base.setString(n, v);
				}
			} else if (method == 4) {
				if (a.length > 1 && a[1] instanceof Double) {
					int type = a.length > 2 ? (a[2] instanceof Double ? MathHelper.floor((Double) a[2]) : 0) : 0;
					double value = (Double) a[1];
					String n = a[0].toString();
					if (type == 0) {
						base.setDouble(n, value);
					} else if (type == 1) {
						base.setInteger(n, MathHelper.floor(value));
					} else if (type == 2) {
						base.setFloat(n, new Float(value));
					} else if (type == 3) {
						base.setByte(n, (byte) MathHelper.floor(value));
					} else if (type == 4) {
						base.setLong(n, new Long(MathHelper.floor(value)));
					} else if (type == 5) {
						base.setShort(n, (short) MathHelper.floor(value));
					} else
						throw new LuaException("Invalid type");
				}
			} else if (method == 5) {
				if (a.length > 1) {
					NBTBase tag = null;
					String n = a[0].toString();
					// System.out.println(a[1]);
					try {
						tag = JsonToNBT.getTagFromJson("{v:" + (String) a[1] + "}");
					} catch (NBTException e) {
						throw new LuaException("Invalid NBT " + e.toString());
					}
					if (tag instanceof NBTTagCompound) {
						base.setTag(n, ((NBTTagCompound) tag).getTag("v"));
					} else
						throw new LuaException("Invalid NBT");
				}
			} else if (method == 6) {
				if (a.length > 0) {
					String n = a[0].toString();
					NBTTagList list = (NBTTagList) base.getTag(n);
					return new Object[]{new LuaNBTTagList(list)};
				}
			} else if (method == 7) {
				if (a.length > 1) {
					NBTBase tag = null;
					String n = a[0].toString();
					try {
						tag = JsonToNBT.getTagFromJson(a[1].toString());
					} catch (NBTException e) {
						throw new LuaException("Invalid NBT " + e.toString());
					}
					if (tag instanceof NBTTagCompound) {
						base.setTag(n, tag);
					} else
						throw new LuaException("Invalid NBT");
				}
			} else if (method == 8) {
				if (a.length > 0) {
					String n = a[0].toString();
					NBTTagCompound tag = base.getCompoundTag(n);
					return new Object[]{new LuaNBTTagCompound(tag)};
				}
			} else if (method == 9) {
				if (a.length > 0) {
					String n = a[0].toString();
					int[] array = base.getIntArray(n);
					return new Object[]{new LuaNBTTagIntArray(array)};
				}
			} else if (method == 10) {
				if (a.length > 1) {
					NBTBase tag = null;
					String n = a[0].toString();
					try {
						tag = JsonToNBT.getTagFromJson(a[1].toString());
					} catch (NBTException e) {
						throw new LuaException("Invalid NBT " + e.toString());
					}
					if (tag instanceof NBTTagIntArray) {
						base.setTag(n, tag);
					} else
						throw new LuaException("Invalid NBT");
				}
			} else if (method == 11) {
				if (a.length > 0) {
					String n = a[0].toString();
					return new Object[]{base.getBoolean(n)};
				}
			} else if (method == 12) {
				if (a.length > 1 && a[1] instanceof Boolean) {
					String n = a[0].toString();
					boolean v = (Boolean) a[1];
					base.setBoolean(n, v);
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return base != null ? base.toString() : "{}";
		}

		@Override
		public long getID() {
			return 0;
		}
	}

	public static class LuaNBTTagList implements ITMLuaObject {
		final NBTTagList base;

		public LuaNBTTagList() {
			this.base = new NBTTagList();
		}

		public LuaNBTTagList(NBTTagList in) {
			this.base = in;
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"export", "getObjectAt", "appendObject", "tagCount"};
		}

		@Override
		public Object[] call(IComputer context, String m, Object[] a) throws LuaException {
			int method = Arrays.binarySearch(getMethodNames(), m);
			if (method == 0)
				return new Object[]{this.toString()};
			else if (method == 1) {
				if (a.length > 0 && a[0] instanceof Double) {
					int entry = MathHelper.floor((Double) a[0]) - 1;
					if (base != null && entry < base.tagCount()) {
						int type = a.length > 1 ? (a[1] instanceof Double ? MathHelper.floor((Double) a[1]) : 1) : 0;
						if (type == 0) {
							NBTTagCompound tag = base.getCompoundTagAt(entry);
							if (tag != null)
								return new Object[]{new LuaNBTTagCompound(tag)};
							else
								return new Object[]{new LuaNBTTagCompound()};
						} else if (type == 1) {
							return new Object[]{base.getStringTagAt(entry)};
						} else
							throw new LuaException("Invalid type");
					}
				}
			} else if (method == 2) {
				if (a.length > 0) {
					if (base != null) {
						int type = a.length > 1 ? (a[1] instanceof Double ? MathHelper.floor((Double) a[1]) : 1) : 0;
						String tagS = a[0].toString();
						if (type == 0) {
							NBTBase tag = null;
							try {
								tag = JsonToNBT.getTagFromJson(tagS);
							} catch (NBTException e) {
								throw new LuaException("Invalid NBT " + e.toString());
							}
							if (tag != null && tag instanceof NBTTagCompound) {
								base.appendTag(base);
							}
						} else if (type == 1) {
							base.appendTag(new NBTTagString(tagS));
						} else
							throw new LuaException("Invalid type");
					}
				}
			} else if (method == 3)
				return new Object[]{base.tagCount()};
			return null;
		}

		@Override
		public String toString() {
			return base != null ? base.toString() : "[]";
		}

		@Override
		public long getID() {
			return 0;
		}
	}

	public static class LuaNBTTagIntArray implements ITMLuaObject {
		int[] array;

		public LuaNBTTagIntArray(int[] in) {
			this.array = in;
		}

		public LuaNBTTagIntArray(int size) {
			this.array = new int[size];
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"get", "set", "lenth", "reCreate", "export"};
		}

		@Override
		public Object[] call(IComputer context, String m, Object[] a) throws LuaException {
			int method = Arrays.binarySearch(getMethodNames(), m);
			if (method == 0) {
				if (a.length > 0 && a[0] instanceof Double) {
					int entry = MathHelper.floor((Double) a[0]) - 1;
					if (array != null && entry < array.length)
						return new Object[]{array[entry]};
				}
			} else if (method == 1) {
				if (a.length > 1 && a[0] instanceof Double && a[1] instanceof Double) {
					int entry = MathHelper.floor((Double) a[0]) - 1;
					int value = MathHelper.floor((Double) a[1]);
					if (array != null && entry < array.length) {
						int old = array[entry];
						array[entry] = value;
						return new Object[]{old};
					}
				}
			} else if (method == 2) {
				if (array != null)
					return new Object[]{array.length};
				else
					return new Object[]{0};
			} else if (method == 3) {
				int size = a.length > 0 ? (a[0] instanceof Double ? MathHelper.floor((Double) a[0]) : 3) : 3;
				this.array = new int[size];
			} else if (method == 4)
				return new Object[]{this.toString()};
			return null;
		}

		@Override
		public String toString() {
			String ret = "";
			for (int i : this.array) {
				ret = ret + "," + i;
			}
			return "[" + ret.substring(1) + "]";
		}

		@Override
		public long getID() {
			return 0;
		}
	}
}
