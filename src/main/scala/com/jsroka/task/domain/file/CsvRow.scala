package com.jsroka.task.domain.file

import fs2.data.csv.generic.semiauto.{deriveRowDecoder, deriveRowEncoder}
import fs2.data.csv.{RowDecoder, RowEncoder}

case class CsvRow(value: Int)

object CsvRow {

  implicit val myRowDecoder: RowDecoder[CsvRow] = deriveRowDecoder[CsvRow]
  implicit val myRowEncode: RowEncoder[CsvRow] = deriveRowEncoder[CsvRow]
}
