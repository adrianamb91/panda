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
            if (data.ok == true) {
                populateProjectDropdown(data);
            } else {
                alert("No projects!");
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

function submitMTS(i) {
	console.log("submit mts");
	var table = $('#monthly-entries');
	var selectorValue = 'tr:eq(' + i + ')';
	var row = $(selectorValue, table);
	
	$.ajax({
		type: "GET",
		url : "HomepageServlet",
		data : {phase : "submitMTSByClerk",
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

$(document).ready(function() {

	console.log("document ready");
	mainContent = document.getElementById('view_page').innerHTML;

	getLoginDataFromServer();
	loadProjectsForUser();
	loadTodaysTimesheetForUser();
	loadAllMTimesheetsForUser();

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

    $("#main_datepicker").datepicker(
        {
            dateFormat : "dd/mm/yy",
            onSelect : function(selected, event) {
                console.log("I'm here");
                loadTodaysTimesheetForUser(selected);
            }
        });
});

function loadAllMTimesheetsForUser() {

	// delete all rows from the table
	// don't worry, they'll be brought back from the server
	$("#monthly-entries > tbody").empty();
	$.ajax({
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
				if (data.status[i] == 'OPEN' ) {
					tsTable.append("<tr>" + 
							"<td>" + data.date[i] + "</td>" +
							"<td>" + data.status[i] 
							+ '<button id="submitMTS" style="float: right" onclick="submitMTS(' + i + 1 + ')">Submit</button>' 
							+ "</td>" + 
					"</tr>");
				} else {
					tsTable.append("<tr>" + "<td>" + data.date[i] + "</td>"
							+ "<td>" + data.status[i] + "</td>" + "</tr>");
				}
			}
		}
	});
}

function removeActivity(i) {
	var table = $('#entries-table');
	var selectorValue = 'tr:eq(' + i + ')';
	var row = $(selectorValue, table);
	$.ajax({
		type: "GET", 
		url : "HomepageServlet",
		data : {phase : "removeActivity",
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


// for the moment this doesn't really work
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
	$("#dialog-form").dialog({
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
					saveActivity(row[0].cells[0].innerHTML, row[0].cells[1].innerHTML, row[0].cells[2].innerHTML, row[0].cells[3].innerHTML);
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
            date: dateWanted
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
                //maxDate: "+1d",
				onSelect : function(dateText, inst) {
					var date = $.datepicker.parseDate(inst.settings.dateFormat
							|| $.datepicker._defaults.dateFormat, dateText,
							inst.settings);
					var dateText1 = $.datepicker.formatDate("dd/mm/yy", date,
							inst.settings);
				}
			});
});

function saveActivity(olddate, oldduration, olddescription, oldProjectName) {
	
	console.log("entered save");
	
	var duration = document.getElementById('duration').value;
	var description = document.getElementById('description').value;
	var isExtra = document.getElementById('isExtra').checked;
	var projectDropdown = document.getElementById('projectDrop');
	var projectName = projectDropdown.options[projectDropdown.selectedIndex].text;

	$( "#selection_date" ).datepicker( "option", "dateFormat", "dd/mm/yy" );
	var date = $( "#selection_date" ).datepicker().val();
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
			old_duration: "" + oldduration,
			old_description: "" + olddescription,
			old_projectName: "" + oldProjectName
 		},
		success : function(data, textStatus, jqXHR) {
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
