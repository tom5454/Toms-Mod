package com.tom.api.tileentity;

public interface IHeatSource {
	double getHeat();

	double getMaxHeat();

	double transferHeat(double temp, double res);

	public static double[] handleHeatLogic(double temp, double toTemp, double res, double toRes, double maxTransfer) {
		if (temp > toTemp) {
			double tr = Math.min(maxTransfer, (temp - toTemp) / 2);
			double t1 = temp - tr / res;
			double t2 = toTemp + tr / toRes;
			if (res > toRes) {
				if (t2 > t1) {
					return new double[]{t1, t1};
				} else
					return new double[]{t1, t2};
			} else if (res < toRes) {
				if (t2 < t1) {
					return new double[]{t2, t2};
				} else
					return new double[]{t1, t2};
			} else
				return new double[]{t1, t2};
		} else if (temp < toTemp) {
			double tr = Math.min(maxTransfer, (toTemp - temp) / 2);
			double t1 = temp + tr / res;
			double t2 = toTemp - tr / toRes;
			if (res > toRes) {
				if (t2 > t1) {
					return new double[]{t1, t1};
				} else
					return new double[]{t1, t2};
			} else if (res < toRes) {
				if (t2 < t1) {
					return new double[]{t2, t2};
				} else
					return new double[]{t1, t2};
			} else
				return new double[]{t1, t2};
		} else
			return new double[]{temp, toTemp};
	}
}
