<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import = "java.util.*, java.math.*, java.sql.*"  %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="javax.naming.InitialContext" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Fabflix</title>
</head>
<a href="items">Cart</a>
<body BGCOLOR="#FDF5E6">
<h2>Search</h2>
<form ACTION="movielist.html" METHOD="GET">
    Title: <input TYPE="TEXT" name="title"><br>
    Director: <input TYPE="TEXT" name="director"> <br>
    Star: <input TYPE="TEXT" name="star"><br>
    Year: <input TYPE="TEXT" name="year"><br>

    <input TYPE="SUBMIT" VALUE="Search">

</form>
<h2>Browse by Genre</h2>
<form action="movielist.html">
    Genre:<select name="genre">

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
    <input type="submit">

</form>
<h2>Browse Alphabetically</h2>
<form action="movielist.html">
    First Character:<select name="first">

    <%
        String alphnum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for(int i=0; i<=alphnum.length()-1; i++){%>
            <option value= <%=alphnum.charAt(i)%>> <%=alphnum.charAt(i) %> </option>
        <%} %>
    </select>
<input type="submit">

</form>

</body>
</html>
