package com.timesheetapplication.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.timesheetapplication.enums.Job;

@Entity
@Table(name = "employee")
public class Employee extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "email")
	private String email;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "firstname")
	private String firstName;

	@Column(name = "lastname")
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "deptno")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "manager_id")
	private Employee manager;

	@Enumerated(EnumType.STRING)
	@Column(name = "job")
	private Job job;

//	@ManyToMany(cascade = { CascadeType.ALL })
//	@JoinTable(name = "employee_project_map", joinColumns = @JoinColumn(name = "proj_id"), inverseJoinColumns = @JoinColumn(name = "emp_id"))
//	private List<Project> projects = new ArrayList<Project>();
//
//	@OneToMany(mappedBy = "owner")
//	private List<MonthlyTimesheet> mTimesheets = new ArrayList<MonthlyTimesheet>();

//	public List<Project> getProjects() {
//		return projects;
//	}
//
//	public void setProjects(List<Project> projects) {
//		this.projects = projects;
//	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

//	public List<MonthlyTimesheet> getmTimesheets() {
//		return mTimesheets;
//	}
//
//	public void setmTimesheets(List<MonthlyTimesheet> mTimesheets) {
//		this.mTimesheets = mTimesheets;
//	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

}
