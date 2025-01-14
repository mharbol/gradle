// tag::use-plugin[]
plugins {
    `java-library`
}
// end::use-plugin[]


// tag::repo[]
repositories {
    mavenCentral()
}
// end::repo[]

// tag::dependencies[]
dependencies {
    api("org.apache.httpcomponents:httpclient:4.5.7")
    implementation("org.apache.commons:commons-lang3:3.5")
}
// end::dependencies[]

if (hasProperty("toolchainJvmVersion")) {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(property("toolchainJvmVersion") as String)
        }
    }
}
