plugins {
	kotlin("jvm") version "2.3.20"
	`java-library`
	`maven-publish`
}

group = property("group")!!
version = property("version")!!
repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
}

kotlin {
	jvmToolchain(21)
}

tasks.test {
	useJUnitPlatform()
}

java {
	withSourcesJar()
	withJavadocJar()
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])

			groupId = project.group.toString()
			artifactId = "search-trees"
			version = project.version.toString()
		}
	}
}