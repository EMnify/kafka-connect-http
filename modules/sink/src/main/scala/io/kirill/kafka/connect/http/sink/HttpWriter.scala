package io.kirill.kafka.connect.http.sink

import org.apache.kafka.connect.sink.SinkRecord

final class HttpWriter(val conf: HttpSinkConfig) {

  var currentBatch: Seq[SinkRecord] = List()

  def put(records: Seq[SinkRecord]): Unit = {
    if (currentBatch.size + records.size >= conf.batchSize) {
      val (batch, remaining) = (currentBatch ++ records).splitAt(conf.batchSize)
      send(batch)
      currentBatch = remaining
    } else {
      currentBatch = currentBatch ++ records
    }
  }

  def flush(): Unit = {
    send(currentBatch)
    currentBatch = List()
  }

  def send(records: Seq[SinkRecord]): Unit = {

  }
}

object HttpWriter {

  def sendRequest(conf: HttpSinkConfig, body: String): Unit = {

  }

  def formatRecords(conf: HttpSinkConfig, records: Seq[SinkRecord]): String = {
    val regexReplacements = conf.regexPatterns.zip(conf.regexReplacements)
    val formatRecord: SinkRecord => String = rec => regexReplacements.foldLeft(rec.value().toString) {
      case (res, (regex, replacement)) => res.replaceAll(regex, replacement)
    }

    records.map(formatRecord).mkString(conf.batchPrefix, conf.batchSeparator, conf.batchSuffix)
  }

  def apply(conf: HttpSinkConfig): HttpWriter = new HttpWriter(conf)
}
