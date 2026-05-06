plugins {
	kotlin("jvm") version "2.3.20"
	`java-library`
	`maven-publish`

	id("org.jlleitschuh.gradle.ktlint") version "12.1.2" // For code formatting
	id("io.gitlab.arturbosch.detekt") version "1.23.8" // For static code analysis
}

group = property("group")!!
version = property("version")!!
repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
}

detekt {
	buildUponDefaultConfig = true
	allRules = false
	config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
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

tasks.named("check") {
	dependsOn("ktlintCheck")
	dependsOn("detekt")
}

tasks.register("verifyCodeQuality") {
	group = "verification"
	dependsOn("ktlintCheck", "detekt", "test")
}

tasks.register("formatCode") {
	group = "formatting"
	dependsOn("ktlintFormat")
}
