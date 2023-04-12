package com.jsroka.task.services.file

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class CsvIntReaderSpec extends AnyFlatSpecLike with Matchers {

  private val testedFileName = "src/test/resources/example.csv"

  it should "read example file from resources and return stream of elements" in {

    val stream = new CsvIntReader[IO].readCsv(testedFileName)

    val resultList = stream.compile.toList.unsafeRunSync()
    resultList shouldEqual List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  }
}
