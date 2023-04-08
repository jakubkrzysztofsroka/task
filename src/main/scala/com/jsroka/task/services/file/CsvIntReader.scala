package com.jsroka.task.services.file

import cats.effect.kernel.Async
import com.jsroka.task.domain.file
import fs2._
import fs2.data.csv._
import fs2.data.text.ascii.byteStreamCharLike
import fs2.io.file.Files
import fs2.io.file.Path

class CsvIntReader[F[_]: Async] extends CsvReadingService[F, Int] {

  override def readCsv(fileName: String): Stream[F, Int] = {
    val resourcePath = getClass.getClassLoader.getResource(fileName).toURI.getPath
    Files[F].readAll(Path(resourcePath)).through(decodeWithoutHeaders[file.CsvRow]()).map(_.value)
  }
}
