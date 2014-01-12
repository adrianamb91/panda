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
        url : "HomepageServlet",
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