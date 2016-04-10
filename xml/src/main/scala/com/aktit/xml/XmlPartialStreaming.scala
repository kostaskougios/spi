package com.aktit.xml

import java.io.{InputStream, StringWriter}
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events._

import com.aktit.utils.FailFast
import com.sun.xml.internal.stream.events.DummyEvent
import org.apache.commons.lang3.StringEscapeUtils

import scala.collection.JavaConverters._
import scala.xml.{Node, XML}

/**
  * @author kostas.kougios
  */
class XmlPartialStreaming
{
	private val xmlInputFactory = XMLInputFactory.newInstance

	def parse(in: InputStream, filterElement: String): Iterator[Node] = {
		FailFast.notNull(in, "in")
		val eventReader = xmlInputFactory.createXMLEventReader(in)

		def nextElement: Stream[Node] = {
			var depth = 0
			var within = false
			var withinDepth = 0
			val xmlAsString = new StringWriter(4096)

			while (eventReader.hasNext) {
				val e = eventReader.nextEvent
				e match {
					case start: StartElement =>
						if (!within && start.getName.getLocalPart == filterElement) {
							within = true
							withinDepth = depth
						}
						depth += 1
					case stop: EndElement =>
						depth -= 1
						if (within && depth == withinDepth && stop.getName.getLocalPart == filterElement) {
							xmlAsString.append(toString(stop))
							within = false
							val xmlStr = xmlAsString.toString
							try {
								val n = XML.loadString(xmlStr)
								return n #:: nextElement
							} catch {
								case e: Throwable =>
									throw new IllegalStateException("error processing extracted xml :\n" + xmlStr, e)
							}
						}
					case _ => // nop
				}

				if (within) {
					xmlAsString.append(toString(e))
				}
			}
			eventReader.close()
			Stream.empty
		}

		nextElement.toIterator
	}

	private def toString(e: XMLEvent): String = e match {
		case s: StartElement =>
			s"""<${s.getName.getLocalPart} ${
				s.getAttributes.asScala.collect {
					case a: Attribute => toString(a)
				}.mkString(" ")
			}>"""
		case s: EndElement =>
			s"""</${s.getName.getLocalPart}>"""
		case c: Characters => StringEscapeUtils.escapeXml11(c.getData)
		case c: Comment => "<!-- " + c.getText + " -->"
		case d: DummyEvent => ""
	}

	private def toString(a: Attribute) = s"""${a.getName.getLocalPart}="${StringEscapeUtils.escapeXml11(a.getValue)}" """
}
