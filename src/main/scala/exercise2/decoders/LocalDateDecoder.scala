package exercise2.decoders

import cats.data.Validated
import org.http4s.ParseFailure
import org.http4s.QueryParamDecoder

import java.time.LocalDate

object LocalDateDecoder {
  implicit val localDateDecoder: QueryParamDecoder[LocalDate] = param =>
    Validated
      .fromTry(scala.util.Try(LocalDate.parse(param.value)))
      .leftMap(t => ParseFailure(s"Invalid LocalDate: ${t.getMessage}", t.getMessage))
      .toValidatedNel
}