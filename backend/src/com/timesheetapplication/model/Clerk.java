package com.timesheetapplication.model;

import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

public class Clerk extends Employee {

	@ManyToOne
	private Department department;

	@ManyToMany
	private Set<Project> projects;

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}
