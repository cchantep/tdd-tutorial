---
title: TDD tutorial
date: \today
fontsize: 11pt
monofont: Menlo
mainfont: Avenir
header-includes:
- \usepackage{pandoc-solarized}
- \input{beamer-includes}
---

## TDD tutorial

A TDD tutorial based on ReactiveMongo and particularly the PullRequest [#750](https://github.com/ReactiveMongo/ReactiveMongo/pull/750).

## Introduction

The goal is to share a [TDD](https://en.wikipedia.org/wiki/Test_driven_development) example, based on [ReactiveMongo code](http://github.com/reactivemongo/reactivemongo).

The Pull Request [#750](https://github.com/ReactiveMongo/ReactiveMongo/pull/750), which aims to implement a support for [DNS seed list](https://github.com/mongodb/specifications/blob/master/source/initial-dns-seedlist-discovery/initial-dns-seedlist-discovery.rst#initial-dns-seedlist-discovery), will be used as subject.

*Estimated time: 1h 30min (reading time 10min)*

> Keywords: [tdd](https://en.wikipedia.org/wiki/Test_driven_development), [scala](https://www.scala-lang.org/), [specs2](https://etorreborre.github.io/specs2/)

## Requirements

**Dev tools:**

- [Git client](https://git-scm.com/)
- JDK 1.8+
- [SBT](http://scala-sbt.org/)

**Knowledge:**

- *required:* [Scala 2.12+](https://www.scala-lang.org/)
- *optional:* [specs2](https://etorreborre.github.io/specs2) (test framework)

## Start the tutorial

You first need to git-clone this tutorial:

```bash
git clone git@github.com:cchantep/tdd-tutorial.git \
  --branch master --single-branch
```

Then the TDD approach is described step by step thereafter.

## Phase 1 - Setup dnsjava

We will be using the library [*dnsjava*](http://www.dnsjava.org/), so at first step you need to add the corresponding [dependency (version 2.1.8)](http://search.maven.org/#artifactdetails%7Cdnsjava%7Cdnsjava%7C2.1.8%7Cjar) in the [`build.sbt`](https://github.com/cchantep/tdd-tutorial/blob/master/build.sbt) (make sure to reload the build in SBT).

Once that's ok, we will starting testing right now, by checking how to use *dnsjava* to resolve SRV records.

At this point you can start the REPL using `sbt console` (or just type `console` if SBT is already launched).

## Phase 1 - Read documentation & first test

When the SBT Scala REPL is started, it's time to read the [documentation](http://www.dnsjava.org/dnsjava-current/doc/) to figure out how to perform a SRV lookup, and test it for `_imaps._tcp.gmail.com` in the REPL.

It should print an expected result as below:

```
Array[org.xbill.DNS.Record] = Array(
  _imaps._tcp.gmail.com.	21599	IN	SRV	\
  5 0 993 imap.gmail.com.)
```

*Time to code & test: 10min (solution thereafter)*

## Phase 1 - SRV Lookup in the REPL

You can paste the following snippet to test SRV resolution.

```scala
import org.xbill.DNS._

new Lookup("_imaps._tcp.gmail.com", Type.SRV).run()
```

## Phase 1 - Refactor as a function

Still in the REPL, you can now implement the following function using the tested lookup code, to return only the [target name](http://www.dnsjava.org/dnsjava-current/doc/org/xbill/DNS/SRVRecord.html#getTarget()) of the resolved SRV record.

```scala
import org.xbill.DNS.Name
def records(name: String): Array[Name] = ???
// lookup for SRV record _imaps._tcp.<name>
```

This function can immediately by tested in the REPL.

```scala
records("gmail.com")
// Expected result: ... Array(imap.gmail.com.)
```

*Time to code & test: 5min (solution thereafter)*

## Phase 1 - REPL records function

The `records` function can be declared as below in the REPL.

```scala
def records(name: String): Array[Name] =
  new Lookup(s"_imaps._tcp.${name}", Type.SRV).
  run().map(_.getAdditionalName)
```

## Phase 1 - Timeout resolution

In order to configure the timeout for the SRV resolution, a custom `Resolver` can be used as follows (you can test it in the REPL).

```scala
import org.xbill.DNS._

def customResolver: Resolver = {
  val r = Lookup.getDefaultResolver
  r.setTimeout(5/*seconds*/)
  r
}

val lkup = new Lookup("_imaps._tcp.gmail.com", Type.SRV)
lkup.setResolver(customResolver)
lkup.run()
```

## Phase 1 - Contracts as signature & tests

After these interactive tests in the REPL, according the TDD approach we will go on with the TDD approach, first writing a test for the SRV resolution.

The expected contracts for such resolution are written as,

- the "not implemented" [function `srvRecords`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala#L42),
- the [corresponding test](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala#L52).

## Phase 1 - Start with failing test

This test can be executed in SBT:

```bash
testOnly -- include srvRecords
```

At this point, without `srvRecords` implementation, the test is expected to fail.

```
[info] Utilities
[error]   x resolve SRV record for _imaps._tcp at ..
[error]    an implementation is missing (..)
[info] Total for specification Utilities
[info] Finished in 185 ms
[info] 1 example, 1 failure, 0 error
```

## Phase 1 - Implement srvRecords to match acceptance

Now it's time to make the test happy, by implementing `srvRecords` so it matches its contract.

To do so, you can edit the function in [UtilSpec](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala).

The SBT task must be executed continuously until the function no longer raises any error.

```bash
testOnly UtilSpec -- include srvRecords
```

*Time to code & test: 5min (solution thereafter)*

## Phase 1 - Solution & expected result

*See the [online solution](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase1/src/test/scala/UtilSpec.scala#L42)*

Expected test results:

```
[info] Utilities
[info]   + resolve SRV record for _imaps._tcp at ...
[info] Total for specification Utilities
[info] Finished in 195 ms
[info] 1 example, 0 failure, 0 error
[info] Passed: Total 1, Failed 0, Errors 0, Passed 1
```

## Phase 1 - Commit

At this step, it's time to commit the changes applied on `build.sbt` and `UtilSpec.scala`.

```bash
git commit -a -m "Phase #1"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase1`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase1).

## Phase 2 - List TXT records

We know how to resolve SRV records, to implements [DNS seed list](https://github.com/mongodb/specifications/blob/master/source/initial-dns-seedlist-discovery/initial-dns-seedlist-discovery.rst#initial-dns-seedlist-discovery), TXT records are also required.

You can first test it for domain gmail.com in the REPL: `sbt console`

Considering only the RDATA for each record, the expected result is `Array("v=spf1 redirect=_spf.google.com")`

*Time to code & test: 10min (solution thereafter)*

## Phase 2 - TXT lookup in REPL

The TXT records for gmail.com can be checked as below.

```scala
import org.xbill.DNS._

new Lookup("gmail.com", Type.TXT).
  run().map(_.rdataToString)
// => Array("v=spf1 redirect=_spf.google.com")
```

## Phase 2 - Contracts as signature & tests

After these REPL tests, we will save it in `UtilSpec.scala` with the "not implemented" [function `txtRecords`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala#L47).

According the function signature, you have to write the [test in `UtilSpec`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala#L56) to check that TXT resolution for gmail.com returns `v=spf1 redirect=_spf.google.com`.

```scala
"resolve TXT record for gmail.com" in {
  todo /* List("v=spf1 redirect=_spf.google.com") */
}
```

*Time to code test: 1min*

## Phase 2 - Start with failing test

As soon as the test for `txtRecords` is written, it can be used in SBT.

```bash
testOnly
```

For now, this test is expected to fail (without the function implementation).

## Phase 2 - Implement txtRecords to match acceptance

From there, you can implement the `txtRecords` function in [UtilSpec](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala).

Until the corresponding test is successful, the SBT task can be executed continuously.

```bash
testOnly UtilSpec
```

*Time to code & test: 5min (solution thereafter)*

## Phase 2 - Solution & expected result

*See the [online solution](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase2/src/test/scala/UtilSpec.scala#L72)*

Expected test results:

```
[info] Utilities
...
[info] DNS resolver should
[info]   + resolve SRV record for _imaps._tcp at ...
[info]   + resolve TXT record for gmail.com
[info] Total for specification Utilities
[info] Finished in 331 ms
[info] 4 examples, 0 failure, 0 error
[info] Passed: Total 4, Failed 0, Errors 0, Passed 4
```

## Phase 2 - Commit

At this step, it's time to commit the changes applied on `UtilSpec.scala`.

```bash
git commit -a -m "Phase #2"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase2`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase2).

## Phase 3 - Refactor srvRecords function

At this step, the function `srvRecords` can be moved in the package object [`reactivemongo.util`](https://github.com/cchantep/tdd-tutorial/blob/master/src/main/scala/util/package.scala).

While moving this function, you should also take the opportunity to add it the following parameter.

```scala
timeout: FiniteDuration
```

*Time to code: 5min (solution thereafter)*

## Phase 3 - Document srvRecords function

In order to finalize the `srvRecords` function, you can document it, with a Scaladoc as below.

```scala
/**
 * @param name the DNS name (e.g. `my.mongodb.com`)
 * @param timeout the resolution timeout (default: 5s)
 * @param srvPrefix the SRV prefix (e.g. `_mongodb._tcp`)
 */
```

*Time to document: 1min (solution thereafter)*

## Phase 3 - Refactored svrRecords & Scaladoc

*See the [refactoring online](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase3/src/main/scala/util/package.scala#L55)*

According this refactoring, the tests in [`UtilSpec.scala`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala) need to be updated.

*Time to update tests: 1min (solution thereafter)*

## Phase 3 - Test refactoring of srvRecords 

*See the [updated tests](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase3/src/test/scala/UtilSpec.scala#L94)*

Once done it can be used from SBT to make sure no regression is introduced:

```bash
testOnly UtilSpec
```

## Phase 3 - Commit

At this step, it's time to commit the changes applied on `package.scala` and `UtilSpec.scala`.

```bash
git commit -a -m "Phase #3"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase3`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase3).

## Phase 4 - Refactor txtRecords function

As for `srvRecords`, the function `txtRecords` can also be moved in the package object [`reactivemongo.util`](https://github.com/cchantep/tdd-tutorial/blob/master/src/main/scala/util/package.scala).

While moving this function, you should also take the opportunity to add it the following parameter.

```scala
timeout: FiniteDuration
```

*Time to code: 5min (solution thereafter)*

## Phase 4 - Document srvRecords function

In order to finalize the `srvRecords` function, you can document it, with a Scaladoc as follows.

```scala
/**
 * @param name the DNS name (e.g. `my.mongodb.com`)
 * @param timeout the resolution timeout (default: 5s)
 */
```

*Time to document: 1min (solution thereafter)*

## Phase 4 - Refactored txtRecords & Scaladoc

*See the [refactoring online](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase4/src/main/scala/util/package.scala#L98)*

According this refactoring, the tests in [`UtilSpec.scala`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala) need to be updated.

*Time to update tests: 1min (solution thereafter)*

## Phase 4 - Test the refactoring

*See the [updated tests](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase4/src/test/scala/UtilSpec.scala#L39)*

Once done it can be used from SBT to make sure no regression is introduced:

```bash
testOnly UtilSpec
```

## Phase 4 - Commit

At this step, it's time to commit the changes applied on `package.scala` and `UtilSpec.scala`.

```bash
git commit -a -m "Phase #4"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase4`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase4).

## Phase 5 - Refactor srvRecords & txtRecords

You can refactor both `srvRecords` and `txtRecords`,
to return `Future` (for composition & error handling).

*Time to code: 15min (solution thereafter)*

## Phase 5 - Refactoring & Scaladoc

*See the [refactoring online](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase5/src/main/scala/util/package.scala#L56)*

According this refactoring, the tests in [`UtilSpec.scala`](https://github.com/cchantep/tdd-tutorial/blob/master/src/test/scala/UtilSpec.scala) need to be updated.

(See [specs2 documentation](https://etorreborre.github.io/specs2/guide/SPECS2-4.2.0/org.specs2.guide.Matchers.html) about testing `Future`)

*Time to update tests: 5min (solution thereafter)*

## Phase 5 - Test the refactoring

*See the [updated tests](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase5/src/test/scala/UtilSpec.scala#L34)*

Once done it can be used from SBT to make sure no regression is introduced:

```bash
testOnly UtilSpec
```

## Phase 5 - Commit

At this step, it's time to commit the changes applied on `package.scala` and `UtilSpec.scala`.

```bash
git commit -a -m "Phase #5"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase5`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase5).

## Phase 6 - Refactor txtRecords with ListSet

As the TXT resolution must only consider the distinct records, the function `txtRecords` can be refactored to use [`ListSet`](https://www.scala-lang.org/api/2.12.6/scala/collection/immutable/ListSet.html) rather than `List`.

*Time to code: 5min (solution thereafter)*

## Phase 6 - Refactored txtRecords

*See the [online solution](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase6/src/main/scala/util/package.scala#L138)*

After this refactoring, the tests must be failing when using `testOnly` in SBT.
So `UtilSpec.scala` need to be updated accordingly.

*Time to update tests: 1min (solution thereafter)*

## Phase 6 - Tests & commit

*See the [updated tests](https://github.com/cchantep/tdd-tutorial/blob/tdd/phase6/src/test/scala/UtilSpec.scala#L48)*

At this step, it's time to commit the changes applied on `package.scala` and `UtilSpec.scala`.

```bash
git commit -a -m "Phase #6"
```

> The code expected after this phase can be checked with the online tag [`tdd/phase6`](https://github.com/cchantep/tdd-tutorial/tree/tdd/phase6).

## Complete solution

You can have a look at a complete solution for all this tutorial in the [`solution` branch](https://github.com/cchantep/tdd-tutorial/compare/solution#commits_bucket).