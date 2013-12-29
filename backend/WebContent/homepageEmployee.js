var serverIp = "localhost:8080";
var mainContent;
var loginData;
var globalDate;

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
	})
}

function changePassword() {

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
			console.log(data);
			populateProjectDropdown(data);
		},
		error : function() {
			alert("failure");
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

$(document).ready(function() {
	
	console.log ("document ready");
	mainContent = document.getElementById('view_page').innerHTML;

	getLoginDataFromServer();
	loadProjectsForUser();
	loadTodaysTimesheetForUser();
	//loadAllMTimesheetsForUser();

	$('.editable').editable('http://www.example.com/save.php', {
        indicator : 'Saving...',
        tooltip   : 'Click to edit...'
    });
	
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

function loadAllMTimesheetsForUser() {
	
	// delete all rows from the table
	// don't worry, they'll be brought back from the server
	$("#monthly-entries > tbody").empty();
	
	
	$.ajax({
		type: "GET",
		url: "HomepageServlet",
		data: {phase : "loadAllMTimesheets"},
		success: function (data, textStatus, jqXHR) {
			console.log(data);
			var tsTable = $('#monthly-entries');

			for (var i = 0; i < data.size; i ++) {
				tsTable.append(
						"<tr>" +
						"<td>" + data.date[i] + "</td>" +
						"<td>" + data.status[i] + "</td>" +
						"</tr>"
				);
			}
		}
	});
}

function loadTodaysTimesheetForUser() {
	
	// delete all rows from the table
	// don't worry, they'll be brought back from the server
	$("#entries > tbody").empty();
	
	
	$.ajax({
		type: "GET",
		url: "HomepageServlet",
		data: {phase : "loadTodaysTimesheet"},
		success: function (data, textStatus, jqXHR) {
			console.log(data);
			var tsTable = $('#entries');

			for (var i = 0; i < data.size; i ++) {
				tsTable.append(
						"<tr>" +
						'<td class="edit">' + data.date + "</td>" +
						"<td>" + data.duration[i]+ "</td>" +
						"<td>" + data.description[i] + "</td>" +
						"<td>" + data.project[i] + "</td>" +
						"</tr>"
				);
			}
		}
	});
}

// form function
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
			"Create new entry" : function() {
				allFields.removeClass("ui-state-error");
				// console.log(validateFields(form));
				 if (validateFields(form)) {
					// addToUsers(form);
					console.log("did it!");
					saveActivity();
				}
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		},
		close : function() {
			allFields.val("").removeClass("ui-state-error");
			loadTodaysTimesheetForUser();
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

		var project = document.getElementById('projectDrop');
		bValid = bValid
				&& validateSelectedProject(project, "You must select a project");

		return bValid;
	}
});

$(function() {
	$(".datepicker").datepicker(
			{
				dateFormat : "dd/mm/yy",
				onSelect : function(dateText, inst) {
					var date = $.datepicker.parseDate(inst.settings.dateFormat
							|| $.datepicker._defaults.dateFormat, dateText,
							inst.settings);
					var dateText1 = $.datepicker.formatDate("dd/mm/yy", date,
							inst.settings);
				}
			});
});

function saveActivity() {
	var duration = document.getElementById('duration').value;
	var description = document.getElementById('description').value;
	var isExtra = document.getElementById('isExtra').checked;
	var projectDropdown = document.getElementById('projectDrop');
	var projectName = projectDropdown.options[projectDropdown.selectedIndex].text;

	$.ajax({
		type : "GET",
		url : "HomepageServlet",
		data : {
			phase : "saveActivity",
			duration : "" + duration,
			description : "" + description,
			isExtra : "" + isExtra,
			date : "" + globalDate,
			project : "" + projectName
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			if (data.ok == true) {
				alert("saved!");
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}
