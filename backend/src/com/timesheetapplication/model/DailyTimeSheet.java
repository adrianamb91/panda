package com.timesheetapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "daily_timesheet")
public class DailyTimeSheet extends AbstractEntity {
	@Temporal(TemporalType.DATE)
	@Column(name = "date")
	private Date date;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Employee owner;

	@OneToMany (mappedBy = "timesheet")
	private List<Activity> activities = new ArrayList<Activity>();

	@ManyToOne
	@JoinColumn(name = "month_timesheet_id")
	private MonthlyTimesheet mTimesheet;
	
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

	public MonthlyTimesheet getmTimesheet() {
		return mTimesheet;
	}

	public void setmTimesheet(MonthlyTimesheet mTimesheet) {
		this.mTimesheet = mTimesheet;
	}
	
}
