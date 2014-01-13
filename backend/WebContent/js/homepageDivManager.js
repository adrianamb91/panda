/**
 * Created by Adri on 1/12/14.
 */

var serverIp = "localhost:8080";
var mainContent;
var loginData;
var globalDate;

$(document).ready(function() {

    console.log("document ready");
    mainContent = document.getElementById('view_page').innerHTML;

    getLoginDataFromServer();

    loadTodaysTimesheetForUser();
    loadAllMTimesheetsForUser();

    loadEmployees();
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

// Initializari: START
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
    });
}

//Initializari: STOP

// Own daily timesheet: stuff
function saveActivity(olddate, oldduration, olddescription, oldProjectName) {

    console.log("entered save");

    var duration = document.getElementById('duration').value;
    var description = document.getElementById('description').value;
    var isExtra = document.getElementById('isExtra').checked;

    $("#selection_date").datepicker("option", "dateFormat", "dd/mm/yy");
    var date = $("#selection_date").datepicker().val();
    console.log(date);

    $.ajax({
        type : "GET",
        url : "DivManagerServlet",
        data : {
            phase : "saveActivity",
            duration : "" + duration,
            description : "" + description,
            isExtra : "" + isExtra,
            date : "" + date,
            old_date : "" + olddate,
            old_duration : "" + oldduration,
            old_description : "" + olddescription,
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

function loadTodaysTimesheetForUser(dateWanted) {

    // delete all rows from the table
    // don't worry, they'll be brought back from the server
    $('#entries-table > tbody').empty();

    console.log('s-a facut un clean!');

    $.ajax({
        type : "GET",
        url : "DivManagerServlet",
        data : {
            phase : "loadDTS",
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

    function validateFields(form) {
        var bValid = true;
        var date = $('[name="selection_date"]', form);
        var duration = $('[name="duration"]', form);
        var description = $('[name="description"]', form);

        bValid = bValid && checkLength(description, "description", 1, 250);
        bValid = bValid
            && checkRegexp(duration, /^([0-9.])+$/i,
            "Duration should be expressed in number of hours");


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
// Chestii de DTS: end!

// Chestii de own MTS: Start
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
                if (data.status[i] == 'OPEN') {
                    console.log("dat2: " + data.date[i]);
                    tsTable.append("<tr>" + "<td>"
                        + data.date[i] + "</td>"
                        + "<td>" + '<a href="# onclick="editMTSstatus(' + i + 1 + ')>'
                        + data.status[i]
                        + '</a> <button id="submitMTS" style="float: right" onclick="submitMTS('
                        + i + 1 + ')">Submit</button>' + "</td>" + "</tr>");
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
            date : "" + row[0].cells[0].innerHTML
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

//Chestii legate de toti employees din divizie
function loadEmployees() {
    $.ajax({
        type : "GET",
        url : "DivManagerServlet",
        data : {
            phase : "loadEmployees"
        },
        success : function(data, textStatus, jqXHR) {
            console.log("ALL CLERKS: ");
            console.log(data);
            if (data.ok == true) {
                populateDropdown(data, 'clerkDrop');
                populateDropdown(data, 'clerkDrop_report');
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

function loadProjects() {
	$.ajax({
		type : "GET",
		url : "DivManagerServlet",
		data : {
			phase : "loadAllProjectsFromDivision"
		},
		success : function(data, textStatus, jqXHR) {
			console.log("LOAD ALL PROJECTS RESP:");
			console.log(data);
			if (data.ok == true) {
				//populateProjectsTable(data);
				//populateDropdown(data, 'projectDrop');
				populateDropdown(data, 'project_drop_report');
			}
		},
		error : function() {
			alert("failure");
			console.log('There is an error');
		}
	});
}

function viewMTS() {
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
//End of chestii de legate de toti employees din divizie

//Chestii generale
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
//End of chestii generale

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
				if (data.size == 0) {
					alert("Angajatul selectat nu are nici o activitate");
					return false;
				}
				else {
					populateSummaryWorkTable(data);
				}
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
	//summary_interval-entries-table
	
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