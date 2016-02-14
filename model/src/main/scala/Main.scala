/**
  * Created by Egor on 13.02.2016.
  */
object Main extends App{

  slick.codegen.SourceCodeGenerator.main(
    Array("slick.driver.MySQLDriver", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/schedule", "./model/src/main/scala", "com.eb.schedule.model", "root", "q1w2e3r4")
  )

}
