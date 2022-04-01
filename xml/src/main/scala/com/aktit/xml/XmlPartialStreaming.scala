package com.aktit.xml

import com.aktit.utils.FailFast
import org.apache.commons.lang3.StringEscapeUtils

import java.io.{InputStream, StringWriter}
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events._
import scala.jdk.CollectionConverters._
import scala.xml.{Node, SAXParser, Source, XML}

/** Loads each scala.xml.Node at a time, avoiding loading the whole xml into memory.
  *
  * Note: this class requires optimization/refactoring.
  *
  * @author
  *   kostas.kougios
  */
class XmlPartialStreaming {
  private val xmlInputFactory = XMLInputFactory.newInstance
  xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false)

  private def newParser: SAXParser = {
    val f = SAXParserFactory.newInstance
    f.setNamespaceAware(false)
    f.newSAXParser
  }

  private val parser = newParser

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
                parser.reset()
                val n = XML.loadXML(Source.fromString(xmlStr), parser)
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

    nextElement.iterator
  }

  private def toString(e: XMLEvent): String = e match {
    case s: StartElement =>
      s"""<${s.getName.getLocalPart} ${s.getAttributes.asScala
        .collect { case a: Attribute =>
          toString(a)
        }
        .mkString(" ")}>"""
    case s: EndElement =>
      s"""</${s.getName.getLocalPart}>"""
    case c: Characters => StringEscapeUtils.escapeXml11(c.getData)
    case c: Comment    => "<!-- " + c.getText + " -->"
    case _             => ""
  }

  private def toString(a: Attribute) = s"""${a.getName.getLocalPart}="${StringEscapeUtils.escapeXml11(a.getValue)}" """
}

object XmlPartialStreaming {
  def setup(): Unit = {
    // avoids Message: JAXP00010004: The accumulated size of entities is "50,000,001" that exceeded the "50,000,000" limit set by "FEATURE_SECURE_PROCESSING"
    System.setProperty("jdk.xml.totalEntitySizeLimit", "2147480000")
  }
}
