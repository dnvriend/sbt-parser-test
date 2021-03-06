libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.7"

// to format scala source code
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

// enable updating file headers eg. for copyright
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1")

// a prompt for sbt
//addSbtPlugin("com.scalapenos" % "sbt-prompt" % "1.0.0")

// enable twirl template engine
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.1.1")