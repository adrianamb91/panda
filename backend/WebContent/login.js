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
			console.log("LOGIN : request submitted successfully");
			console.log("LOGIN : Response: " + data);
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
				}
			} else {
				console.log("LOGIN : We should print error message");
				var errorMsg = document.getElementById('error_message');
				errorMsg.style.color = 'Red';
				errorMsg.style.fontSize = "large";
				errorMsg.innerText = "Username/password are no good!";
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

