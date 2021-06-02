package challange

import io.gatling.core.session.Session

object Util {


  def setChallangerRadioAndSelected(session: Session) = {

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
}
