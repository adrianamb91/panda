package com.timesheetapplication.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "daily_timesheet")
public class DailyTimeSheet extends AbstractEntity {

	@Column(name = "date")
	private Date date;

	@Column(name = "owner")
	private Employee owner;

	@OneToMany (mappedBy = "timesheet")
	private List<Activity> activities;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Employee getOwner() {
		return owner;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
}
