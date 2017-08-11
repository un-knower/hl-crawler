logLevel := Level.Warn

resolvers += Resolver.sbtPluginRepo("releases")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.6")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.7.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

