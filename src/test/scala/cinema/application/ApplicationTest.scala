package cinema.application

import cinema.application.test.Application
import org.scalatest.WordSpecLike

class ApplicationTest extends WordSpecLike  {

  "Application" must {
    "run" in {
      Application.main(null)
    }
  }
}