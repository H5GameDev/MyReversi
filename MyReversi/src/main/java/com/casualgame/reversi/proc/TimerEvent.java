
package com.casualgame.reversi.proc;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.casualgame.reversi.service.SessionService;

public class TimerEvent extends QuartzJobBean {
	@Autowired
	private SessionService sessionService;
	private static final Logger LOG = LoggerFactory.getLogger(TimerEvent.class);
	private static int e = 0;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		sessionService.expireTick();
		if ((e = (e + 1) % 80) == 0)
			sessionService.printStatus();
	}
}
