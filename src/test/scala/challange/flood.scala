package challange

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class flood extends Simulation {

	//DOC - https://gatling.io/docs/current/
	//Data feeder Strategies - https://gatling.io/docs/current/session/feeder/
	//Text Assertion\Correlation - https://gatling.io/docs/current/http/http_check/#http-check
	//Scenario - https://gatling.io/docs/current/general/simulation_setup/

	//Execute Code before Test
	before {

		println("Simulation is about to start! - Runtime users : " + users);
	}


	//Get dynamic value from runtime
	val users: Int = Integer.getInteger("users", 1).toInt;

	//.queue // default behavior: use an Iterator on the underlying sequence
	//.random // randomly pick an entry in the sequence
	//.shuffle // shuffle entries, then behave like queue
	//.circular // go back to the top of the sequence once the end is reached
  val data = csv("age.csv").circular;

	val httpProtocol = http
		.baseUrl("https://challenge.flood.io")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("I AM ROBOT")

	val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Origin" -> "https://challenge.flood.io",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_5 = Map(
		"Accept" -> "*/*",
		"X-Requested-With" -> "XMLHttpRequest")

	val scn = scenario("flood")
		// Launch
		.exec(FloodIO.launch)
		.pause(1)
		// Step1_start
		.exec(FloodIO.step1)
		.pause(1)
		.feed(data)
		// Step2_next
		.exec(FloodIO.step2)
		.pause(1)
		// Custom code to be added to capture data and process it before sending to next step
		.exec	(
			session => {
				Util.setChallangerRadioAndSelected(session);
			}
		)
		// Step3_next
		.exec(FloodIO.step3)
		.pause(1)
		// Step4_next
		.exec(FloodIO.step4)
		.pause(1)
		//Clear Unused correlations - to reduce heap size(Not mandatory to clear)
		.exec(
			session => {
				session
					.remove("challenger_order")
					.remove("challenger_value")
			}
		)
		// Step5_next
		.exec(FloodIO.step5)

	//Execution Scenario
	setUp(
		scn.inject(constantUsersPerSec(users) during(20)),  // Ramp-up users in 20 seconds
//		Add More Flows to same executions with independent runtime Config
//		scn.inject(atOnceUsers(1))
	).protocols(httpProtocol)
		.maxDuration(30) //hold for 30 seconds


	//Execute Code after end of Test
	after {
		println("Simulation is finished!")
	}

}


