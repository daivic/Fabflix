/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function addToCart(title){
    $.ajax("api/index", {
        method: "POST",
        data: "title="+title,

    });
    alert("Successfully Added " + title);
}
function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");
    let addmovie = jQuery("#add_info")
    let addbtn = jQuery("#add_btn");

    addbtn.append("<a onclick='addToCart(\""+resultData[0]["movie_title"]+ "\")'><i class=\"fa fa-plus\"></i></a>");


    let dir = resultData[0]["movie_director"];
    //let genres = resultData[0]["movie_genres"];
    let rating = resultData[0]["movie_rating"];
    // append two html <p> created to the h3 body, which will refresh the page
    // movieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_name"] + "</p>" +
    //    "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");
    movieInfoElement.append(resultData[0]["movie_title"] +
        "<i>(" + resultData[0]["movie_year"] + ")</i>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

// Concatenate the html tags with resultData jsonObject to create table rows
    const genres = resultData[0]["movie_genres"].split(",");
    const genresIDs = resultData[0]["genre_ids"].split(",");
    let rowHTML = "<table>\n" +
    "    <!-- Create a table header -->\n" +
    "        <thead>\n" +
    "        <tr>\n" +
    "            <th>Director</th>\n" +
    "            <th>Genres</th>\n" +
    "            <th>Rating</th>\n" +
    "        </tr>\n" +
    "        </thead>"+
        "<tr> <th>"+dir+" </th>" +
    " <th> "
    for (let i = 0; i < genres.length; i++) {
        rowHTML += '<a href="movielist.html?genre=' + genresIDs[i] + '">' + genres[i] + "," +'</a>' ;
    }

     rowHTML += "</th>" +
        " <th>"+rating+" </th>" +
        "</tr>" +
        "</table>";
    addmovie.append(rowHTML);
    const stars = resultData[0]["movie_stars"].split(",");
    const ids = resultData[0]["star_ids"].split(",");
    //just added
    for (let i = 0; i < stars.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>";

        rowHTML += '<a href="single-star.html?id=' + ids[i] + '">' + stars[i] +'</a>' ;
        rowHTML +=  "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }


}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
console.log(movieId);
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});