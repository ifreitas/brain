name := "brain"

version := "0.3.3"

organization := "Israel Freitas"

scalaVersion := "2.11.0"

EclipseKeys.withSource := true

resolvers ++= Seq(
                  "snapshots"            at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"              at "https://oss.sonatype.org/content/repositories/staging"  ,
                  "releases"             at "https://oss.sonatype.org/content/repositories/releases" ,
                  "Ansvia Releases Repo" at "http://scala.repo.ansvia.com/releases/"
                 )
                 
enablePlugins(JettyPlugin)

mainClass := Some("RunWebApp")

unmanagedResourceDirectories in Test += (baseDirectory) { _ / "src/main/webapp" }.value

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6"
  Seq(
    "org.scala-lang.modules"     %% "scala-xml"         % "1.0.6",
    "com.ansvia.graph"           %% "blueprints-scala"  % "0.1.61-20150416-SNAPSHOT",
    "com.orientechnologies"      % "orientdb-core"      % "1.6.3",
    "com.orientechnologies"      % "orient-commons"     % "1.6.3",
    "com.orientechnologies"      % "orientdb-client"    % "1.6.3",
    "net.liftweb"                %% "lift-webkit"       % liftVersion           % "compile" withSources(),
    "org.eclipse.jetty.orbit"    % "javax.servlet"      % "3.0.0.v201112011016" % "compile,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "org.eclipse.jetty"          % "jetty-webapp"       % "8.1.12.v20130726"    % "compile,test",
    "net.liftweb"                %% "lift-mapper"       % liftVersion           % "compile",
    "org.scalatest"              %% "scalatest"         % "2.1.3"               % "test",
    "org.mockito"                % "mockito-all"        % "1.9.5"               % "test",
    "org.seleniumhq.selenium"    % "selenium-java"      % "2.35.0"              % "test"
  )
}
