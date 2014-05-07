<!DOCTYPE html>
<%@ page import="java.io.*,java.net.*" %>
<html lang="en">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"> 
        <title>SearchHotels</title>
        <meta name="generator" content="Bootply" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <script src="http://code.jquery.com/jquery-1.8.3.js" type="text/javascript"></script>
        <script src="js/typeahead.bundle.min.js" type="text/javascript"></script>

        <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js" type="text/javascript"></script>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css" rel="stylesheet">
        
        <!--[if lt IE 9]>
          <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
      <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.min.css" type="text/css" rel="stylesheet">

        <!-- CSS code from Bootply.com editor -->
        
        <style type="text/css">
            html,body {
  height:85%;
}

h1 {
  font-family: Arial,sans-serif
  font-size:80px;
  color:#DDCCEE;
}

.lead {
  color:#DDCCEE;
}


/* Custom container */
.header {
  width: 100%;
  background-color:#110022;
  color:#eee;
  overflow:hidden;
}

.v-center {
  margin-top:40px;
}
  
        </style>
    </head>
    
    <!-- HTML code from Bootply.com editor -->
    
    <body>
      <%
        String query = null;
        if(request.getParameter("query") != null && request.getParameter("query") != "") {
          query = request.getParameter("query");
        }
      %>
      <div class="container header">
        <div class="row">
          <div class="col-lg-4">
            <h1>Search Hotels</h1>
            <p class="lead">Text query search on hotel reviews</p>
          </div>  
          <div class="col-lg-6 v-center">
            <form action="search.jsp" method="POST">
              <div class="input-group">
              <%
                if(query == null)
                  out.print("<input type=\"text\" class=\"form-control input-lg\" name=\"query\" placeholder=\"Enter query\">");
                else
                  out.print("<input type=\"text\" class=\"form-control input-lg\" name=\"query\" value=\"" + query + "\">");
              %>
                <span class="input-group-btn"><button type="submit" class="btn btn-lg btn-primary submit">Search</button></span>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="container">
        <div class="row">
            <%
              if(query != null) {
                  Socket clientSocket = new Socket("localhost", 6789);
                  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                  outToServer.writeBytes(query + '\n');
                  String result = inFromServer.readLine();
                  String [] results = result.split("@@@");
                  for(String r: results){
                    out.print("<div class=\"well\">" + r + "</div>");
                  }
              }
            %>
        </div>  
        <hr>
        <div class="row">
          <div class="col-lg-12">
            <p class="text-center">CS410 Class Project</p>
            <p class="text-center">Team: Shruthi Sudhanva, Sinduja Subramaniam</p>
            <p class="text-center">Guided by Hongning</p>
          </div>
        </div>
      </div>

      <!-- JavaScript jQuery code from Bootply.com editor -->
      <script type="text/javascript" src="js/main.js"></script>
      <script type='text/javascript'>
      
      $(document).ready(function(){
        
      });  
      </script>
    </body>
</html>