package com.aktit.dto

import org.joda.time.{DateTime, DateTimeZone}

/**
  * We need case classes for avro serialization (avro4s)
  *
  * @author kostas.kougios
  *         21/05/18 - 12:17
  */
case class EpochDateTime(time: Long)
{
	def toJodaUTCDateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
}

object EpochDateTime
{
	def apply(joda: DateTime): EpochDateTime = EpochDateTime(joda.getMillis)

	def now = EpochDateTime(System.currentTimeMillis)
}