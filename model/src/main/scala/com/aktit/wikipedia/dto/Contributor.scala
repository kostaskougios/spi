package com.aktit.wikipedia.dto

import scala.xml.NodeSeq

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
sealed trait Contributor

object Contributor
{
	def fromXml(revisionXml: NodeSeq) = {
		val contributorXml = revisionXml \ "contributor"

		if (contributorXml.flatMap(_.child).isEmpty) {
			ContributorUnknown
		} else {
			if ((contributorXml \ "ip").isEmpty) {
				ContributorUser((contributorXml \ "id").text.trim.toLong, (contributorXml \ "username").text)
			} else {
				ContributorIP((contributorXml \ "ip").text.trim)
			}
		}
	}
}

case class ContributorUser(id: Long, name: String) extends Contributor

case class ContributorIP(ip: String) extends Contributor

case object ContributorUnknown extends Contributor