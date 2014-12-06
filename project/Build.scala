import sbt._
import sbt.Keys._

object LiftProjectBuild extends Build {

  import Dependencies._
  import BuildSettings._

  lazy val root = Project("causes", file("."))
    .settings(liftAppSettings: _*)
    .settings(libraryDependencies ++=
      compile(
        liftWebkit,
        liftMongodb,
        liftExtras,
        liftMongoauth,
        logback,
        rogueField,
        rogueCore,
        rogueLift,
        rogueIndex
      ) ++
      test(scalatest) ++
      container(jettyWebapp)
    )
}
