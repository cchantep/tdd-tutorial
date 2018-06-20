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
      resolver: Resolver = defaultResolver): List[String] = {
      val service = Name.fromConstantString(name + '.')
      // assert service.label >= 3

      val baseName = Name.fromString(
        name.dropWhile(_ != '.').drop(1), Name.root)

      val srvName = Name.concatenate(
        Name.fromConstantString(srvPrefix), service)

      val lookup = new Lookup(srvName, Type.SRV)

      lookup.setResolver(resolver)

      lookup.run().map { rec =>
        val nme = rec.getAdditionalName

        // if nme.isAbsolute then assert nme.subdomain(baseName)

        if (nme.isAbsolute) {
          nme.toString(true)
        } else {
          Name.concatenate(nme, baseName).toString(true)
        }
      }.toList
    }

    def txtRecords(
      name: String,
      resolver: Resolver = defaultResolver): List[String] = {

      val lookup = new Lookup(name, Type.TXT)

      lookup.setResolver(resolver)

      lookup.run().map { rec =>
        val data = rec.rdataToString
        val stripped = data.stripPrefix("\"")

        if (stripped == data) {
          data
        } else {
          stripped.stripSuffix("\"")
        }
      }.toList
    }

    // ---

    "resolve SRV record for _imaps._tcp at gmail.com" in {
      reactivemongo.util.srvRecords(
        name = "gmail.com",
        srvPrefix = "_imaps._tcp") must_=== List("imap.gmail.com")

    } tag "srvRecords"

    "resolve TXT record for gmail.com" in {
      todo /* test that calling txtRecords for gmail.com returns the following:
            List("v=spf1 redirect=_spf.google.com") */
    } tag "txtRecords"
  }
}
