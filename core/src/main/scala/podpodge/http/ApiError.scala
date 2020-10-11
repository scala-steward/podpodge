package podpodge.http

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto._

sealed trait ApiError extends Exception

object ApiError {
  final case class NotFound(message: String) extends ApiError

  object NotFound {
    implicit val decoder: Decoder[NotFound] = deriveDecoder[NotFound]
    implicit val encoder: Encoder[NotFound] = deriveEncoder[NotFound]
  }

  final case class InternalError(message: String) extends ApiError

  object InternalError {
    implicit val decoder: Decoder[InternalError] = deriveDecoder[InternalError]
    implicit val encoder: Encoder[InternalError] = deriveEncoder[InternalError]
  }

  implicit val decoder: Decoder[ApiError] = deriveDecoder[ApiError]
  implicit val encoder: Encoder[ApiError] = deriveEncoder[ApiError]
}