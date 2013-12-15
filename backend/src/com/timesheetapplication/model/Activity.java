package com.timesheetapplication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activity")
public class Activity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "duration")
	private Float duration;

	@Column(name = "description")
	private String description;

	@ManyToOne
	private DailyTimeSheet timesheet;

	@Column(name = "is_extra")
	private Boolean isExtra;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getDuration() {
		return duration;
	}

	public void setDuration(Float duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DailyTimeSheet getTimesheet() {
		return timesheet;
	}

	public void setTimesheet(DailyTimeSheet timesheet) {
		this.timesheet = timesheet;
	}

	public Boolean getIsExtra() {
		return isExtra;
	}

	public void setIsExtra(Boolean isExtra) {
		this.isExtra = isExtra;
	}

}
