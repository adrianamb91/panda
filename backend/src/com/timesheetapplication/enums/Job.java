package com.timesheetapplication.enums;

import javax.persistence.Entity;
import javax.persistence.Table;

public enum Job {

	CLERK(0), DEPT_MANAGER(1), DIVISION_MANAGER(2), CEO(3), ADMIN(4);

	private Integer type;

	private Job(Integer i) {
		this.type = i;
	}
}
