package com.jsroka.task.services.file

import fs2.Stream

trait CsvReadingService[F[_], Row] {

  def readCsv(fileName: String): Stream[F, Row]
}
