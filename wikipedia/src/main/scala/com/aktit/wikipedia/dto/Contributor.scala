package com.aktit.wikipedia.dto

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
trait Contributor

case class ContributorUser(id: Long, name: String) extends Contributor

case class ContributorIP(ip: String) extends Contributor

case object ContributorUnknown extends Contributor