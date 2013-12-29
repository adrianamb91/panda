package com.timesheetapplication.enums;

public enum MonthlyTimesheetStatus {

	OPEN(0), SUBMITTED(1), APPROVED(2), REJECTED(3);

	private Integer type;

	private MonthlyTimesheetStatus(Integer i) {
		this.type = i;
	}
	
}
