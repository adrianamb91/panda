/**
 * Created by Adri on 1/11/14.
 */

var serverIp = "localhost:8080";
var mainContent;
var loginData;
var globalDate;

$(document).ready(function() {

	console.log("document ready");
	mainContent = document.getElementById('view_page').innerHTML;

	getLoginDataFromServer();
	loadProjectsForUser();
	loadTodaysTimesheetForUser();
	loadAllMTimesheetsForUser();

	loadEmployees();

	getDepartment();
	loadClients();
	loadProjects();

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

	$("#main_datepicker").datepicker({
		dateFormat : "dd/mm/yy",
		onSelect : function(selected, event) {
			loadTodaysTimesheetForUser(selected);
		}
	});
});

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
			alert("failure: get login data from server");
			console.log('There is an error in getting login data from server');
		}
	});
}

function getDepartment() {
	console.log("getDepartment");
	$.ajax({
		type : "GET",
		url : "ClientProjectServlet",
		data : {
			phase : "getDepartment"
		},
		success : function(data, textStatus, jqXHR) {
			if (data.ok == true) {
				var dname = data.deptName;
				$('#departmentName').val(dname);
				$('#add-project').removeAttr('disabled');
			} else {
				$('#departmentName').val("???");
				$('#add-project').attr('disabled', 'disabled');
			}
		},
		error : function() {
			alert("failure: get login data from server");
			console.log('There is an error in getting login data from server');
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
			alert("failure: end session");
			console.log('There is an error');
		}
	});
}

function loadEmployees() {
	$.ajax({
		type : "GET",
		url : "DepartmentManagerServlet",
		data : {
			phase : "loadEmployees"
		},
		success : function(data, textStatus, jqXHR) {

			console.log("ALL CLERKS: ");
			console.log(data);
			if (data.ok == true) {
				populateDropdown(data, "clerkDrop");
				populateDropdown(data, "clerkDrop_report");
			} else {
				alert("No employees");
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function changePassword() {

}

function viewMTS() {

//	var projectDropdown = document.getElementById('projectDrop');
//	var projectName = projectDropdown.options[projectDropdown.selectedIndex].text;
//	var empname = $('#clerkDrop').options[$('#clerkDrop').selectedIndex].text;

	$('#last-mts-status-div').html("");

	var enameDrop = document.getElementById('clerkDrop');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	console.log("requesting MTS for " + empname);

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "reviewLastMTS",
			name: empname},
			success: function (data, textStatus, jqXHR) {
				console.log("REVIEW MTS: ");
				console.log(data);
				populateAllActivitiesTable(data);
			},
			error: function() {
				alert("general failure!");
			}
	});
}

function loadProjectsForUser() {
	console.log("loadProjectsForUser");
	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "loadProjectsForCurrentUser"
		},
		success : function(data, textStatus, jqXHR) {
			console.log("PROJECTS FOR USER: ");
			console.log(data);
			if (data.ok == true) {
				populateProjectDropdown(data);
				$('#add-entry').removeAttr('disabled');
			} else {
				alert("No projects!");
				$('#add-entry').attr('disabled', 'disabled');
			}

		},
		error : function() {
			alert("failure: load projects for user");
			console.log('There is an error');
		}
	});
}

function populateProjectDropdown(data) {
	var projDrop = document.getElementById("projectDrop");

	for (var i = 0; i < data.projects.length; i++) {
		var elem = document.createElement("option");
		elem.textContent = data.projects[i];
		elem.value = data[i];
		projDrop.appendChild(elem);
	}
}

function loadAllMTimesheetsForUser() {

	// delete all rows from the table
	// don't worry, they'll be brought back from the server
	$("#monthly-entries > tbody").empty();
	$
	.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "loadAllMTimesheets"
		},
		success : function(data, textStatus, jqXHR) {
			console.log("monthly");
			console.log(data);
			var tsTable = $('#monthly-entries');

			for (var i = 0; i < data.size; i++) {
				if (data.status[i] == 'OPEN') {
					console.log("dat2: " + data.date[i]);
					tsTable
					.append("<tr>"
							+ "<td>"
							+ data.date[i]
							+ "</td>"
							+ "<td>"
							+ '<a href="# onclick="editMTSstatus('
							+ i
							+ 1
							+ ')>'
							+ data.status[i]
							+ '</a> <button id="submitMTS" style="float: right" onclick="submitMTS('
							+ i + 1 + ')">Submit</button>'
							+ "</td>" + "</tr>");
				} else {
					tsTable.append("<tr>" + "<td>" + data.date[i]
					+ "</td>" + "<td>"
					+ '<a href="# onclick="editMTSstatus(' + i
					+ 1 + ')>' + data.status[i] + '</a>'
					+ "</td>" + "</tr>");
				}

			}
		}
	});
}

