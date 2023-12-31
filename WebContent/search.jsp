<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import = "java.util.*, java.math.*, java.sql.*"  %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="javax.naming.InitialContext" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Fabflix</title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.11/jquery.autocomplete.min.js"></script>

</head>

<div class="topnav">
    <a style="float: right" onclick="window.location.href = 'cart.html';"><i class="fa fa-shopping-cart"></i></a>
    <a>Fabflix</a>

    <form ACTION="movielist.html" METHOD="GET">
        <input TYPE="TEXT" name="title" placeholder="Title">
        <input TYPE="TEXT" name="director" placeholder="Director">
        <input TYPE="TEXT" name="star" placeholder="Star">
        <input TYPE="TEXT" name="year" placeholder="Date">

        <input TYPE="SUBMIT" VALUE="Search">

    </form>

    <form ACTION="movielist.html" METHOD="GET">
        <input TYPE="TEXT" name="movietitle" id="autocomplete" placeholder="Title">

        <input TYPE="SUBMIT" VALUE="Full Text Search">

    </form>
</div>
<body >

<form action="movielist.html">
    <select name="genre">

        <%
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            Connection conn = dataSource.getConnection();
            String query = "SELECT name, id FROM genres ORDER BY name";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){%>
                <option value= <%=result.getString("id") %>> <%=result.getString("name") %> </option>
        <%} %>
    </select>
    <input type="submit" value="Browse">

</form>
<form action="movielist.html">
    <select name="first">

    <%
        String alphnum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789*";
        for(int i=0; i<=alphnum.length()-1; i++){%>
            <option value= <%=alphnum.charAt(i)%>> <%=alphnum.charAt(i) %> </option>
        <%} %>
    </select>
<input type="submit" value="Browse">

</form>

</body>
</html>

<script>
    function handleLookup(query, doneCallback) {
        console.log("autocomplete initiated")

        // TODO: if you want to check past query results first, you can do it here
        if(localStorage.getItem(query)!= null){
            console.log("autocomplete data retrieved from local storage cache")
            console.log(localStorage.getItem(query));
            handleLookupAjaxSuccess(localStorage.getItem(query), query, doneCallback)

        }
        else {
            // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
            // with the query data
            jQuery.ajax({
                "method": "GET",
                // generate the request url from the query.
                // escape the query string to avoid errors caused by special characters
                "url": "movie-suggestion?query=" + escape(query),
                "success": function (data) {
                    // pass the data, query, and doneCallback function into the success handler
                    console.log("autocomplete data retrieved from ajax request to server")
                    localStorage.setItem(query, data);
                    console.log(data);
                    handleLookupAjaxSuccess(data, query, doneCallback)
                },
                "error": function (errorData) {
                    console.log("lookup ajax error")
                    console.log(errorData)
                }
            })
        }
    }


    /*
     * This function is used to handle the ajax success callback function.
     * It is called by our own code upon the success of the AJAX request
     *
     * data is the JSON data string you get from your Java Servlet
     *
     */
    function handleLookupAjaxSuccess(data, query, doneCallback) {
        // parse the string into JSON
        var jsonData = JSON.parse(data);
        //console.log(jsonData)
        //var jsonData = data;
        // TODO: if you want to cache the result into a global variable you can do it here

        // call the callback function provided by the autocomplete library
        // add "{suggestions: jsonData}" to satisfy the library response format according to
        //   the "Response Format" section in documentation
        doneCallback( { suggestions: jsonData } );
    }


    /*
     * This function is the select suggestion handler function.
     * When a suggestion is selected, this function is called by the library.
     *
     * You can redirect to the page you want using the suggestion data.
     */
    function handleSelectSuggestion(suggestion) {
        // TODO: jump to the specific result page based on the selected suggestion
        window.location.href= "single-movie.html?id="+suggestion["data"]["movieId"];

        //console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])
    }


    /*
     * This statement binds the autocomplete library with the input box element and
     *   sets necessary parameters of the library.
     *
     * The library documentation can be find here:
     *   https://github.com/devbridge/jQuery-Autocomplete
     *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
     *
     */
    // $('#autocomplete') is to find element by the ID "autocomplete"
    $('#autocomplete').autocomplete({
        // documentation of the lookup function can be found under the "Custom lookup function" section
        lookup: function (query, doneCallback) {
            handleLookup(query, doneCallback)
        },
        onSelect: function(suggestion) {
            handleSelectSuggestion(suggestion)
        },
        // set delay time
        deferRequestBy: 300,
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters
        minChars: 3,
    });


    /*
     * do normal full text search if no suggestion is selected
     */
    function handleNormalSearch(query) {
        console.log("doing normal search with query: " + query);
        // TODO: you should do normal search here

    }

    // bind pressing enter key to a handler function
    $('#autocomplete').keypress(function(event) {
        // keyCode 13 is the enter key
        if (event.keyCode == 13) {
            // pass the value of the input box to the handler function
            handleNormalSearch($('#autocomplete').val())
        }
    })
</script>