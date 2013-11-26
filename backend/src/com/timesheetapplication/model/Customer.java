package com.timesheetapplication.model;

import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = "customer")
public class Customer extends AbstractEntity {

	private String name;

	@OneToMany
	private Set<Project> projects;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}
