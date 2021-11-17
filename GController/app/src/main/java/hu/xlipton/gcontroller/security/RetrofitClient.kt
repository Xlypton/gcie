package hu.xlipton.gcontroller.security

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
	companion object {
		private const val BASE_URL: String = "http://gdevice:8080"
		private var retrofit: Retrofit? = null

		fun getRetrofitInstance(): Retrofit {
			retrofit = Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build()
			return retrofit as Retrofit
		}
	}
}
