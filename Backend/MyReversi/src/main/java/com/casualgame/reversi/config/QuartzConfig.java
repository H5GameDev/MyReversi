package com.casualgame.reversi.config;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.casualgame.reversi.proc.TimerEvent;

@Configuration
public class QuartzConfig {
	@Bean
	public JobDetail teatQuartzDetail() {
		return JobBuilder.newJob(TimerEvent.class).withIdentity("TimerEvent").storeDurably().build();
	}

	@Bean
	public Trigger testQuartzTrigger() {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2)
				.repeatForever();
		return TriggerBuilder.newTrigger().forJob(teatQuartzDetail()).withIdentity("TimerEvent")
				.withSchedule(scheduleBuilder).build();
	}
}