function submitMTS(i) {
	var table = $('#monthly-entries');
	var selectorValue = 'tr:eq(' + i + ')';
	var row = $(selectorValue, table);

	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "submitMTSByDeptM",
			date : "" + row[0].cells[0].innerHTML,
		},
		success : function(data, textStatus, jqXHR) {
			alert('s-a dus!');
			if (data.ok == true) {
				loadAllMTimesheetsForUser();
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function removeActivity(i) {
	var table = $('#entries-table');
	var selectorValue = 'tr:eq(' + i + ')';
	var row = $(selectorValue, table);
	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "removeActivity",
			date : (row[0].cells[0]).innerHTML,
			duration : (row[0].cells[1]).innerHTML,
			description : (row[0].cells[2]).innerHTML,
			project : (row[0].cells[3]).innerHTML
		},
		success : function(data, textStatus, jqXHR) {
			alert('s-a dus!');
			loadTodaysTimesheetForUser();
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});

	return false;
}

//for the moment this doesn't really work
function editActivity(i) {

	var table = $('#entries-table');
	var selectorValue = 'tr:eq(' + i + ')';
	var row = $(selectorValue, table);

	var dialogBox = $("#dialog-form");
	dialogBox.dialog("open");

	// to be continued upon Adri's (the boss) request
	$('#selection_date').attr('value', '' + row[0].cells[0].innerHTML);
	$('#duration').attr('value', '' + row[0].cells[1].innerHTML);
	$('#description').attr('value', '' + row[0].cells[2].innerHTML);

	var form = $('#new_entry'), allFields = $(':text', form);
	$("#dialog-form").dialog(
			{
				autoOpen : false,
				height : 510,
				width : 350,
				modal : true,
				draggable : false,
				resizable : false,
				buttons : {
					"Save_2" : function() {
						console.log("save_1 clicked!");
						allFields.removeClass("ui-state-error");
						// console.log(validateFields(form));
						// addToUsers(form);
						console.log("save_2 called!!");
						saveActivity(row[0].cells[0].innerHTML,
								row[0].cells[1].innerHTML,
								row[0].cells[2].innerHTML,
								row[0].cells[3].innerHTML);
						$(this).dialog("close");
					},
					Cancel : function() {
						$(this).dialog("close");
						loadTodaysTimesheetForUser();
					}
				},
				close : function() {
					allFields.val("").removeClass("ui-state-error");
					loadTodaysTimesheetForUser();
				}
			});

	return false;
}

