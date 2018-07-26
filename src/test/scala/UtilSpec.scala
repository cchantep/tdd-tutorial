class UtilSpec extends org.specs2.mutable.Specification {
  "Utilities" title

  import reactivemongo.util.withContent

  section("unit")
  "URI" should {
    import scala.io.Source

    "be loaded from a local file" in {
      val resource = new java.io.File("/etc/hosts")

      withContent(resource.toURI) { in =>
        Source.fromInputStream(in).mkString must beTypedEqualTo(
          Source.fromFile(resource).mkString)
      }
    }

    "be loaded from classpath" in {
      val resource = getClass.getResource("/reference.conf")

      withContent(resource.toURI) { in =>
        Source.fromInputStream(in).mkString must beTypedEqualTo(
          Source.fromURL(resource).mkString)
      }
    }
  }
  section("unit")

  "DNS resolver" should {
    import scala.concurrent.duration._
    import org.xbill.DNS.{ Lookup, Name, Resolver, Type }

    val timeout = 5.seconds

    def defaultResolver: Resolver = {
      val r = Lookup.getDefaultResolver
      r.setTimeout(timeout.toSeconds.toInt)
      r
    }

    def srvRecords(
      name: String,
      srvPrefix: String, // e.g. "_mongodb._tcp"
      resolver: Resolver = defaultResolver): List[String] = ???

    def txtRecords(
      name: String,
      resolver: Resolver = defaultResolver): List[String] = ???
    // ---

    "resolve SRV record for _imaps._tcp at gmail.com" in {
      srvRecords("gmail.com", "_imaps._tcp") must_=== List("imap.gmail.com")
    } tag "srvRecords"

    "resolve TXT record for gmail.com" in {
      txtRecords("gmail.com") must_=== List("v=spf1 redirect=_spf.google.com")
    } tag "txtRecords"
  }
}
