package com.timesheetapplication.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name = "monthlytimesheets")
public class MonthlyTimeSheet extends AbstractEntity {

	private Date date;

	@OneToMany
	private Set<DailyTimeSheet> sheets;

	@OneToOne
	private Employee owner;

	private Boolean isEditable;

	private Boolean isApproved;

	private String rejectionReason;

	private TimeSheetState state;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Set<DailyTimeSheet> getSheets() {
		return sheets;
	}

	public void setSheets(Set<DailyTimeSheet> sheets) {
		this.sheets = sheets;
	}

	public Employee getOwner() {
		return owner;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public TimeSheetState getState() {
		return state;
	}

	public void setState(TimeSheetState state) {
		this.state = state;
	}

}
