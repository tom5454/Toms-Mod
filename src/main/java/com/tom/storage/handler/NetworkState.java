package com.tom.storage.handler;

public enum NetworkState {
	OFF, POWERED_ONLY, LOADING_CHANNELS, ACTIVE, CHANNEL_OVERLOAD_1x {
		@Override
		public boolean isActiveInTick(long totalWorldTime) {
			return fullyActive() && totalWorldTime % 2 == 0;
		}
	},
	CHANNEL_OVERLOAD_2x {
		@Override
		public boolean isActiveInTick(long totalWorldTime) {
			return fullyActive() && totalWorldTime % 3 == 0;
		}
	},
	CHANNEL_OVERLOAD_3x {
		@Override
		public boolean isActiveInTick(long totalWorldTime) {
			return fullyActive() && totalWorldTime % 4 == 0;
		}
	};

	public boolean fullyActive() {
		return this != OFF && this != LOADING_CHANNELS && this != POWERED_ONLY;
	}

	public boolean isPowered() {
		return this != OFF;
	}

	public boolean isLoading() {
		return this == LOADING_CHANNELS;
	}

	public boolean showChannels() {
		return fullyActive() || isLoading();
	}

	public boolean isActiveInTick(long totalWorldTime) {
		return fullyActive();
	}
}