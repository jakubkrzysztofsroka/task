package com.jsroka.task.services.file

import cats.effect.kernel.Async
import com.jsroka.task.services.file.CsvRow
import fs2._
import fs2.data.csv._
import fs2.data.text.ascii.byteStreamCharLike
import fs2.io.file.Files
import fs2.io.file.Path

class CsvIntReader[F[_]: Async] extends CsvReadingService[F, Int] {

  override def readCsv(filePath: String): Stream[F, Int] =
    Files[F].readAll(Path(filePath)).through(decodeWithoutHeaders[CsvRow]()).map(_.value)
}
