/**
 * Created by Adri on 1/10/14.
 */

var serverIp = "localhost:8080";

$(document).ready(function() {
	console.log("document ready");
	mainContent = document.getElementById('view_page').innerHTML;

	getLoginDataFromServer();
	loadManagers();
	loadDivisions();
	loadDepartments();
	loadEmployees();
	loadJobs();

	$('#settingsBtn').click(function() {
		// $('#view_page').load("settings.html #view_settings");
		$('#view_page').hide();
		$('#view_settings').show();
	});
	$('#homeBtn').click(function() {
		// document.getElementById('view_page').innerHTML = mainContent;
		$('#view_settings').hide();
		$('#view_page').show();
	});
	$('#logoutBtn').click(function() {
		endSession();
	});
});

//function createEmployee() {
//window.open('http://' + serverIp
//+ '/TimesheetManagement/addEmployee.html', '_self', false);
//}

//function createDivision() {
//window.open('http://' + serverIp
//+ '/TimesheetManagement/addDivision.html', '_self', false);
//}

//function createDepartment() {
//window.open('http://' + serverIp
//+ '/TimesheetManagement/addDepartment.html', '_self', false);
//}

function populateHeaderData(name, date, job) {
	document.getElementById('topBarInfo').innerHTML = date
	+ "; &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp You are logged in as:"
	+ name + "; &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp Job:" + job;
}

