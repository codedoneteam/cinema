package cinema.config

import org.scalatest.WordSpecLike

import scala.language.postfixOps

class NormalizeTest extends WordSpecLike with NormalizeAware {

  "Normalize" must {
    "normalize path" in {
      assert(normalizePath("d") == "d")
      assert(normalizePath("TestOne") == "test-one")
    }

    "normalize key" in {
      assert(normalizeKey("d") == "d")
      assert(normalizeKey("test-one") == "testOne")
    }
  }
}