package com.timesheetapplication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "division")
public class Division extends AbstractEntity{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "name")
	private String name;

	@Column(name = "manager")
	private Employee manager;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}
}

