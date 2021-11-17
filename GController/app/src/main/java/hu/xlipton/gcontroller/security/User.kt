package hu.xlipton.gcontroller.security

data class User(val userName: String, val firstName: String, val lastName: String, val roles: List<String>)
