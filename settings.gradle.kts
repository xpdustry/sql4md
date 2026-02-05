rootProject.name = "sql4md-base"

fun includeWithEmptyDir(identifier: String) {
    mkdir(identifier)
    include(":$identifier")
}
includeWithEmptyDir("sql4md-sqlite")
includeWithEmptyDir("sql4md-h2")
includeWithEmptyDir("sql4md-mariadb")
includeWithEmptyDir("sql4md-mysql")
includeWithEmptyDir("sql4md-postgresql")
