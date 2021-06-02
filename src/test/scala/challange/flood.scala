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
		println("Simulation is about to start!")
	}

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
		.exec(http("request_0")
			.get("/")
			.check(regex("""name="authenticity_token" type="hidden" value="(.*?)" />""").find.saveAs("authenticity_token"))
			.check(regex("""id="challenger_step_id" name="challenger\[step_id\]" type="hidden" value="(.*?)" />""").find.saveAs("challenger_step_id"))
			.check(substring("Welcome to our Script Challenge").exists)
			.headers(headers_0))
		.pause(1)
		// Step1_start
		.exec(http("request_1")
			.post("/start")
			//Regex tester
			// https://regex101.com/
			// For dynamic content in expression user regex without "()", correlations are captured when regex is enclosed with "(Regex)"
			//Escape characters like [] in expression using \ like \[\] so that it is treated as string rather than expression
			.check(regex("""name="authenticity_token" type="hidden" value="(.*?)" />""").find.saveAs("authenticity_token"))
			.check(regex("""id="challenger_step_id" name="challenger\[step_id\]" type="hidden" value="(.*?)" />""").find.saveAs("challenger_step_id"))
			.check(substring("Step 2").exists)
		.headers(headers_1)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authenticity_token}")
			.formParam("challenger[step_id]", "${challenger_step_id}")
			.formParam("challenger[step_number]", "1")
			.formParam("commit", "Start"))
		.pause(1)
		.feed(data)
		// Step2_next
		.exec(http("request_2")
			.post("/start")
			.check(regex("""name="authenticity_token" type="hidden" value="(.*?)" />""").find.saveAs("authenticity_token"))
			.check(regex("""id="challenger_step_id" name="challenger\[step_id\]" type="hidden" value="(.*?)" />""").find.saveAs("challenger_step_id"))
			.check(regex("""<label class="collection_radio_buttons" for="challenger_order_selected_.*?">(.*?)<\/label>""").findAll.saveAs("challenger_radio_num"))
			.check(regex("""name="challenger\[order_selected\]" type="radio" value="(.*?)" />""").findAll.saveAs("challenger_order_selected"))
			.check(substring("Step 3").exists)
			.headers(headers_1)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authenticity_token}")
			.formParam("challenger[step_id]", "${challenger_step_id}")
			.formParam("challenger[step_number]", "2")
			.formParam("challenger[age]", "${age}")
			.formParam("commit", "Next"))
		.pause(1)
		// Custom code to be added to capture data and process it before sending to next step
		.exec	(
			session => {
				val no_list = session("challenger_radio_num").as[List[String]];
				val radio = session("challenger_order_selected").as[List[String]];
				val max_radio_val = no_list.map(i => i.toInt).max;
				val selected_radio_session = radio(no_list.indexOf(max_radio_val.toString));

				session
					.set("max_radio_val",max_radio_val)
					.set("max_radio_session", selected_radio_session)
					.remove("challenger_radio_num")
					.remove("challenger_order_selected");
			}
		)
		// Step3_next
		.exec(http("request_3")
			.post("/start")
			.check(substring("Step 4").exists)
			.check(regex("""name="authenticity_token" type="hidden" value="(.*?)" />""").find.saveAs("authenticity_token"))
			.check(regex("""id="challenger_step_id" name="challenger\[step_id\]" type="hidden" value="(.*?)" />""").find.saveAs("challenger_step_id"))
			.check(regex("""id="challenger_order_.*" name="challenger\[order_.*\]" type="hidden" value="(.*?)" />""").findAll.saveAs("challenger_value"))
			.check(regex("""id="challenger_order_.*" name="(.*?)" type="hidden" value=".*?" />""").findAll.saveAs("challenger_order"))
			.headers(headers_1)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authenticity_token}")
			.formParam("challenger[step_id]", "${challenger_step_id}")
			.formParam("challenger[step_number]", "3")
			.formParam("challenger[largest_order]", "${max_radio_val}")
			.formParam("challenger[order_selected]", "${max_radio_session}")
			.formParam("commit", "Next"))
		.pause(1)
		// Step4_next
		.exec(http("request_4")
			.post("/start")
			.check(substring("Step 5").exists)
			.check(regex("""name="authenticity_token" type="hidden" value="(.*?)" />""").find.saveAs("authenticity_token"))
			.check(regex("""id="challenger_step_id" name="challenger\[step_id\]" type="hidden" value="(.*?)" />""").find.saveAs("challenger_step_id"))
			.headers(headers_1)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authenticity_token}")
			.formParam("challenger[step_id]", "${challenger_step_id}")
			.formParam("challenger[step_number]", "4")
			.formParam("${challenger_order(0)}", "${challenger_value(0)}")
			.formParam("${challenger_order(1)}", "${challenger_value(1)}")
			.formParam("${challenger_order(2)}", "${challenger_value(2)}")
			.formParam("${challenger_order(3)}", "${challenger_value(3)}")
			.formParam("${challenger_order(4)}", "${challenger_value(4)}")
			.formParam("${challenger_order(5)}", "${challenger_value(5)}")
			.formParam("${challenger_order(6)}", "${challenger_value(6)}")
			.formParam("${challenger_order(7)}", "${challenger_value(7)}")
			.formParam("${challenger_order(8)}", "${challenger_value(8)}")
			.formParam("${challenger_order(9)}", "${challenger_value(9)}")
			.formParam("commit", "Next")
			.resources(http("request_5")
			.get("/code")
			//Regex Path finder
			//	https://jsonpathfinder.com/ -- replace "x" with "$" from path
			.check(jsonPath("$.code").find.saveAs("code"))
			.headers(headers_5)))
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
		.exec(http("request_6")
			.post("/start")
			.check(substring("You're Done!").exists)
			.headers(headers_1)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authenticity_token}")
			.formParam("challenger[step_id]", "${challenger_step_id}")
			.formParam("challenger[step_number]", "5")
			.formParam("challenger[one_time_token]", "${code}")
			.formParam("commit", "Next"))

	//Execution Scenario
	setUp(
		scn.inject(atOnceUsers(1)),
//		Add More Flows to same executions with independent runtime Config
//		scn.inject(atOnceUsers(1))
	).protocols(httpProtocol)

	//Execute Code after end of Test
	after {
		println("Simulation is finished!")
	}

}