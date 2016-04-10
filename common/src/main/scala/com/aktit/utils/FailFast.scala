package com.aktit.utils

/**
  * @author kostas.kougios
  */
object FailFast
{
	def notNull(o: Any, name: String): Unit = {
		if (o == null) throw new NullPointerException("o can't be null")
	}
}
