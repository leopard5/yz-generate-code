package com.yz.code.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class CommonUtils {

	/**
	 * 关闭线程池
	 * 
	 * @param executorService
	 * @author qiyazhong
	 * @since JDK 1.7
	 * @date 2015年7月23日 上午9:29:19
	 */
	public static void shutdownExecutor(ExecutorService executorService) {
		try {
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e2) {
			Thread.interrupted();
		} finally
		{
			executorService.shutdownNow();
		}
	}

}
