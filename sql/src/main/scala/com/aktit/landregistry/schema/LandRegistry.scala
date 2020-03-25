package com.aktit.landregistry.schema

import java.sql.Date

/**
  * @author kostas.kougios
  *         25/03/2020 - 20:17
  */
case class LandRegistry(
	id: String,
	price: Long,
	purchasedDate: Date,
	postCode: String,
	propertyType: String,
	unknown1: String,
	unknown2: String,
	houseNumber: String,
	houseName: String,
	address1: String,
	address2: String,
	address3: String,
	address4: String,
	address5: String
)
