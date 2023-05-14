let addmovie_form = $("#addMovieForm");


function handleAddMovie(resultDataString){
    console.log("accessed handle add movie");
    if(resultDataString["message"].startsWith("SUCCESS") ){
        console.log(resultDataString["message"]);
    }else{
        console.log(resultDataString["message"]);
    }
}

function addMovie(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/addmovie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addmovie_form.serialize(),
            success: handleAddMovie
        }
    );

}
addmovie_form.submit(addMovie);
