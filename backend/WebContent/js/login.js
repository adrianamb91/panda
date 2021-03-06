var serverIp = "localhost:8080";

function sendLoginInfoToServer(un, pswd) {
	$.ajax({
		type : "POST",
		url : "Login",
		data : {
			username : "" + un,
			password : "" + pswd
		},
		success : function(data, textStatus, jqXHR) {
			console.log(data);
			if (data.access === 'granted') {
				if (data.job == 'CLERK') {
					window.open('http://' + serverIp
							+ '/TimesheetManagement/homepageEmployee.html',
							'_self', false);
				} else if (data.job == 'ADMIN') {
					window.open('http://' + serverIp
							+ '/TimesheetManagement/homepageAdmin.html',
							'_self', false);
				} else if (data.job == 'DEPT_MANAGER') {
					window.open('http://' + serverIp
							+ '/TimesheetManagement/homepageDeptManager.html',
							'_self', false);
				} else if (data.job == 'DIVISION_MANAGER') {
					window.open('http://' + serverIp
							+ '/TimesheetManagement/homepageDivManager.html',
							'_self', false);
				} else if (data.job == 'CEO') {
                    window.open('http://' + serverIp
                        + '/TimesheetManagement/homepageCEO.html',
                        '_self', false);
                }
			} 
			else if (data.access === 'wrong_password'){
				$('#errorMessage').text("Invalid password");
				$('#errorMessage').show();
				$('#password').text('');
			}
			else if (data.access === 'wrong_user') {
				$('#errorMessage').text("Invalid username");
				$('#errorMessage').show();
				$('#username').text('');
			}
		},
		error : function() {
			alert("faaaaail!");
			console.log('There is an error');
		}
	});
}

$(document).ready(function() {
    console.log("ready");

    $("#password").keyup(function(event){
        if(event.keyCode == 13){
            $("#sign-in-button").click();
        }
    });
	$('#sign-in-button').click(function() {
		var username = document.getElementById('username').value;
		var password = document.getElementById('password').value;

		console.log ("" + username + " " + password);
		sendLoginInfoToServer(username, password);
	});
	
	setFocusesForElement('username', 'username');
	setFocusesForElement('password', 'password');

});

function setFocusesForElement(element, elementPHolder) {
	$("#" + element).focusin(function() {
		$(this).attr("placeholder", '');
	});

	$("#" + element).focusout(function() {
		$(this).attr("placeholder", '' + elementPHolder);
	});
}

