package com.timesheetapplication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activity")
public class Activity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "duration")
	private Float duration;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "dtimesheet_id")
	private DailyTimeSheet timesheet;

	@Column(name = "is_extra")
	private Boolean isExtra;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setIsExtra(Boolean isExtra) {
		this.isExtra = isExtra;
	}

}
