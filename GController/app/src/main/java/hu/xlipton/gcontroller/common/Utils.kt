/*
 * Author : AdNovum Informatik AG
 */

package hu.xlipton.gcontroller.common

import android.util.Log
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Utils {
	companion object {
		fun calculateDistanceFromCoordinates(x1: Float, y1: Float, x2: Float, y2: Float): Float{
			return sqrt(((x1 - x2) * (x1 - x2) + (y1 -
					y2)	* (y1 - y2)).toDouble())
				.toFloat()
		}

		fun convertHandsFloatToUsableInt(input: Float): Int {
			return (input * 100).roundToInt()
		}

		fun calculateVectorsAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
			val dot = x1 * x2 + y1 * y2
			val det = x1 * y2 - y1 * x2
			Log.i("angle", "det: $det, dot: $dot")
			return atan2( det, dot )
		}
	}
}
