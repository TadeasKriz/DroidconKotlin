package co.touchlab.sessionize

import com.russhwolf.settings.Settings
import co.touchlab.stately.collections.frozenHashMap

class TestSettings() :Settings {
    private val map = frozenHashMap<String, Any?>()

    override val keys: Set<String> = map.keys
    override val size: Int = map.size

    override fun clear() {
        map.clear()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if(map.containsKey(key)){
            map[key] as Boolean
        }else{
            defaultValue
        }
    }

    override fun getBooleanOrNull(key: String): Boolean? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return if(map.containsKey(key)){
            map[key] as Double
        }else{
            defaultValue
        }
    }

    override fun getDoubleOrNull(key: String): Double? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return if(map.containsKey(key)){
            map[key] as Float
        }else{
            defaultValue
        }
    }

    override fun getFloatOrNull(key: String): Float? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return if(map.containsKey(key)){
            map[key] as Int
        }else{
            defaultValue
        }
    }

    override fun getIntOrNull(key: String): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return if(map.containsKey(key)){
            map[key] as Long
        }else{
            defaultValue
        }
    }

    override fun getLongOrNull(key: String): Long? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getString(key: String, defaultValue: String): String {
        return if(map.containsKey(key)){
            map[key] as String
        }else{
            defaultValue
        }
    }

    override fun getStringOrNull(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasKey(key: String): Boolean {
        return map.containsKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = value
    }

    override fun putDouble(key: String, value: Double) {
        map[key] = value
    }

    override fun putFloat(key: String, value: Float) {
        map[key] = value
    }

    override fun putInt(key: String, value: Int) {
        map[key] = value
    }

    override fun putLong(key: String, value: Long) {
        map[key] = value
    }

    override fun putString(key: String, value: String) {
        map[key] = value
    }

    override fun remove(key: String) {
        map.remove(key)
    }

}