package com.timesheetapplication.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "division")
public class Division extends AbstractEntity {

	@Column(name = "name")
	private String name;

	@OneToOne
	@JoinColumn(name = "manager_id")
	private Employee manager;

	@OneToMany(mappedBy = "division")
	private List<Department> departments = new ArrayList<Department>();

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

	public List<Department> getDepartments() {
		return departments;
	}
}
