package com.aktit.wikipedia

import com.aktit.dto.EpochDateTime
import com.aktit.loaders.dto.XmlRow
import com.aktit.spark.testing.AbstractSparkSuite
import com.aktit.wikipedia.IngestWikipediaJob._
import com.aktit.wikipedia.dto.{ContributorIP, ContributorUser}
import org.joda.time.DateTime

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
class IngestWikipediaJobTest extends AbstractSparkSuite
{
	val pages = extractDataFromXml(
		sc.parallelize(
			List(
				XmlRow("en_1.xml",
					<page>
						<title>AccessibleComputing</title>
						<ns>0</ns>
						<id>10</id>
						<redirect title="Computer accessibility"/>
						<revision>
							<id>631144794</id>
							<parentid>381202555</parentid>
							<timestamp>2014-10-26T04:50:23Z</timestamp>
							<contributor>
								<ip>1.2.3.4</ip>
							</contributor>
							<comment>add [[WP:RCAT|rcat]]s</comment>
							<model>wikitext</model>
							<format>text/x-wiki</format>
							<text xml:space="preserve">text1</text>
							<sha1>4ro7vvppa5kmm0o1egfjztzcwd0vabw</sha1>
						</revision>
					</page>
				),
				XmlRow(
					"us_2.xml",
					<page>
						<title>Autism</title>
						<ns>0</ns>
						<id>25</id>
						<revision>
							<id>674604893</id>
							<parentid>674604120</parentid>
							<timestamp>2015-08-05T00:44:14Z</timestamp>
							<contributor>
								<username>Flyer22</username>
								<id>4293477</id>
							</contributor>
							<comment>Undid revision 674604120 by [[Special:Contributions/Zimmygirl7|Zimmygirl7]]</comment>
							<model>wikitext</model>
							<format>text/x-wiki</format>
							<text xml:space="preserve">text2</text>
							<sha1>nbh7faiiieiyqj6sapyrhvaoed0h6j2</sha1>
						</revision>
					</page>
				)
			)
		)
	).collect().toSeq

	import Data._

	test("all extracted") {
		pages.size should be(2)
	}

	test("extracts id") {
		pages.map(_.id).toSet should be(Set(10, 25))
	}

	test("extracts titles") {
		pages.map(_.title).toSet should be(Set("AccessibleComputing", "Autism"))
	}

	test("extracts lang") {
		pages.map(_.lang).toSet should be(Set("en", "us"))
	}

	test("extracts redirects") {
		pages.flatMap(_.redirect).toSet should be(Set("Computer accessibility"))
	}

	test("revision.id") {
		pages.map(_.revisions.head.id).toSet should be(Set(631144794, 674604893))
	}

	test("revision.parentId") {
		pages.map(_.revisions.head.parentId).toSet should be(Set(381202555, 674604120))
	}

	test("revision.time") {
		pages.map(_.revisions.head.time).toSet should be(Set(EpochDateTime(DateTime.parse("2014-10-26T04:50:23Z")), EpochDateTime(DateTime.parse("2015-08-05T00:44:14Z"))))
	}

	test("revision.contributor") {
		pages.map(_.revisions.head.contributor).toSet should be(Set(ContributorIP("1.2.3.4"), ContributorUser(4293477, "Flyer22")))
	}

	test("revision.comment") {
		pages.map(_.revisions.head.comment).toSet should be(Set("add [[WP:RCAT|rcat]]s", "Undid revision 674604120 by [[Special:Contributions/Zimmygirl7|Zimmygirl7]]"))
	}

	test("revision.model") {
		pages.map(_.revisions.head.model).toSet should be(Set("wikitext"))
	}

	test("revision.format") {
		pages.map(_.revisions.head.format).toSet should be(Set("text/x-wiki"))
	}

	test("revision.text") {
		pages.map(_.revisions.head.text).toSet should be(Set("text1", "text2"))
	}

	test("revision.sha1") {
		pages.map(_.revisions.head.sha1).toSet should be(Set("4ro7vvppa5kmm0o1egfjztzcwd0vabw", "nbh7faiiieiyqj6sapyrhvaoed0h6j2"))
	}

	test("reduces pages by id and merges revisions, positive") {
		mergeByIdPerLang(sc.parallelize(Seq(Page1a, Page1b))).collect().toSet should be(Set(Page1a.merge(Page1b)))
	}
}
