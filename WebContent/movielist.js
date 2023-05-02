/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */




/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
let currentPage = 0;
let results = 10;
let order = "m.title ASC, r.rating ASC";
let total_count = 0;
try {
    results = document.getElementById('results').value;

}catch (error){
}
try{
    order = document.getElementById('order').value;

}catch (error){
}
let movieTitle = getParameterByName('title');
let movieDir = getParameterByName('director');
let movieStar = getParameterByName('star');
let movieYear = getParameterByName('year');
let movieGenre = getParameterByName('genre');
let movieAlphaNum = getParameterByName('first');

renderList();


function addToCart(title){
    $.ajax("api/index", {
        method: "POST",
        data: "title="+title,

    });
    alert("Successfully Added " + title);
}
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");
    starTableBodyElement.empty();

    total_count = resultData.length;

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_dir"] + "</td>";

        rowHTML += "<td>";

        const genres = resultData[i]["movie_genres"].split(",");
        const genreIDs = resultData[i]["genre_ids"].split(",");
        for (let i = 0; i < genres.length; i++) {
            rowHTML += '<a href="movielist.html?genre=' + genreIDs[i] + '">' + genres[i] + "," +'</a>' ;
        }
        //rowHTML += "<td>" + resultData[i]["movie_genres"] + "</td>";
        rowHTML +=  "</td>";

        rowHTML += "<td>";

        const stars = resultData[i]["movie_stars"].split(",");
        const ids = resultData[i]["star_ids"].split(",");

        for (let i = 0; i < stars.length; i++) {
                rowHTML += '<a href="single-star.html?id=' + ids[i] + '">' + stars[i] +'</a>' ;
                rowHTML += ", "
        }
        rowHTML +=  "</td>";
        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
        rowHTML += "<td><a onclick='addToCart(\""+resultData[i]["movie_title"]+ "\")'><i class=\"fa fa-plus\"></i></a></td>";


        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}
function changeResult(){
    results = document.getElementById('results').value;
    renderList()
}
function changeOrder(){
    order = document.getElementById('order').value
    renderList();
}
function nextPage(){
    if(!(total_count<results)){
        currentPage +=1;
        renderList();
    }

}
function getPage(){
    return currentPage;
}
function prevPage(){
    if(currentPage>0){
        currentPage-=1;
        renderList();
    }
}
function renderList(){
    if (movieGenre != null){
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/browse?genre=" + movieGenre + "&results=" + results+ "&offset=" + currentPage*results + "&order=" + order,
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
    else if (movieAlphaNum != null){
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/browseAlpha?first=" + movieAlphaNum + "&results=" + results+ "&offset=" + currentPage*results + "&order=" + order,
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
    else{
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/search?title="+ movieTitle+"&director="+movieDir+"&star="+movieStar+"&year="+movieYear+ "&results=" + results + "&offset=" + currentPage*results  + "&order=" + order , // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
    }
}