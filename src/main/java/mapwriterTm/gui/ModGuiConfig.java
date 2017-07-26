package mapwriterTm.gui;

import java.util.ArrayList;
import java.util.List;

import mapwriterTm.config.ConfigurationHandler;
import mapwriterTm.util.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.BooleanEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModGuiConfig extends GuiConfig {
	public ModGuiConfig(GuiScreen guiScreen) {
		super(guiScreen, getConfigElements(), Reference.MOD_ID, Reference.catOptions, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
	}

	/** Compiles a list of config elements */
	private static List<IConfigElement> getConfigElements() {
		// Add categories to config GUI
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyCategoryElement(Reference.catOptions, "mw.configgui.ctgy.general", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.catOptions)).getChildElements()));

		list.add(new DummyCategoryElement(Reference.catFullMapConfig, "mw.configgui.ctgy.fullScreenMap", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.catFullMapConfig)).getChildElements(), MapModeConfigEntry.class));

		list.add(new DummyCategoryElement(Reference.catLargeMapConfig, "mw.configgui.ctgy.largeMap", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.catLargeMapConfig)).getChildElements(), MapModeConfigEntry.class));

		list.add(new DummyCategoryElement(Reference.catSmallMapConfig, "mw.configgui.ctgy.smallMap", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.catSmallMapConfig)).getChildElements(), MapModeConfigEntry.class));
		return list;
	}

	public static class MapModeConfigEntry extends CategoryEntry {
		public MapModeConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			String QualifiedName = this.configElement.getQualifiedName();
			// This GuiConfig object specifies the configID of the object
			// and as
			// such will force-save when it is closed. The parent
			// GuiConfig object's entryList will also be refreshed to
			// reflect
			// the changes.
			return new GuiConfig(this.owningScreen, this.getConfigElement().getChildElements(), this.owningScreen.modID, QualifiedName, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, this.owningScreen.title);
		}
	}

	public static class ModBooleanEntry extends ButtonEntry {
		protected final boolean beforeValue;
		protected boolean currentValue;

		public ModBooleanEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			this.beforeValue = Boolean.valueOf(configElement.get().toString());
			this.currentValue = this.beforeValue;
			this.btnValue.enabled = this.enabled();
			this.updateValueButtonText();
		}

		@Override
		public void updateValueButtonText() {
			this.btnValue.displayString = I18n.format(String.valueOf(this.currentValue));
			this.btnValue.packedFGColour = this.currentValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
		}

		@Override
		public void valueButtonPressed(int slotIndex) {
			if (this.enabled()) {
				this.currentValue = !this.currentValue;
			}
		}

		@Override
		public boolean isDefault() {
			return this.currentValue == Boolean.valueOf(this.configElement.getDefault().toString());
		}

		@Override
		public void setToDefault() {
			if (this.enabled()) {
				this.currentValue = Boolean.valueOf(this.configElement.getDefault().toString());
				this.updateValueButtonText();
			}
		}

		@Override
		public boolean isChanged() {
			return this.currentValue != this.beforeValue;
		}

		@Override
		public void undoChanges() {
			if (this.enabled()) {
				this.currentValue = this.beforeValue;
				this.updateValueButtonText();
			}
		}

		@Override
		public boolean saveConfigElement() {
			if (this.enabled() && this.isChanged()) {
				this.configElement.set(this.currentValue);
				return this.configElement.requiresMcRestart();
			}
			return false;
		}

		@Override
		public Boolean getCurrentValue() {
			return this.currentValue;
		}

		@Override
		public Boolean[] getCurrentValues() {
			return new Boolean[]{this.getCurrentValue()};
		}

		@Override
		public boolean enabled() {
			for (IConfigEntry entry : this.owningEntryList.listEntries) {
				if (entry.getName().equals("circular") && (entry instanceof BooleanEntry)) { return Boolean.valueOf(entry.getCurrentValue().toString()); }
			}

			return true;
		}
	}

	public static class ModNumberSliderEntry extends NumberSliderEntry {
		private boolean enabled = true;

		public ModNumberSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			((GuiSlider) this.btnValue).precision = 2;
			this.updateValueButtonText();
		}

		public void setValue(double val) {
			((GuiSlider) this.btnValue).setValue(val);
			((GuiSlider) this.btnValue).updateSlider();
		}

		@Override
		public boolean enabled() {
			return owningScreen.isWorldRunning ? !owningScreen.allRequireWorldRestart && !configElement.requiresWorldRestart() && this.enabled : this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}
