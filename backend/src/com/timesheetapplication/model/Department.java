package com.timesheetapplication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class Department extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "deptno")
	private int deptno;
	
	@Column(name = "name")
	private String name;

	@Column(name = "manager")
	private Employee manager;

	@Column(name = "divno")
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

	public int getDeptno() {
		return deptno;
	}

	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}


}
