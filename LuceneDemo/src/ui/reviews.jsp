<!DOCTYPE html>
<%@ page import="java.io.*,java.net.*, java.util.regex.Matcher, java.util.regex.Pattern" %>
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
        String id = request.getParameter("id");

      %>
      <div class="container header">
        <div class="row">
          <div class="col-lg-4">
            <h1>Search Hotels</h1>
            <p class="lead">Text query search on hotel reviews</p>
          </div>  
        </div>
      </div>
      <%
        String fileName = "/home/shruthi/cs410Project/LuceneDemo/src/TripAdvisor/TripAdvisor/hotel_" + id + ".dat";
        File file = new File(fileName);
        BufferedReader bReader;
        if(file.exists()) {
            bReader = new BufferedReader(new FileReader(fileName));
            String line,url="",content="",location="",author="";
            String hotelName="";
            while((line = bReader.readLine())!=null){
            if(line.contains("<Hotel Name>"))
            {
              hotelName = line.replace("<Hotel Name>", "");
              StringBuilder str = new StringBuilder(hotelName);
              str.deleteCharAt(0);
              hotelName = str.toString();
              out.print("<h1>"+hotelName+"</h1>");
            }
            if (line.contains("<URL>")) {
              Pattern pattern = Pattern.compile("-(\\w*).html");
              Matcher matcher = pattern.matcher(line);
              if (matcher.find())
              {
                location = matcher.group(1).replaceAll("_", " ");
                out.print("<h4>"+location+"</h4><br>");
              }
            }
            if(line.contains("<Author>")){
              author = line.replace("<Author>","");
              out.print("<div class='well'><b>"+author);
            }
            if (line.contains("<Content>")) {
              content = line.replace("<Content>", "");
              out.print("</b><br>"+content+"</div>");
          }
        }
      }
      %>
      <div class="container">
        <div class="row">

        </div>  
        <hr>
        <div class="row">
          <div class="col-lg-12">
            <p class="text-center">CS410 Class Project</p>
            <p class="text-center">Team: Shruthi Sudhanva, Sinduja Subramaniam</p>
            <p class="text-center"></p>
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