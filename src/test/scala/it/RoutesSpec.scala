package it

import cats.effect.IO
import cats.effect.kernel.Ref
import collatz.{CollatzMachineController, MachineId, RunningMachine, StartNumber}
import munit.CatsEffectSuite
import org.scalamock.scalatest.MockFactory
import http.Routes
import org.http4s.Method.POST
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{HttpApp, HttpRoutes, Request, Status}
import org.http4s.server.Router


class RoutesSpec extends CatsEffectSuite with MockFactory {

//  def httpApp(routes: HttpRoutes[IO]) = Router("/" -> routes).orNotFound
//  // Mock dependencies
//  val mockRef = mock[Ref[IO, Map[MachineId, RunningMachine[IO]]]]
//  val mockMachineManager = mock[CollatzMachineController[IO]]
//  val routes = httpApp(Routes.routes[IO](mockMachineManager))
//
//  // Create a client to make HTTP requests
//  val client = Client.fromHttpApp(routes)
//
//  test("POST /create/<id>/<starting number> should create a new machine") {
//    // Arrange: Expect machine creation to be called with the right params
//    (mockMachineManager.createAndStartMachine _).expects(MachineId("machine-1"), StartNumber(100)).returning(IO.unit)
//
//    // Act: Simulate an HTTP request to POST /create/machine-1/100
//    val request: Request[IO] = Request[IO](method = POST, uri= uri"/create/machine-1/100")
//
//    // Use client to simulate request
//    client.run(request).use { response =>
//      // Assert: Check if response status is Ok
//      IO(assertEquals(response.status, Status.Ok))
//    }
//  }
////
//  test("POST /destroy/<id> should destroy a machine") {
//    // Arrange: Expect machine destruction to be called
//    (mockMachineManager.destroyMachine _).expects("machine-1").returning(IO.unit)
//
//    // Act: Simulate an HTTP request to POST /destroy/machine-1
//    val request = POST(uri"/destroy/machine-1")
//
//    // Assert response
//    client.run(request).use { response =>
//      assertEquals(response.status, Status.Ok)
//    }
//  }
//
//  test("POST /increment/<id>/<amount> should increment a machine") {
//    // Arrange: Expect machine increment to be called with correct params
//    (mockMachineManager.incrementMachine _).expects("machine-1", 5L).returning(IO.unit)
//
//    // Act: Simulate an HTTP request to POST /increment/machine-1/5
//    val request = POST(uri"/increment/machine-1/5")
//
//    // Assert response
//    client.run(request).use { response =>
//      assertEquals(response.status, Status.Ok)
//    }
//  }

  // Add more tests for other routes like GET /messages, POST /destroy, etc.
}
