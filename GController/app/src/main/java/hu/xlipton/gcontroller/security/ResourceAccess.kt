package hu.xlipton.gcontroller.security

import com.google.gson.annotations.SerializedName

class ResourceAccess(
	@SerializedName("roles")
	val roles: List<String>
)