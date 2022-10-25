package ciris.aws.ssm

import software.amazon.awssdk.services.ssm.model.{GetParameterRequest, GetParameterResponse, Parameter, ParameterNotFoundException}
import software.amazon.awssdk.services.ssm.{SsmAsyncClient, SsmClient}

import java.util.concurrent.CompletableFuture
import scala.collection.mutable

case class SsmClientMock(initialValues: (String, String)*)
    extends BaseMock(initialValues: _*)
    with SsmClient {

  override def getParameter(req: GetParameterRequest): GetParameterResponse =
    mockGet(req)
}

case class SsmAsyncClientMock(initialValues: (String, String)*)
    extends BaseMock(initialValues: _*)
    with SsmAsyncClient {

  override def getParameter(req: GetParameterRequest): CompletableFuture[GetParameterResponse] =
    CompletableFuture.completedFuture(mockGet(req))
}

abstract class BaseMock(initialValues: (String, String)*) {

  def serviceName(): String = "mock-ssm"

  def close(): Unit = ()

  private[this] val secrets = mutable.Map(initialValues: _*)

  def mockGet(req: GetParameterRequest): GetParameterResponse = {
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
