let addstar_form = $("#addStarForm");
function handleAddStar(resultDataString){
    console.log("accessed handle add star");
    if(resultDataString["message"].startsWith("SUCCESS") ){
        console.log(resultDataString["message"]);
    }else{
        console.log(resultDataString["message"]);
    }
}
function addStar(formSubmitEvent) {
    console.log("entered addstar");
    formSubmitEvent.preventDefault();
    $.ajax(
        "_dashboard/api/addstar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addstar_form.serialize(),
            success: handleAddStar
        }
    );

}

addstar_form.submit(addStar);
