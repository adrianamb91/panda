package com.timesheetapplication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class Department extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "name")
	private String name;

	// One employee cand manage only one department.
	@OneToOne
	@JoinColumn(name = "manager_id")
	private Employee manager;

	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;
	
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
	
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}


}
