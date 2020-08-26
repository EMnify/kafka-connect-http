package io.kirill.kafka.connect.http.sink.authenticator

import io.kirill.kafka.connect.http.sink.HttpSinkConfig
import sttp.client.{Identity, NothingT, SttpBackend}

trait Authenticator {
  def authHeader(): String
}

object Authenticator {
  def oauth2(config: HttpSinkConfig, backend: SttpBackend[Identity, Nothing, NothingT]): Authenticator =
    new Oauth2Authenticator(config, backend)
}