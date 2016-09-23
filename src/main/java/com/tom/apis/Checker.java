package com.tom.apis;

public interface Checker{
	int apply(int function);
	public interface CheckerPredicate<T>{
		int apply(T input);
	}
	public static class RunnableStorage implements Runnable{
		public Runnable runnable;
		private final boolean deleteRunnable;
		public RunnableStorage(boolean deleteRunnable) {
			this.deleteRunnable = deleteRunnable;
		}
		@Override
		public void run() {
			if(runnable != null){
				runnable.run();
				if(deleteRunnable)runnable = null;
			}
		}
	}
}