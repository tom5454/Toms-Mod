package mapwriterTm.forge;

import org.lwjgl.input.Keyboard;

import mapwriterTm.Mw;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MwKeyHandler
{
	public static KeyBinding keyMapGui = new KeyBinding("key.mw_open_gui", Keyboard.KEY_M, "MapwriterTm");
	public static KeyBinding keyNewMarker = new KeyBinding("key.mw_new_marker", Keyboard.KEY_INSERT, "MapwriterTm");
	public static KeyBinding keyMapMode = new KeyBinding("key.mw_next_map_mode", Keyboard.KEY_N, "MapwriterTm");
	public static KeyBinding keyNextGroup = new KeyBinding("key.mw_next_marker_group", Keyboard.KEY_COMMA, "MapwriterTm");
	public static KeyBinding keyTeleport = new KeyBinding("key.mw_teleport", Keyboard.KEY_T, "MapwriterTm");
	public static KeyBinding keyZoomIn = new KeyBinding("key.mw_zoom_in", Keyboard.KEY_ADD, "MapwriterTm");
	public static KeyBinding keyZoomOut = new KeyBinding("key.mw_zoom_out", Keyboard.KEY_SUBTRACT, "MapwriterTm");
	public static KeyBinding keyUndergroundMode = new KeyBinding("key.mw_underground_mode", Keyboard.KEY_U, "MapwriterTm");

	public final KeyBinding[] keys =
		{
				keyMapGui,
				keyNewMarker,
				keyMapMode,
				keyNextGroup,
				keyTeleport,
				keyZoomIn,
				keyZoomOut,
				keyUndergroundMode
		};

	public MwKeyHandler()
	{
		//ArrayList<String> listKeyDescs = new ArrayList<String>();
		// Register bindings
		for (KeyBinding key : this.keys)
		{
			if (key != null)
			{
				ClientRegistry.registerKeyBinding(key);
			}
			//listKeyDescs.add(key.getKeyDescription());
		}
	}

	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent event)
	{
		this.checkKeys();
	}

	private void checkKeys()
	{
		for (KeyBinding key : this.keys)
		{
			if ((key != null) && key.isPressed())
			{
				Mw.getInstance().onKeyDown(key);
			}
		}
	}
}