function getLoginDataFromServer() {
	console.log("getLoginDataFromServer");
	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "init"
		},
		success : function(data, textStatus, jqXHR) {
			if (data.ok == true) {
				loginData = data;
				globalDate = loginData.date;
				populateHeaderData(loginData.name, loginData.date,
						loginData.job);
			} else {
				window.open('http://' + serverIp
						+ '/TimesheetManagement/login.html', '_self', false);
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function endSession() {
	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "done"
		},
		success : function(data, textStatus, jqXHR) {
			window.open('http://' + serverIp
					+ '/TimesheetManagement/login.html', '_self', false);
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function saveDivision() {
	//face cerere pentru salvare divizie noua, apelata cand apesi butonul 'Save'
	//din dialogul de adaugare divizie
	var name = document.getElementById('nameDiv').value;
	var divManagerName = document.getElementById('bossDivDrop');
	var managerName = null;

	if (divManagerName.selectedIndex != 0) {
		managerName = divManagerName.options[divManagerName.selectedIndex].text;
	} else {
		console.log("N-ai selectat manager!");
		managerName="";
	}

	$.ajax({
		type : "GET",
		url : "ManagementServlet",
		data : {
			phase : "saveDivision",
			name : "" + name,
			manager : "" + managerName
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			if (data.ok == true) {
				alert("saved");
				loadDivisions();
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function loadDivisions() {
	//populeaza dropdown cu divizii (folosit in dialogul de adaugare departament)
	//+ populeaza tabelul cu divizii din pagina principala

	$.ajax({
		type : "GET",
		url : "ManagementServlet",
		data : {
			phase : "loadAllDivisions"
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			if (data.ok == true) {
				populateDropdown(data, "divDeptDrop");
				populateDivisionsTable(data);
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function populateDivisionsTable(data) {
	var tsTable = $('#divisions-table');
	var noEntries = $('#no-divisions');
	var existingEntries = $('#exist-divisions');

	$('#divisions-table > tbody').empty();

	if (data.ok == true) {
		tsTable.show();
		noEntries.hide();
		existingEntries.show();

		for (var i = 0; i < data.size; i++) {
			// index = 0 -> selects table head so this row should be
			// skipped
			var rowIndex = i + 1;
			tsTable.append("<tr>" + 
					'<td class="edit">' + data.elements[i] + "</td>" + 
					"<td>" + data.managers[i] + "</td>"
					+ "<td>" + '<a href="#" onClick="editActivity(' + rowIndex
					+ ')">edit</a>' + "<td>"
					+ '<a href="#" onClick="removeActivity(' + rowIndex
					+ ')">remove</a>' + "</tr>");
		} 
	} else {
		noEntries.show();
		tsTable.hide();
		existingEntries.hide();
	}
}

function loadManagers() {

	$.ajax({
		type : "GET", 
		url : "ManagementServlet",
		data : {
			phase : "loadAllPossibleManagers"
		},
		success : function(data, textSataus, jqXHR) {
			populateDropdown(data, "bossDeptDrop");
			populateDropdown(data, "bossDivDrop");
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});


}

//form function
$(function() {
	var form = $('#new_division'), allFields = $(':text', form);
	$("#add-division-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save" : function() {
				console.log("save clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveDivision();
					$(this).dialog("close");
				}
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
		}
	});

	// create new division from the main page
	$("#add-division").button().click(function() {
		$("#add-division-dialog").dialog("open");
	});

	// Form validation
	function updateTips(t) {
		var tips = $(".validateTips");

		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}

	function checkLength(o, n, min, max) {
		var isValid = true;
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			updateTips("Length of " + n + " must be between " + min + " and "
					+ max + " characters.");
			isValid = false;
		}
		return isValid;
	}

	function checkRegexp(o, regexp, n) {
		var isValid = true;
		if (!(regexp.test(o.val()))) {
			o.addClass("ui-state-error");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateSelectedManager(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat manager");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;
		var divisionName = $('[name="nameDiv"]', form);

		//bValid = bValid && checkLength(divisionName, "divisionName", 1, 200);

		//var manager = document.getElementById('bossDivDrop');
		//bValid = bValid
		//    && validateSelectedManager(manager, "You must select a manager");

		return bValid;
	}
});

function saveDepartment() {
	var departmentName = document.getElementById('nameDept').value;

	var deptManagerName = document.getElementById('bossDeptDrop');
	var managerName = null;

	if (deptManagerName.selectedIndex != 0) {
		managerName = deptManagerName.options[deptManagerName.selectedIndex].text;
	} else {
		console.log("N-ai selectat manager");
		managerName="";
	}

	var deptDivisionName = document.getElementById('divDeptDrop');
	var divisionName = null;

	if (deptDivisionName.selectedIndex != 0) {
		divisionName = deptDivisionName.options[deptDivisionName.selectedIndex].text;
	} else {
		console.log("N-ai selectat divizie");
		divisionName="";
	}

	$.ajax({
		type: "GET",
		url : "ManagementServlet",
		data : {phase : "saveDepartment",
			name : "" + departmentName,
			manager : "" + managerName,
			division: "" + divisionName
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function populateDepartmentsTable(data) {
	var tsTable = $('#departments-table');
	var noEntries = $('#no-departments');
	var existingEntries = $('#exist-departments');

	$('#departments-table > tbody').empty();

	if (data.ok == true) {
		tsTable.show();
		noEntries.hide();
		existingEntries.show();

		for (var i = 0; i < data.size; i++) {
			// index = 0 -> selects table head so this row should be
			// skipped
			var rowIndex = i + 1;
			tsTable.append("<tr>" + 
					'<td class="edit">' + data.elements[i] + "</td>" + 
					"<td>" + data.managers[i] + "</td>" + 
					"<td>" + data.divisions[i] + "</td>"
					+ "<td>" + '<a href="#" onClick="editActivity(' + rowIndex
					+ ')">edit</a>' + "<td>"
					+ '<a href="#" onClick="removeActivity(' + rowIndex
					+ ')">remove</a>' + "</tr>");
		} 
	} else {
		noEntries.show();
		tsTable.hide();
		existingEntries.hide();
	}
}

function loadDepartments() {
//	TODO:
	$.ajax({
		type : "GET",
		url : "ManagementServlet",
		data : {
			phase : "loadAllDepartments"
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			if (data.ok == true) {
				populateDropdown(data, "empDeptDrop");
				populateDepartmentsTable(data);
				alert("whooopie");
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
	
}

$(function() {
	var form = $('#new_department'), allFields = $(':text', form);
	$("#add-department-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save" : function() {
				console.log("save department clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveDepartment();
					$(this).dialog("close");
				}
			},
			Cancel : function() {
				$(this).dialog("close");
				loadDepartments();
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
			loadDepartments();
		}
	});

	// create new division from the main page
	$("#add-department").button().click(function() {
		$("#add-department-dialog").dialog("open");
	});

	// Form validation
	function updateTips(t) {
		var tips = $(".validateTips");

		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}

	function checkLength(o, n, min, max) {
		var isValid = true;
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			updateTips("Length of " + n + " must be between " + min + " and "
					+ max + " characters.");
			isValid = false;
		}
		return isValid;
	}

	function checkRegexp(o, regexp, n) {
		var isValid = true;
		if (!(regexp.test(o.val()))) {
			o.addClass("ui-state-error");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateSelectedOption(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat ceva");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;
		var name = $('[name="nameDept"]', form);

		//bValid = bValid && checkLength(name, "name", 1, 200);

		var manager = document.getElementById('bossDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(manager, "You must select a manager");

		var division = document.getElementById('divDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(division, "You must select a division");

		return bValid;
	}
});

$(function() {
	var form = $('#new_department'), allFields = $(':text', form);
	$("#add-department-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save" : function() {
				console.log("save department clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveDepartment();
					$(this).dialog("close");
				}
			},
			Cancel : function() {
				$(this).dialog("close");
				loadDepartments();
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
			loadDepartments();
		}
	});

	// create new division from the main page
	$("#add-department").button().click(function() {
		$("#add-department-dialog").dialog("open");
	});

	// Form validation
	function updateTips(t) {
		var tips = $(".validateTips");

		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}

	function checkLength(o, n, min, max) {
		var isValid = true;
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			updateTips("Length of " + n + " must be between " + min + " and "
					+ max + " characters.");
			isValid = false;
		}
		return isValid;
	}

	function checkRegexp(o, regexp, n) {
		var isValid = true;
		if (!(regexp.test(o.val()))) {
			o.addClass("ui-state-error");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateSelectedOption(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat ceva");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;
		var name = $('[name="nameDept"]', form);

		//bValid = bValid && checkLength(name, "name", 1, 200);

		var manager = document.getElementById('bossDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(manager, "You must select a manager");

		var division = document.getElementById('divDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(division, "You must select a division");

		return bValid;
	}
});

function saveEmployee(){
	var username = document.getElementById('username').value;
	var pass = document.getElementById('password').value;
	var retypePass = document.getElementById('confirmPassword').value;

	var firstName = document.getElementById('firstname').value;
	var lastName = document.getElementById('lastname').value;
	var email = document.getElementById('email').value;

	var jobDrop = document.getElementById('jobDrop');
	var job = null;

	if (jobDrop.selectedIndex != 0) {
		job = jobDrop.options[jobDrop.selectedIndex].text;
	} else {
		console.log("N-ai selectat job");
		job="";
	}

	var deptDrop = document.getElementById('empDeptDrop');
	var deptName = null;

	if (deptDrop.selectedIndex != 0) {
		deptName = deptDrop.options[deptDrop.selectedIndex].text;
	} else {
		console.log("N-ai selectat departament");
		deptName="";
	}
	$.ajax({
		type : "GET",
		url : "ManagementServlet",
		data : {
			phase : "saveNewEmployee",
			name : "" + name,
			pass : "" + pass,
			firstName : "" + firstName,
			lastName : "" + lastName,
			email : "" + email,
			job : "" + job,
			depart: "" + deptName
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}



function loadEmployees(){

	$.ajax({
		type : "GET", 
		url : "ManagementServlet",
		data : {
			phase : "loadAllEmployees"
		},
		success : function(data, textSataus, jqXHR) {
			var tsTable = $('#employees-table');
			var noEntries = $('#no-employees');
			var existingEntries = $('#exist-employees');

			$('#employees-table > tbody').empty();
			
			if (data.ok == true) {
				tsTable.show();
				noEntries.hide();
				existingEntries.show();

				for (var i = 0; i < data.size; i++) {
					// index = 0 -> selects table head so this row should be
					// skipped
					var rowIndex = i + 1;
					tsTable.append("<tr>" + 
							'<td class="edit">' + data.usernames[i] + "</td>" + 
							"<td>" + data.firstnames[i] + "</td>" + 
							"<td>" + data.lastnames[i] + "</td>" + 
							"<td>" + data.emails[i] + "</td>" + 
							"<td>" + data.jobs[i] + "</td>" + 
							"<td>" + data.departments[i] + "</td>" + 
							"<td>" + '<a href="#" onClick="editActivity(' + rowIndex
							+ ')">edit</a>' + "<td>"
							+ '<a href="#" onClick="removeActivity(' + rowIndex
							+ ')">remove</a>' + "</tr>");
				} 
			} else {
				noEntries.show();
				tsTable.hide();
				existingEntries.hide();
			}

		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});

}

function loadJobs() {
	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "loadAllJobs"
		},
		success : function(data, textSataus, jqXHR) {
			console.log(data), populateDropdown(data, "jobDrop");
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function populateDropdown(data, elementId) {
	$('#' + elementId).empty();
	var projDrop = document.getElementById(elementId);
	console.log(data.elements.length);

	for (var i = 0; i < data.elements.length; i++) {
		var elem = document.createElement("option");
		elem.textContent = data.elements[i];
		elem.value = data[i];
		projDrop.appendChild(elem);
	}
}

$(function() {
	var form = $('#new_employee'), allFields = $(':text', form);
	$("#add-employee-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save" : function() {
				console.log("save employee clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveEmployee();
					$(this).dialog("close");
				}
			},
			Cancel : function() {
				$(this).dialog("close");
				loadEmployees();
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
			loadEmployees();
		}
	});

	// create new division from the main page
	$("#add-employee").button().click(function() {
		$("#add-employee-dialog").dialog("open");
	});

	// Form validation
	function updateTips(t) {
		var tips = $(".validateTips");

		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}

	function checkLength(o, n, min, max) {
		var isValid = true;
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			updateTips("Length of " + n + " must be between " + min + " and "
					+ max + " characters.");
			isValid = false;
		}
		return isValid;
	}

	function checkRegexp(o, regexp, n) {
		var isValid = true;
		if (!(regexp.test(o.val()))) {
			o.addClass("ui-state-error");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateSelectedOption(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat ceva");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;
		var username = $('[name="username"]', form);

		//TODO:
		//bValid = bValid && checkLength(name, "name", 1, 200);

		//var manager = document.getElementById('bossDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(manager, "You must select a manager");

		//var division = document.getElementById('divDeptDrop');
		//bValid = bValid
		//    && validateSelectedOption(division, "You must select a division");

		return bValid;
	}
});