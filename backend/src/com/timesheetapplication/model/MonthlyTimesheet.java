package com.timesheetapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.timesheetapplication.enums.MonthlyTimesheetStatus;

@Entity
@Table(name = "monthly_timesheet")
public class MonthlyTimesheet extends AbstractEntity {

	@Column(name = "date")
	@Temporal(TemporalType.DATE)
	private Date date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Employee owner;

	@OneToMany(mappedBy = "mTimesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DailyTimeSheet> timesheets = new ArrayList<DailyTimeSheet>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private MonthlyTimesheetStatus status = MonthlyTimesheetStatus.OPEN;

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

	public List<DailyTimeSheet> getTimesheets() {
		return timesheets;
	}

	public void setTimesheets(List<DailyTimeSheet> timesheets) {
		this.timesheets = timesheets;
	}

	public MonthlyTimesheetStatus getStatus() {
		return status;
	}

	public void setStatus(MonthlyTimesheetStatus status) {
		this.status = status;
	}

}
