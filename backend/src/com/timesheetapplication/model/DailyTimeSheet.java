package com.timesheetapplication.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Table;

@Table(name = "timesheet")
public class DailyTimeSheet extends AbstractEntity{

	private Date date;
	
	private Employee owner;
	
	private Set<Activity> activities;
	
	
	
}
