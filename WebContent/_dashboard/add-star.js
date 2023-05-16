let addstar_form = $("#addStarForm");
function handleAddStar(resultDataString){
    console.log("handling")
    console.log(resultDataString["message"])
    $("#message").text(resultDataString["message"]);
}
function addStar(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "../api/addstar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addstar_form.serialize(),
            success: handleAddStar
        }
    );

}

addstar_form.submit(addStar);
