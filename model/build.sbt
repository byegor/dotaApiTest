name := "model"

version := "0.2"

libraryDependencies ++= Seq(
  "com.typesafe.slick"          %% "slick"            % "3.2.0",
  "com.typesafe.slick"          %% "slick-hikaricp"   % "3.2.0" exclude("com.zaxxer", "HikariCP-java6"),
  "com.typesafe"                %  "config"           % "1.3.1",

  "joda-time"                   % "joda-time"         % "2.7",
  "org.joda"                    % "joda-convert"      % "1.7",

  "mysql"                       % "mysql-connector-java" % "5.1.36",
  "com.zaxxer"                  % "HikariCP"          % "2.4.5",
  "com.h2database"              % "h2"                % "1.4.191" % "test",


  "org.apache.httpcomponents"   % "httpclient"        % "4.5",
  "com.mashape.unirest"         % "unirest-java"      % "1.4.6"
)
