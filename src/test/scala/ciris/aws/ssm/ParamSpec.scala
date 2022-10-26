package ciris.aws.ssm

import cats.effect.IO
import munit.CatsEffectSuite

class ParamSpec extends CatsEffectSuite {

  test("params should load an existing param") {

    val asyncMock = SsmAsyncClientMock("/sync/test/key" -> "super secret value")

    params[IO](asyncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "super secret value"))
  }

  test("params should properly handle a non-existent key") {

    val asyncMock = SsmAsyncClientMock()

    params[IO](asyncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "NOT-FOUND!"))
  }

}
