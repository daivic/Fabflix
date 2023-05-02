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
