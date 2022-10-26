package ciris.aws.ssm

import software.amazon.awssdk.services.ssm.SsmAsyncClient
import software.amazon.awssdk.services.ssm.model.{
  GetParameterRequest,
  GetParameterResponse,
  Parameter,
  ParameterNotFoundException
}

import java.util.concurrent.CompletableFuture
import scala.collection.mutable

case class SsmAsyncClientMock(initialValues: (String, String)*) extends SsmAsyncClient {

  def serviceName(): String = "mock-ssm"

  def close(): Unit = ()

  private[this] val secrets = mutable.Map(initialValues: _*)

  override def getParameter(req: GetParameterRequest): CompletableFuture[GetParameterResponse] =
    CompletableFuture.completedFuture {
      assert(
        req.withDecryption,
        "the GetParameterRequest call from param() should always specify decryption"
      )
      secrets.get(req.name) match {
        case None =>
          throw ParameterNotFoundException.builder
            .message(s"${getClass.getSimpleName} - no mock secret setup")
            .build()
        case Some(v) =>
          GetParameterResponse.builder
            .parameter(
              Parameter.builder
                .name(req.name)
                .value(v)
                .build
            )
            .build
      }
    }

}
