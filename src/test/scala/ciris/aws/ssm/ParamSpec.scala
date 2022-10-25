package ciris.aws.ssm

import cats.effect.IO
import munit.CatsEffectSuite

class ParamSpec extends CatsEffectSuite {

  test("params (sync) should load an existing param") {

    val syncMock = SsmClientMock("/sync/test/key" -> "super secret value")

    params[IO](syncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "super secret value"))
  }

  test("params (sync) should properly handle a non-existent key") {

    val syncMock = new SsmClientMock()

    params[IO](syncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "NOT-FOUND!"))
  }


  test("params (async) should load an existing param") {

    val asyncMock = SsmAsyncClientMock("/sync/test/key" -> "super secret value")

    paramsAsync[IO](asyncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "super secret value"))
  }

  test("params (async) should properly handle a non-existent key") {

    val asyncMock = SsmAsyncClientMock()

    paramsAsync[IO](asyncMock)
      .flatMap { param =>
        param("/sync/test/key").default("NOT-FOUND!")
      }
      .load
      .map(value => assertEquals(value, "NOT-FOUND!"))
  }


}
