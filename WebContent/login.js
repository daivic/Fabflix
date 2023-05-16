let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to movielist.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("search.jsp");
    }
    else if(resultDataJson["status"] === "ReCaptchaFail"){
        console.log("recaptcha doesn't work");
        $("#recaptcha_error_message").text(resultDataJson["message"]);
    }
    else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);