function loadTodaysTimesheetForUser(dateWanted) {

	// delete all rows from the table
	// don't worry, they'll be brought back from the server
	$('#entries-table > tbody').empty();

	console.log('s-a facut un clean!');

	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "loadTodaysTimesheet",
			date : dateWanted
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			var tsTable = $('#entries-table');
			var noEntries = $('#no-entries');
			var existingEntries = $('#exist-entries');

			if (data.ok == true) {
				tsTable.show();
				noEntries.hide();
				existingEntries.show();
				for (var i = 0; i < data.size; i++) {
					// index = 0 -> selects table head so this row should be
					// skipped
					var rowIndex = i + 1;
					tsTable.append("<tr>" + '<td class="edit">' + data.date
							+ "</td>" + "<td>" + data.duration[i] + "</td>"
							+ "<td>" + data.description[i] + "</td>" + "<td>"
							+ data.project[i] + "</td>" + "<td>"
							+ '<a href="#" onClick="editActivity(' + rowIndex
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
	});
}

function saveActivity(olddate, oldduration, olddescription, oldProjectName) {

	console.log("entered save");

	var duration = document.getElementById('duration').value;
	var description = document.getElementById('description').value;
	var isExtra = document.getElementById('isExtra').checked;
	var projectDropdown = document.getElementById('projectDrop');
	var projectName = projectDropdown.options[projectDropdown.selectedIndex].text;

	$("#selection_date").datepicker("option", "dateFormat", "dd/mm/yy");
	var date = $("#selection_date").datepicker().val();
	console.log(date);

	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "saveActivity",
			duration : "" + duration,
			description : "" + description,
			isExtra : "" + isExtra,
			date : "" + date,
			project : "" + projectName,
			old_date : "" + olddate,
			old_duration : "" + oldduration,
			old_description : "" + olddescription,
			old_projectName : "" + oldProjectName
		},
		success : function(data, textStatus, jqXHR) {
			console.log("SAVE ACTIVITY:");
			console.log(data);
			if (data.ok == true) {

				// replace with simple ok modal.
				alert("saved!");
				loadTodaysTimesheetForUser(date);
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

//De aici incepe managementul clientilor si al proiectelor

function saveClient() {
	var name = document.getElementById('clientName').value;

	$.ajax({
		type : "GET",
		url : "ClientProjectServlet",
		data : {
			phase : "saveClient",
			name : "" + name,
		},
		success : function(data, textStatus, jqXHR) {
			console.log("CLIENT SAVE RESP: ");
			console.log(data);
			if (data.ok == true) {
				alert("saved");
				loadClients();
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});

}

function populateClientsTable(data) {
	var tsTable = $('#clients-table');
	var noEntries = $('#no-clients');
	var existingEntries = $('#exist-clients');

	if (data.ok == true) {
		$('#clients-table > tbody').empty();
		tsTable.show();
		noEntries.hide();
		existingEntries.show();

		for (var i = 0; i < data.size; i++) {
			var rowIndex = i + 1;
			tsTable.append("<tr>" + '<td class="edit">' + data.elements[i]
			+ "</td>" + "</tr>");
		}
	} else {
		noEntries.show();
		tsTable.hide();
		existingEntries.hide();
	}
}

function loadClients() {
	$.ajax({
		type : "GET",
		url : "ClientProjectServlet",
		data : {
			phase : "loadAllClients"
		},
		success : function(data, textStatus, jqXHR) {
			console.log("LOAD ALL CLIENTS: ");
			console.log(data);
			if (data.ok == true) {
				populateClientsTable(data);
				populateDropdown(data, "clientDrop")
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function saveProject() {
	var departmentName = document.getElementById('departmentName').value;
	var projectName = document.getElementById('projectName').value;

	var clientNameDrop = document.getElementById('clientDrop');
	var clientName = null;

	clientName = clientNameDrop.options[clientNameDrop.selectedIndex].text;

	console.log("manager name: |" + clientName + "|");

	$.ajax({
		type : "GET",
		url : "ClientProjectServlet",
		data : {
			phase : "saveProject",
			client : "" + clientName,
			project : "" + projectName,
			department : "" + departmentName
		},
		success : function(data, textStatus, jqXHR) {
			console.log("PROJECT SAVE: ");
			console.log(data);
			loadProjects();
			loadProjectsForUser();
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});

}

function loadProjects() {
	$.ajax({
		type : "GET",
		url : "ClientProjectServlet",
		data : {
			phase : "loadAllProjects"
		},
		success : function(data, textStatus, jqXHR) {
			console.log("LOAD ALL PROJECTS RESP:");
			console.log(data);
			if (data.ok == true) {
				populateProjectsTable(data);
				populateDropdown(data, 'projectDrop');
				populateDropdown(data, 'project_drop_report');
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function populateProjectsTable(data) {
	var tsTable = $('#projects-table');
	var noEntries = $('#no-projects');
	var existingEntries = $('#exist-projects');

	if (data.ok == true) {
		$('#projects-table > tbody').empty();
		tsTable.show();
		noEntries.hide();
		existingEntries.show();

		for (var i = 0; i < data.size; i++) {
			var rowIndex = i + 1;
			tsTable.append("<tr>" + '<td class="edit">' + data.elements[i]
			+ "</td>" + "<td>" + data.clients[i] + "</td>" + "</tr>");
		}
	} else {
		noEntries.show();
		tsTable.hide();
		existingEntries.hide();
	}
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
	var form = $('#new_client'), allFields = $(':text', form);
	$("#add-client-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save" : function() {
				console.log("save client clicked!");
				allFields.removeClass("ui-state-error");
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveClient();
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
	$("#add-client").button().click(function() {
		$("#add-client-dialog").dialog("open");
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

	function validateFields(form) {
		var bValid = true;
		var name = $('[name="clientName"]', form);

		bValid = bValid && checkLength(name, "name", 1, 200);

		return bValid;
	}
});

//form for entries in daily timesheet
$(function() {
	var form = $('#new_project'), allFields = $(':text', form);
	$("#add-project-dialog").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save_1" : function() {
				console.log("new project save clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveProject();
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

	// create new entry from the main page
	$("#add-project").button().click(function() {
		$("#add-project-dialog").dialog("open");
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

	function validateSelectedProject(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat proiect");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;

		return bValid;
	}
});

$(function() {
	$(".datepicker").datepicker(
			{
				dateFormat : "dd/mm/yy",
				// maxDate: "+1d",
				onSelect : function(dateText, inst) {
					var date = $.datepicker.parseDate(inst.settings.dateFormat
							|| $.datepicker._defaults.dateFormat, dateText,
							inst.settings);
					var dateText1 = $.datepicker.formatDate("dd/mm/yy", date,
							inst.settings);
				}
			});
});

//form function
$(function() {
	var form = $('#new_entry'), allFields = $(':text', form);
	$("#dialog-form").dialog({
		autoOpen : false,
		height : 510,
		width : 350,
		modal : true,
		draggable : false,
		resizable : false,
		buttons : {
			"Save_1" : function() {
				console.log("save_1 clicked!");
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveActivity();
					$(this).dialog("close");
				}

			},
			Cancel : function() {
				$(this).dialog("close");
				loadTodaysTimesheetForUser(globalDate);
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
			loadTodaysTimesheetForUser(globalDate);
		}
	});

	// create new entry from the main page
	$("#add-entry").button().click(function() {
		$("#dialog-form").dialog("open");
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

	function validateSelectedProject(o, n) {
		var isValid = true;
		console.log(o.selectedIndex);
		if (o.selectedIndex == 0) {
			console.log("N-ai selectat proiect");
			updateTips(n);
			isValid = false;
		}
		return isValid;
	}

	function validateFields(form) {
		var bValid = true;
		var date = $('[name="selection_date"]', form);
		var duration = $('[name="duration"]', form);
		var description = $('[name="description"]', form);

		bValid = bValid && checkLength(description, "description", 1, 250);
		bValid = bValid
		&& checkRegexp(duration, /^([0-9.])+$/i,
		"Duration should be expressed in number of hours");

		//var project = document.getElementById('projectDrop');
		//bValid = bValid
		//		&& validateSelectedProject(project, "You must select a project");

		return bValid;
	}
});


function populateAllActivitiesTable(data) {

	console.log("populateAllActivitiesTable:");

	var table = $('#all-entries-table');
	$('#all-entries-table > tbody').empty();
	console.log("ALL ACTIVITIES: ");
	console.log(data);
	if (data.ok == true) {
		for (var i = 0; i < data.size; i ++) {
			console.log("intra aici");
			table.append("<tr>" + 
					'<td>' + data.date[i] + "</td>" + 
					"<td>" + data.duration[i] + "</td>" + 
					"<td>" + data.description[i] + "</td>" + 
					"<td>" + data.project[i] + "</td>" + 
			"</tr>");
		}
	}	
	return false;
}


function reviewWorkFromEmployeeInInterval() {

	var from = $('#selection_date_from').val();
	var to = $('#selection_date_to').val();

	var enameDrop = document.getElementById('clerkDrop_report');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	console.log("to: " + to + " from: " + from);

	//generatePieChart();

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "reviewMTSforUserInInterval",
			name: "" + empname,
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("REVIEW MTS INTERVAL: ");
			console.log(data);
			populateIntervalActivitiesTable(data);
			generatePieChart(data.chartDataJSON, 'chart1');
		},
		error: function() {
			alert("general failure!");
		}
	});
}

function populateIntervalActivitiesTable(data) {

	console.log("populateIntervalActivitiesTable:");

	var table = $('#interval-entries-table');
	$('#interval-entries-table > tbody').empty();
	console.log("DETAILED MTS: ");
	console.log(data);
	if (data.ok == true) {
		for (var i = 0; i < data.size; i ++) {
			table.append("<tr>" + 
					'<td>' + data.date[i] + "</td>" + 
					"<td>" + data.duration[i] + "</td>" + 
					"<td>" + data.description[i] + "</td>" + 
					"<td>" + data.project[i] + "</td>" + 
			"</tr>");
		}
	}	
	return false;
}

function generatePieChart(entries, elementId) {

	$('#' + elementId).show();
	console.log("PLOT: ");
	console.log(entries);

	plot2 = jQuery.jqplot(elementId, entries,
			{
		title : ' ',
		seriesDefaults : {
			shadow : true,
			renderer : jQuery.jqplot.PieRenderer,
			rendererOptions : {
				startAngle : 180,
				sliceMargin : 4,
				showDataLabels : true
			}
		},
		legend : {
			show : true,
			location : 'w'
		}
			});
}

function reviewSummaryWorkFromEmployeeInInterval() {

	var from = $('#selection_date_from').val();
	var to = $('#selection_date_to').val();

	var enameDrop = document.getElementById('clerkDrop_report');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "reviewSummaryWorkFromEmployeeInInterval",
			name: "" + empname,
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("SUMMARY WORK: ");
			console.log(data);
			if (data.ok == true) {
				populateSummaryWorkTable(data);
			}
		},
		error: function() {
			alert("general failure!");
		}
	});
}

function populateSummaryWorkTable(data) {
	//summary_interval-entries-table

	var table = $('#summary_interval-entries-table');
	$('#summary_interval-entries-table > tbody').empty();

	console.log("SUMMARY DATA:");
	console.log(data);
	if (data.ok == true) {
		for (var i = 0; i < data.size; i ++) {
			table.append("<tr>" + 
					"<td>" + data.project[i] + "</td>" + 
					"<td>" + data.duration[i] + "</td>" + 
			"</tr>");
		}
	}	
	return false;
}



function reviewWorkForProjectInInterval() {

	var from = $('#selection_date_from_proj').val();
	var to = $('#selection_date_to_proj').val();

	var projDrop = document.getElementById('project_drop_report');
	var projname = projDrop.options[projDrop.selectedIndex].text;

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "reviewSummaryWorkForProjectInInterval",
			name: "" + projname,
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("PROJECT SUMMARY WORK: ");
			console.log(data);
			populateProjectSummaryWorkTable(data);
			generatePieChart(data.chartDataJSON, 'chart2');
		},
		error: function() {
			alert("general failure!");
		}
	});

}

function populateProjectSummaryWorkTable(data) {
	var table = $('#summary_interval-project-table');
	$('#summary_interval-project-table > tbody').empty();

	console.log("PROJECT SUMMARY DATA:");
	console.log(data);
	if (data.ok == true) {
		for (var i = 0; i < data.size; i ++) {
			table.append("<tr>" + 
					"<td>" + data.owners[i] + "</td>" + 
					"<td>" + data.durations[i] + "</td>" + 
			"</tr>");
		}
	}	
	return false;
}

function reviewWorkInDepartmentInInterval() {

	var from = $('#selection_date_from_dept').val();
	var to = $('#selection_date_to_dept').val();

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "reviewWorkInDepartmentInInterval",
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("DEPARTMENT SUMMARY WORK: ");
			console.log(data);
			populateDepartmentSummaryWorkTable(data);
			generatePieChart(data.chartDataJSON, 'chart3');

		},
		error: function() {
			alert("general failure!");
		}
	});

}

function populateDepartmentSummaryWorkTable(data) {
	//summary_interval-dept-table

	var table = $('#summary_interval-dept-table');
	$('#summary_interval-dept-table > tbody').empty();

	console.log("DEPARTMENT SUMMARY DATA:");
	console.log(data);
	if (data.ok == true) {
		for (var i = 0; i < data.size; i ++) {
			table.append("<tr>" + 
					"<td>" + data.projects[i] + "</td>" + 
					"<td>" + data.durations[i] + "</td>" + 
			"</tr>");
		}
	}	
	return false;
}

function exportProjectSummaryToPDF() {

	var from = $('#selection_date_from_proj').val();
	var to = $('#selection_date_to_proj').val();

	var projDrop = document.getElementById('project_drop_report');
	var projname = projDrop.options[projDrop.selectedIndex].text;

	if (from == '' || to == '' || projname == '') {
		alert("Nu ai selectat datele necesare!");
		return false;
	}

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "exportProjectSummaryToPDF",
			from: "" + from, 
			to: "" + to,
			name: "" + projname
		},
		success: function (data, textStatus, jqXHR) {
			console.log(data);
			if(data.ok == true) {
				alert("Raportul a fost generat cu success in format pdf");
			}
		},
		error: function() {
			alert("general failure!");
		}
	});
}

function exportWorkFromEmployeeInInterval() {
	var from = $('#selection_date_from').val();
	var to = $('#selection_date_to').val();

	var enameDrop = document.getElementById('clerkDrop_report');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	if (from == '' || to == '' || empname == '') {
		alert ("Nu ai selectat datele necesare");
		return false;
	} 

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "exportMTSforUserInIntervaltoPDF",
			name: "" + empname,
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("REVIEW MTS INTERVAL: ");
			console.log(data);
			if (data.ok == true) {
				alert ("Fisierul a fost salvat cu succes!");
			}

		},
		error: function() {
			alert("general failure!");
		}
	});
}

function exportWorkInDepartmentInIntervalPDF() {
	var from = $('#selection_date_from_dept').val();
	var to = $('#selection_date_to_dept').val();

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "exportWorkInDepartmentInInterval",
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("DEPARTMENT SUMMARY WORK: ");
			console.log(data);
			if (data.ok == true) {
				alert ("Fisierul a fost generat cu success");
			}

		},
		error: function() {
			alert("general failure!");
		}
	});
}

function exportMTStoXLS() {
	var enameDrop = document.getElementById('clerkDrop');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;
	$.ajax({
		type : "GET",
		url : "DepartmentManagerServlet",
		data : {
			phase : "exportXLSwithReviewLastMTS",
			name : empname
		},
		success : function(data, textStatus, jqXHR) {
			console.log("export XLS: ");
			console.log(data);
			if (data.ok == true) {
				alert("Fisierul xls a fost generat cu succes!");
			}
		},
		error : function() {
			alert("general failure!");
		}
	});
}

function exportWorkInDepartmentInIntervalXLS() {
	var from = $('#selection_date_from_dept').val();
	var to = $('#selection_date_to_dept').val();

	$.ajax({
		type: "GET", 
		url: "DepartmentManagerServlet",
		data: {phase: "exportWorkInDepartmentInIntervalXLS",
			from: "" + from, 
			to: "" + to
		},
		success: function (data, textStatus, jqXHR) {
			console.log("DEPARTMENT SUMMARY WORK: ");
			console.log(data);
			if (data.ok == true) {
				alert ("Fisierul xls a fost generat cu success");
			}
		},
		error: function() {
			alert("general failure!");
		}
	});
}

function viewSubmittedMTS() {
	var enameDrop = document.getElementById('clerkDrop');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	console.log("requesting MTS for " + empname);

	$.ajax({
		type: "GET",
		url: "DepartmentManagerServlet",
		data: {phase: "reviewLastSubmittedMTS",
			name: empname},
			success: function (data, textStatus, jqXHR) {
				console.log("REVIEW MTS: ");
				console.log(data);

				if (data.ok == true) {
					if (data.status == 'SUBMITTED') {
						var dateString = data.mtsDate + "";
						console.log("Date string: ");
						console.log(dateString);
						$('#last-mts-status-div').html("Status: " + data.status  + 
								' <a href="#" onClick="approveMTS('
								+ ')">APPROVE</a>' + 
								' <a href="#" onClick="rejectMTS(' 
								+ ')">REJECT</a>');
					}
					else {
						$('#last-mts-status-div').html("Status: " + data.status);
					}
					populateAllActivitiesTable(data);
				} else {
					$('#all-entries-table > tbody').empty();
					$('#last-mts-status-div').html("");
				}
			},
			error: function() {
				alert("general failure!");
			}
	});
}

function approveMTS() {
	console.log("Vreau sa approve");
	var enameDrop = document.getElementById('clerkDrop');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	$.ajax({
		type: "GET",
		url: "DepartmentManagerServlet",
		data: {phase: "changeMTSStatus",
			emp: empname,
			newstatus: "APPROVED"},
			success: function (data, textStatus, jqXHR) {
				console.log(data);
				if (data.ok == true) {
					alert("Approved!");
				} else {
					alert("Failure!");
				}
			},
			error: function() {
				alert("general failure!");
			}
	});
}

function rejectMTS(mtsDate) {
	console.log("Vreau sa reject");
	console.log("Vreau sa approve");
	var enameDrop = document.getElementById('clerkDrop');
	var empname = enameDrop.options[enameDrop.selectedIndex].text;

	$.ajax({
		type: "GET",
		url: "DepartmentManagerServlet",
		data: {phase: "changeMTSStatus",
			emp: empname,
			newstatus: "REJECTED"},
			success: function (data, textStatus, jqXHR) {
				console.log(data);
				if (data.ok == true) {
					alert("Rejected!");
				} else {
					alert("Failure!");
				}
			},
			error: function() {
				alert("general failure!");
			}
	});
}


